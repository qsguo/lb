package loadbalance;

import org.apache.commons.math3.geometry.partitioning.Side;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;



public class Cslb {
//  private static RealMatrix freqMatrix = null;
  private RealMatrix covarianceMatrix = null;
  private Map<Integer, String> plan = null;
//  private Collection<String> streamSet = null;
  private Map<Integer, String> streamMap = null;
  private double theta = 0;  // a threshold
  private int dimension = 0; // dimension for covariance matrix
  private int nodeNum =  0;
  
  
  public Cslb(Map<Integer, String> smap, RealMatrix covariances, double threshold, int number) {
    streamMap = smap;
    theta = threshold;
    dimension = covariances.getColumnDimension();
    nodeNum = number;
    
    covarianceMatrix = covariances;
  }
  
  public Cslb(Map<Integer, String> smap, double[][] frequencies, double threshold, int number) {
    streamMap = smap;
    theta = threshold;
    dimension = smap.size();
    nodeNum = number;
    
    Statistics stat = new Statistics(smap, frequencies);
    covarianceMatrix = stat.getCovarianceMatrix();
  }
  
  /*
   * Making plan
   */
  public Map<String, Set<String>> makePlan() {
    PriorityQueue<Entry<String, Double>> pq = this.computeCovarianceSum();
    DisjointSet disjointSet = new DisjointSet(new HashSet<String>(streamMap.values()));
    
    int setNum = 1, index=0;
    double[] covArray = null;
    String curSid = null;
    
    while (!pq.isEmpty() && setNum< nodeNum) {
      curSid = pq.poll().getKey();
      System.out.println("curSid: "+curSid);
      index = getIndexBySid(curSid);
      
      covArray = covarianceMatrix.getColumn(index);
      Object[] formerSet = disjointSet.getSet(curSid).toArray();
     
      
//      System.out.println("curSid: "+curSid);
      disjointSet.split(curSid); // move itself to a new set
      setNum++;
      
      for (Object object : formerSet) {
        String sid = object.toString();
        
        double value = covArray[this.getIndexBySid(sid)];
//        System.out.println(curSid+" "+sid+" covariance: "+value);
        
        if (value < theta) {
          disjointSet.moveElement(sid, curSid);
          
        }
      }
    }
    
    System.out.println("--plan: "+ disjointSet.getPlan());
    
    return disjointSet.getPlan();
  }
  
  public double covSum() {
    double sum = 0;
    for (int i=0; i<dimension; i++) {
      double colsum = 0;
      for (double d : covarianceMatrix.getColumn(i)) {
        colsum += d;
      }
      sum += colsum;
      System.out.println(colsum);
    }
    sum = sum/2.0;
    System.out.println(sum);
    
    return sum;
  }
  
  public double matrixSum(RealMatrix realMatrix) {
    double sum = 0;
    for (int i=0; i<dimension; i++) {
      for (double d : realMatrix.getColumn(i)) {
        sum += d;
      }
    }
    sum = sum/2.0;
//    System.out.println(sum);
    
    return sum;
  }
  
  
  public double computeCovariance(Map<String, Set<String>> plan) {
    double sum=0;
    Set<String> keySet = plan.keySet();
    
    Iterator<String> iterator = keySet.iterator();
    Set<String> elemeSet = null;
    
    while (iterator.hasNext()) {
      elemeSet = plan.get(iterator.next());
      
      int col=0, row=0;
      Object[] objects = elemeSet.toArray();
      for (int i=0; i<objects.length; i++) {
        row = this.getIndexBySid(objects[i].toString());
        double colsum = 0;
        for (int j = 0; j < objects.length; j++) {
          col = this.getIndexBySid(objects[j].toString());
          colsum += covarianceMatrix.getEntry(row, col);
        }
//        System.out.println(colsum);
        sum += colsum;
      }
    }
    sum = sum/2.0;
    System.out.println(sum);
    
    return sum;
    
  }
  
  
  public Map<String, Integer> djSet2Map() {
    // TODO Auto-generated method stub
    return null;
  }

  //column to sid
  public String getSidByIndex(int index) {
    return String.valueOf(index+1);
  }
  
  //sid to column
  public int getIndexBySid(String sid) {
    return (Integer.valueOf(sid) - 1 );
  }
  
  private PriorityQueue<Entry<String,Double>> computeCovarianceSum() {
    PriorityQueue<Entry<String, Double>> pQueue = new PriorityQueue<Entry<String,Double>>(dimension, new Comparator<Entry<String,Double>>() {

      @Override
      public int compare(Entry<String,Double> e1, Entry<String,Double> e2) {
        // TODO Auto-generated method stub 
        if (e1.getValue() < e2.getValue()) {
          return +1;
        }
        else {
          return -1;
        }
      }
      
    });
    
    
    //push the <Sid, covSum> into the priority queue
    for (int index = 0; index < dimension; index++) {
      Map.Entry<String, Double> entry = new covSumEntry<String, Double>(this.getSidByIndex(index), covarianceSum(index));
      pQueue.add(entry);
    } 
    
    return pQueue;
  }
  
  
  private double covarianceSum(int index) {
    double covSum = 0;
    if (index>= dimension) {
      System.out.println("out of range "+index);
      return -1.0;
    }
    else {
      double[] series = covarianceMatrix.getColumn(index);
      
      double cov = 0;
      for (int i = 0; i < dimension; i++) {
        cov = series[i];
        
        if (i != dimension && cov > theta) {
          covSum += series[i];
        }
        else {
          continue;
        }
      }
      
      return covSum;
    }
    
  }
  

  //for testing
  public static void main(String[] args ) {
    int numberOfStreams = 3;
    double[][] fm = {{4.0,2.0,0.60},{4.2,2.1,0.59},{3.9,2.0,0.58},{4.3,2.1,0.62},{4.1,2.2,0.63}};
    //The Covariance Matrix should be {{0.025,0.0075,0.00175},{0.0075,0.007,0.00135}, {0.00175,0.00135,0.00043}}
    
    Map<Integer, String> sMap = new HashMap<Integer, String>(3);
    for (int col = 0; col < 3; col++) {
      sMap.put(col, String.valueOf(col));
    }
    
//    Cslb cslb = new Cslb(sMap,fm,0,2);
//    cslb.makePlan();
    
    System.out.println("starting 1");
    Statistics st = new Statistics(sMap,fm);
    Cslb cslb2 = new Cslb(sMap, st.getCovarianceMatrix(), 0, 2);
    cslb2.makePlan();
  }
  
  /*
   * Inner class defined an entry for recording <sid, covSum>
   */
  private static class covSumEntry<K, V> implements Map.Entry<K, V> {
    private K key;
    private V value;
    
    public covSumEntry(K key, V value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public K getKey() {
      return this.key;
    }

    @Override
    public V getValue() {
      return this.value;
    }

    @Override
    public V setValue(V value) {
      this.value = value;
      return value;
    }
    
  }
  

}
