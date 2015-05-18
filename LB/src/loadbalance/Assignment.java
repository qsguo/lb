package loadbalance;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Assignment {
  private static Map<Integer, String> assignment = null;
  
  public Assignment() {
    // TODO Auto-generated constructor stub
  }
  
  public Assignment(Map<Integer, String> plan) {
    assignment = plan;
  }
  
  public static Set<Integer> getKeySubset(String value) {
    Set<Integer> keySubset = new HashSet<Integer>();
    
    for (Entry<Integer, String> entry : assignment.entrySet()) {
      if (entry.getValue() == value) {
        keySubset.add( entry.getKey());
      }
      else continue;
    }
        
    return keySubset;
  }
  
  
  public boolean check(int num) {
    if (assignment.size() == num) {
      return true;
    }
    else {
      System.out.println("The assignment is incorrect");
      return false;
    }
  }
  
  public static boolean addEntry(int key, String value) {
    
    if (assignment.containsKey(key)) {
      System.out.println("this entry is invalid as the key is present in the assignment");
      return false;
    }
    else {
      assignment.put(key, value);
      return true; 
    }
  }
  
//  public static void main(String[] args) {
//    assignment.put(1, 3);
//    assignment.put(2, 2);
//    assignment.put(3, 3);
//    
//    
//    System.out.println(Assignment.getKeySubset(3));
//  }
  
}
