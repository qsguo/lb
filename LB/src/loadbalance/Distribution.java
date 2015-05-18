package loadbalance;

public class Distribution {
  
  public Distribution() {
    // TODO Auto-generated constructor stub
  }
  
  public int dataParser(String str) {
    
    String delims = "[ ]+";
    str.split(delims);
    
    return 1;
  }
  
  public void main() {
    String str = "xxx yyy";
    
    Distribution tester = new Distribution();
    tester.dataParser(str);
    
    System.out.println(str);
    
  }

}
