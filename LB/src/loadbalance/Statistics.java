package loadbalance;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealMatrixChangingVisitor;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Variance;


public class Statistics {
  private Vector<RealVector> frequencies = null;
  private Map<Integer, String> streamMap = null;
  private RealMatrix matrix = null, covMatrix = null;  //matrix(hNum,sNum): column(series) -- row(histogram)
  private int hNum = 0, sNum = 0;
  
  public Statistics( Map<Integer, String> smap) {
    streamMap = smap;
    frequencies = new Vector<RealVector>(10);
//    hNum = value;
    sNum = smap.size();
    System.out.println("parameters: "+ hNum +" "+sNum + " ");
  }
  
  public Statistics(Map<Integer, String> smap, double[][] fm) {
    streamMap = smap;
    hNum = fm.length;   //number of histograms
    sNum = fm[0].length; //number of streams
    frequencies = new Vector<RealVector>(hNum);
    
    for (double[] ds : fm) {
      frequencies.add(new ArrayRealVector(ds));
    }
        
    setMatrix();
    computeCovMatrix();
  }
//  

  //get index for a given sid
  public int getIndexBySid(String sid) {
    for (Entry<Integer, String> entry : streamMap.entrySet()) {
      if (entry.getValue()==sid) {
        return entry.getKey();
      }
      else {
        continue;
      }
    }
    return -1;
  }
  
  //get sid for a given index
  public String getSidByIndex(int index) {
    return streamMap.get(index);
  }
  
  //frequency vectors -> matrix, a column in matrix records the statistics for a stream
  public int setMatrix() {
    matrix = new BlockRealMatrix(hNum, sNum);
    
    int idx = 0;
    Iterator<RealVector> iter = frequencies.iterator();
    while (iter.hasNext()) {
      matrix.setRowVector(idx++, iter.next());
    }
    
    return 1;
  }
  
  //matrix -> covariance matrix
  public RealMatrix computeCovMatrix() {
    covMatrix = new Covariance(matrix.getData()).getCovarianceMatrix();
    return covMatrix;
  }

  public RealMatrix getMatrix() {
    this.setMatrix();
    return matrix;
  }
  
  public Collection<String> getStreamSet() {
    return streamMap.values();
  }
  
  public RealMatrix getCovarianceMatrix() {
    return covMatrix;
  }
  
  public int getNumberOfHistogram() {
    return hNum;
  }
  
  public int getNumberOfStreams() {
    return sNum;
  }
  /*
   * append a histogram as the last vector of frequencies
   */
  public int appendHistogram(int[] histogram) {
    int num = this.getNumberOfStreams();
    
    if ( histogram.length == num) {
      RealVector vector = new ArrayRealVector(num);
      frequencies.add(vector);
      hNum++;  //length increase with 1
      return hNum;
    }
    else return -1;
  }
  
  
  public int appendHistogram(Map<String, Integer> data) {
    ArrayRealVector histogram = new ArrayRealVector(sNum);
    String sid = null;
    int index=1;
    
    for ( ; index<=sNum; index++) {
      sid = streamMap.get(index);
      
      if (data.get(sid)!=null) {
        histogram.addToEntry(index-1, data.get(sid).intValue());
        data.remove(sid);
      }
      else {
        histogram.append(0); //set the frequency for this stream as zero
      }
    }
    
//    if (!data.isEmpty()) { // record the information in histogram and streams (Map) for the new streams
//      for (Map.Entry<String, Integer> entry : data.entrySet()) {
//        streamMap.put(index++, entry.getKey());
//        histogram.append(entry.getValue());
//      }
//    }
//    
//    fillZero(histogram.getDimension()-sNum); //fill zeros to frequencies
    frequencies.add(histogram);  // add as the latest histogram
//    System.out.println("histogram: "+histogram);
    
    return ++hNum;
    
  }
  
  
  private int fillZero(int num) {
    if (num==0) {
      return 1;
    }
    else {
      Iterator<RealVector> iterator = frequencies.iterator();
      RealVector vector = null;
      while (iterator.hasNext()) {
        vector = iterator.next();
        for (int i = 0; i < num; i++) {
          vector.append(0);
        }
      }
      return 1;
    }
  }

  
  public RealVector getHistogramByIndex(int index) {
    if ( index < 0 || index > hNum-1 ) { //index is out of the current range [0,length-1]
      return null;
    }
    else {
      return frequencies.get(index);
    }
  }
  
  
  public RealVector getHistogramBySid(String sid) {
    return this.getHistogramByIndex(this.getIndexBySid(sid));
  }
  
  
  
  public RealVector getStatisticByIndex(int index) {
    RealVector statistics = new ArrayRealVector(hNum);
    
    Iterator<RealVector> iterator = frequencies.iterator();
    while (iterator.hasNext()) {
      statistics.append(iterator.next().getEntry(index));
    }
    
    return statistics;
  }
  
  public RealVector getStatisticBySid(String sid) {
    int index = this.getIndexBySid(sid);
    RealVector statistics = new ArrayRealVector(hNum);
    
    Iterator<RealVector> iterator = frequencies.iterator();
    while (iterator.hasNext()) {
      statistics.append(iterator.next().getEntry(index));
    }
    
    return statistics;
  }
  
  public RealVector aggregateRows(int start, int length, RealMatrix matrix) {
    RealVector row = new ArrayRealVector(matrix.getColumnDimension());
    
    for (int cur=start; cur<start+length; cur++) {
      row = row.add(matrix.getRowVector(cur));
      if (cur==7200) {
        return row;
      }
      else continue;
    }

    return row;
  }
  
  //return the frequencies for a set of streams
  public RealVector aggregateColumn(Set<String> streams) {
    RealVector series = new ArrayRealVector(120);
    
    for (String stream : streams) {
      int sid = Integer.valueOf(stream)-1;
//      System.out.println("rows"+matrix.getColumnVector(sid).getDimension());
      series = series.add(matrix.getColumnVector(sid));
    }

    return series;
  }
  
  
  public RealMatrix getLoadMatrix(Map<String, Set<String>> plan){
    RealMatrix tempMatrix = new BlockRealMatrix(120, plan.size());
//    RealMatrix loadMatrix = new BlockRealMatrix(120, plan.size());
    RealVector colVector = null;
    RealVector rowVector = null;
    
//    System.out.println("matrix: "+matrix);
    
    int colIndex = 0, rowIndex = 0;
    for (Entry<String, Set<String>> sid : plan.entrySet()) {
      colVector = aggregateColumn(sid.getValue());
      tempMatrix.setColumnVector(colIndex++, colVector);
    }
    
//    int runs = 120;
//    for (int i = 0; i < runs; i++) {
//      int start = i * 60;
//      rowVector = aggregateRows(start, 60, tempMatrix);
//      loadMatrix.setRowVector(rowIndex++, rowVector);
//    }
//    
//    return loadMatrix;
    return tempMatrix;
  }
  
  public double[] calculateLI(Map<String, Set<String>> plan) {
    RealMatrix loadMatrix = getLoadMatrix(plan);
//    System.out.println("loadMatrix: "+loadMatrix);
    for (int i = 0; i < 120; i++) {
      double[] array = loadMatrix.getRowVector(i).toArray();
      for (double d : array) {
        System.out.print(d+" ");
      }
      System.out.print("\n");
    }
    
    int size = loadMatrix.getRowDimension();
    double[] vars = new double[size];
    
    Variance varCalculator = new Variance();
    for (int i = 0; i < size; i++) {
      vars[i] = varCalculator.evaluate(loadMatrix.getRow(i));
//      System.out.println(vars[i]);
    }
    
    return vars;
  }
  
}



