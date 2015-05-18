package loadbalance;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.distribution.ZipfDistribution;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class Synthetizer {
//  Vector<String> data = null;
  
  public Synthetizer( ) {
//    data = new Vector<String>(num);
  }
  
 
  
  private int writeData(String path, Vector<String> vector) throws IOException{
    FileWriter fout = new FileWriter(path);
    
    Iterator<String> iterator = vector.iterator();
    while (iterator.hasNext()) {
      fout.append(iterator.next());
    }
    
    fout.close();
    
    return 1;
  }
  
  
  public Vector<String> generateZipfStream(double exponent, int num)  {
    ZipfDistribution tsZipf = new ZipfDistribution(100,1.3655999999);
    ZipfDistribution zipf = new ZipfDistribution(10000, exponent);
    NormalDistribution normal =  new NormalDistribution(5001, 1667);
    
    Vector<String> data = new Vector<String>(num);
    long timestamp = 0;
    int iat = 0;
//    System.out.println(tsZipf.getNumericalMean());
    
    int count=0;
    while (count<num) {
      iat = tsZipf.sample();
      timestamp += iat;
      
      String tuple = String.valueOf(timestamp)+" "+ String.valueOf(zipf.sample())+ " "+ String.valueOf((int) normal.sample())+"\n";
      data.add(tuple);
      count++;
    }
    
    System.out.println(timestamp);
    return data;
  }
  
  
  public Vector<String> generatePoissonStream(double lambda, int num)  {
    PoissonDistribution poisson = new PoissonDistribution(lambda);
    NormalDistribution normal =  new NormalDistribution(49.5, 16.5);
    
    Vector<String> data = new Vector<String>(num);
    long timestamp = 0;
    int iat = 0;
    
    int count=0;
    while (count<num) {
      iat = poisson.sample();
      timestamp += iat;
      
      String tuple = String.valueOf(timestamp)+" "+ String.valueOf((int) normal.sample())+"\n";
      data.add(tuple);
      count++;
    }
    
    System.out.println(timestamp);
    return data;
  }
  
  
  public Vector<String> generatePoissonStream(double lambda, double exponent, int num)  {
    PoissonDistribution poisson = new PoissonDistribution(lambda);
//    ZipfDistribution zipf = new ZipfDistribution(10000, exponent);
    NormalDistribution normal =  new NormalDistribution(5001, 1667);
    
    Vector<String> data = new Vector<String>(num);
    long timestamp = 0;
    int iat = 0, step=1;
    int number=0;
    
    int count=0;
    while (count<num) {
      iat = poisson.sample();
      timestamp += iat;

      if ((timestamp/10000) < step) {
        number++;
      }
      else {
        System.out.println(number);
        number=1;
        step++;
      }
      count++;
      
//      String tuple = String.valueOf(timestamp)+" "+ String.valueOf(zipf.sample())+ " "+ String.valueOf((int) normal.sample())+"\n";
//      data.add(tuple);
//      count++;
    }
    
//    System.out.println(timestamp);
    return data;
  }
  
  public Vector<Integer> generateZipfData(double exponent, int num)  {
    ZipfDistribution zipf = new ZipfDistribution(1000, exponent);
    
    Vector<Integer> data = new Vector<Integer>(num);
    
    int count=0, sample=0;
    while (count<num) {
      sample = 10*zipf.sample();
      data.add(sample);
      System.out.println(sample);
      count++;
    }
    
    return data;
  }
  
  
  public int generatePoissonData(double lambda, int num, FileWriter ofile) throws IOException  {
    PoissonDistribution poisson = new PoissonDistribution(lambda);
    
    Vector<Integer> numbers = new Vector<Integer>(num);
    Map<Integer, Integer> histogram = null;
    
    int count=0, sample=0;
    while (count<num) {
      sample = 200*poisson.sample();
      numbers.add(sample);
      System.out.println(sample);
      count++;
    }
//    ofile.write("write sth");
    
//    Vector<Integer> data = new Vector<Integer>(num);
    int occurence = 0;
    Iterator<Integer> iterator = numbers.iterator();
    while (iterator.hasNext()) {
      occurence = iterator.next();
      histogram = generateNormalData(occurence);
      
      for (Map.Entry<Integer, Integer> entry : histogram.entrySet()) {
        ofile.write(entry.toString()+" ");
//        ofile.write(String.valueOf(entry.getKey())+":" + String.valueOf(entry.getValue()));
      }
      ofile.write("\n");
    }
    
    return 1;
  }
  
  
  
  public Map<Integer, Integer> generateNormalData(int num)  {
    NormalDistribution normal = new NormalDistribution(49.5, 16.5);
    Map<Integer, Integer> histogram = new HashMap<Integer, Integer>(100);
    
    int count=0, sample=0, key;
    while (count<num) {
      sample = (int) Math.ceil(normal.sample());
      if (sample>0 && sample <= 100) {
        key = computeKey(sample);
        if (histogram.containsKey(key)) {
          histogram.put(key, histogram.get(key).intValue()+1); //increase the occurrence with 1
        }
        else {
          histogram.put(key, 1);  //the first occurrence of this key
        }
        count++;
      }
      else {
        continue;
      }
    }
    
    return histogram;
  }
  
  private int computeKey(int input) {
    int key = input;
    return key;
  }
  
  public static void main(String[] args) throws IOException {
    String path1 = "/home/qingsongguo/workspace/testing.txt";
//    String path2 = "/home/qingsongguo/workspace/d2.txt";
    
    
    
//    FileWriter f1 = new FileWriter(path1);
//    int number = 7200;
//    Synthetizer synthetizer = new Synthetizer();
//    synthetizer.generatePoissonData(50, 7200, f1);
//    f1.close();
    
    ZipfDistribution zipf = new ZipfDistribution(100,1.3655999999);
    System.out.println(zipf.getNumericalMean());
    System.out.println(zipf.getNumericalVariance());
    
//    Map<Integer, Integer> map = synthetizer.generateNormalData(number); 
//    
//    
//    for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
//      System.out.print(entry.toString()+" ");
//    }
//    System.out.print("\n");
    
  }
  
  
//  public static void main(String[] args) throws IOException {
//    String path1 = "/home/qingsongguo/workspace/d1.txt";
//    String path2 = "/home/qingsongguo/workspace/d2.txt";
//    
//    int number = 10000;
//    Synthetizer synthetizer = new Synthetizer();
//    
//    
//    Vector<String> d1 = synthetizer.generateZipfStream(1.765, number);
//    synthetizer.writeData(path1, d1);
//    
//    Vector<String> d2 = synthetizer.generatePoissonStream(10, 1.765, number);
//    synthetizer.writeData(path2, d2);
//    
//    
////    ZipfDistribution tsZipf = new ZipfDistribution(100,1.3655999999);
////    System.out.println(tsZipf.getNumericalMean());
//  }
}



