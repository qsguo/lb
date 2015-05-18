package loadbalance;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.distribution.MixtureMultivariateNormalDistribution;
import org.apache.commons.math3.distribution.MixtureMultivariateRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.exception.MathArithmeticException;

public class Imbalance {


  public Imbalance() {
  }

  public static int nextInt(Random rand) {
//      Random rand = new Random();
      double prob = rand.nextGaussian();
      
      return (int) Math.ceil(100*prob);
  }

  public String[] parseLine(String str) {
    String delims = "[ ]+";
    String[] wordArray = str.split(delims);
    
    return wordArray;
  }
  
  public String[] parseWords(String str) {
    String delimiter = "=";
    String[] words = str.split(delimiter);
    
    return words;
  }

  public static void main(String[] args) throws IOException {
    //initialize stream map
    int streams = 100, nodes = 15;
    double threshold=10000;
    
    Map<Integer, String> sMap = new HashMap<Integer, String>(streams);
    for (int col = 1; col <= streams; col++) {
      sMap.put(col, String.valueOf(col));
    }
    
    Imbalance tester = new Imbalance();
    Statistics st = new Statistics(sMap);
    
    
    //read lines
    String path = "/home/qingsongguo/workspace/testing.txt";
    FileReader infile = new FileReader(path);
    BufferedReader bfr = new BufferedReader(infile);

    Map<String, Integer> histogram = new HashMap<String, Integer>(streams);
    String line = bfr.readLine();
    
    while (line != null) {
      String[] stringArray = tester.parseLine(line);
      
      String key=null;
      int value = 0;
      
      for (String str : stringArray) {
        String[] words = tester.parseWords(str); 
        
        key = words[0];
        value = Integer.valueOf(words[1]);
        histogram.put(key, value);
      }
//      System.out.println("histogram: "+histogram);
      st.appendHistogram(histogram);   // append histogram to covariance matrix of object st
      histogram.clear();
      
      line = bfr.readLine();
    }
      st.getMatrix();
      st.computeCovMatrix();
    
      Cslb balancer = new Cslb(sMap, st.getCovarianceMatrix(), threshold, nodes);
      Map<String, Set<String>> plan = balancer.makePlan();
      balancer.covSum();
      balancer.computeCovariance(plan);
    
  }
}




