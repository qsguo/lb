package loadbalance;

import java.io.BufferedReader;
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

public class Tests {


  public Tests() {
  }

  public static int nextInt(Random rand) {
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
    int streams = 100, nodes = 10;
//    double threshold=10;
    double threshold=0;
    
    Map<Integer, String> sMap = new HashMap<Integer, String>(streams);
    for (int col = 1; col <= streams; col++) {
      sMap.put(col, String.valueOf(col));
    }
    
    Tests tester = new Tests();
    Statistics st = new Statistics(sMap);
    
    
    //read lines
//    String path = "/home/qingsongguo/workspace/s1.txt";
//    String path = "/home/qingsongguo/workspace/s2.txt";
    String path = "/home/qingsongguo/workspace/tw.txt";
    FileReader infile = new FileReader(path);
    BufferedReader bfReader = new BufferedReader(infile);

    Map<String, Integer> histogram = new HashMap<String, Integer>(streams);
    String line = bfReader.readLine();
    int count = 1;
    
    while (line != null && count<=120) { //7200
      String[] stringArray = tester.parseLine(line);
      
      String key=null;
      int value = 0;
      
      for (String str : stringArray) {
        String[] words = tester.parseWords(str); 
        
        key = words[0];
        value = Integer.valueOf(words[1]);
        histogram.put(key, value);
      }
      st.appendHistogram(histogram);   // append histogram to covariance matrix of object st
      histogram.clear();
      
      line = bfReader.readLine();
      count++;
    }
    
    st.getMatrix();
    st.computeCovMatrix();
  
    Cslb balancer = new Cslb(sMap, st.getCovarianceMatrix(), threshold, nodes);
    Map<String, Set<String>> plan = balancer.makePlan();

    double[] vars = st.calculateLI(plan);
    int index = 1;
    for (double d : vars) {
      System.out.println(d);
    }
    
//      balancer.covSum();
//      balancer.computeCovariance(plan);
      
    
  }
}




