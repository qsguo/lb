package loadbalance;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class DisjointSet {
    private HashMap<String, Set<String>> disjointSet=null;
    private String str = "missing";
 
    public DisjointSet(){
        disjointSet = new HashMap<String, Set<String>>();
    }
    
    public DisjointSet(Set<String> set){
      disjointSet = new HashMap<String, Set<String>>();
      List<String> sortedList = new ArrayList<String>(set);
      Collections.sort(sortedList);
      disjointSet.put(sortedList.get(0), set);
  }
 
    public void createSet(String element){
    	if (!this.findSet(element).equals(str))  //element is already in DisjointSet
			return;
		
        Set<String> set = new HashSet<String>();
        set.add(element);
        disjointSet.put(element, set);
    }
 
    public Map<String, Set<String>> getDisjointSet() {
      return disjointSet;
    }
    
    //get the set and return its representative
    public String findSet(String element){
        for (Map.Entry<String, Set<String>> entry : disjointSet.entrySet()){
            if (entry.getValue().contains(element)) {
              return entry.getKey();
            }
            else {
              continue;
            }
        }
        
        return str;
    }
    
    
    public Map<String, Set<String>> getPlan() {
      HashMap<String, Set<String>> plan = (HashMap<String, Set<String>>) disjointSet.clone();
      return plan;
    }
    
    
    public Set<String> getSet(String element){
      String rep = findSet(element);
      
      if (rep.equals(str)) {
        return null;
      }
      else {
        return disjointSet.get(rep);
      }
    }
    
    
    public Set<String> getRepresentives(){
        return disjointSet.keySet();
    }
    
    public Collection<Set<String>> getAllSets(int idx){
        return disjointSet.values();
    }
 
    public int getSize(){
        return disjointSet.size();
    }
    
    
    public void printAllSets() {
      System.out.println("-- printing -- " + disjointSet.keySet().size()+" "+disjointSet.values().size());
      for (String key : disjointSet.keySet()) {
        System.out.println(key +" : " +disjointSet.get(key));
      }
      
      return ;
    }
    
    
    public boolean split(String element){
//      System.out.println(this.getSet(element));
      if (getSet(element).size()==1) {  // the set has only one element
        return true;
      }
      else {
        String rep = findSet(element);
//        System.out.println("splitting: "+ element + " from " + rep);
        
        if (rep.equals(str)) {
          System.out.println(rep+ " " +str + "this element is not exited");
          return false;
        }
        
        
        Set<String> set = disjointSet.get(rep);
        set.remove(element);
        
        
        if (element.equals(rep)) { //element itself the representative
          disjointSet.remove(element); // remove this entry as we should update its representative
          List<String> sortedList = new ArrayList<String>(set);
          Collections.sort(sortedList);
          rep = sortedList.get(0); //find a new representative for this set
          
          disjointSet.put(rep, set);
        }
        
        createSet(element);
        System.out.println(disjointSet.keySet());
        
        return true;
      }
    }    
    
    
    public int union(String firstElement, String secondElement){
        String firstRep = findSet(firstElement);
        String secondRep = findSet(secondElement);
        
        if (firstRep.equals(secondRep)) { // no need of union
          return 1;
        }
 
        Set<String> firstSet = disjointSet.get(firstRep);
        Set<String> secondSet = disjointSet.get(secondRep);
 
        if (firstSet != null && secondSet != null){
          if (firstRep.compareTo(secondRep)<=0) {
            firstSet.addAll(secondSet);
            disjointSet.remove(secondRep);
          }
          else {
            secondSet.addAll(firstSet);
            disjointSet.remove(firstRep);
          }
        }
 
        return 1;
    }

    
    
    public boolean moveElement(String firstElement, String secondElement){
        String rep1 = findSet(firstElement);
        System.out.println("moving: "+ firstElement + " " + rep1 + " -> "+ secondElement);
        
        if (rep1.equals(str)) {
//          System.out.println(rep1+" "+str+" this element is not exited");
          return false;
        }
        else {
          String rep2 = findSet(secondElement);
          if (rep2.equals(rep1)) { // if firstElement and secondElement locate in the same set, there is no need of movement
            return true;
          }
          
          
          if (rep2.equals(str)) {
            createSet(secondElement);
            rep2 = secondElement;
//            System.out.println("-- rep2 -- "+ rep2);
          }
          
          Set<String> set1 = getSet(firstElement), set2 = getSet(secondElement);
//          System.out.println("set1------"+ set1);
          set2.add(firstElement);
          set1.remove(firstElement);
          if (set1.isEmpty()) { //if set1 is empty, then remove the corresponding entry from map
            disjointSet.remove(rep1);
            return true;
          }
          
          
          if (rep1.equals(firstElement)) { // should change the representative for set1
            List<String> sortedList = new ArrayList<String>(set1);
            Collections.sort(sortedList);
//            System.out.println("sortedList------"+ sortedList);
            String newRep = sortedList.get(0);
            
            disjointSet.remove(rep1);
            disjointSet.put(newRep, set1);
//            System.out.println("new rep: " + findSet(newRep));
//            System.out.println("rep1: " + rep1);
          }
          
          if (firstElement.compareTo(rep2) < 0) {
            disjointSet.put(firstElement, set2); //element is the new representative for set2
            disjointSet.remove(rep2);
          }
       System.out.println(disjointSet.keySet());   
       return true;
       }
    }
    
    

 
    public static void main(String... arg){
        DisjointSet disjointSet = new DisjointSet();
 
        for (int i = 1; i <= 5; i++){
            disjointSet.createSet(String.valueOf(i));
        }
 
        System.out.println("ELEMENT : REPRESENTATIVE KEY");
        for (int i = 1; i <= 5; i++){
            System.out.println(i + "\t:\t" + disjointSet.findSet(String.valueOf(i)));
        } 
 
        System.out.println("the set for 10 : "+ disjointSet.findSet(String.valueOf(10)));
        disjointSet.union("1", "10");
        System.out.println("the set for 1 : "+ disjointSet.findSet("1"));
        System.out.println("The number of disjoint set after a union : "+ disjointSet.getSize());
        System.out.println("Performing unions ");
        disjointSet.union("1", "2");
        disjointSet.union("1", "3");
        disjointSet.union("1", "4");
        disjointSet.union("1", "5");
        System.out.println("The number of disjoint set after a union: "+ disjointSet.getSize());
 
 
        System.out.println("ELEMENT : REPRESENTATIVE KEY");
        for (int i = 1; i <= 5; i++){
            System.out.println(i + "\t:\t" + disjointSet.findSet(String.valueOf(i)));
        }
 
        System.out.println("\n set : "+ disjointSet.getSet("1"));
        
        disjointSet.moveElement("1", "6");
        System.out.println("ELEMENT : REPRESENTATIVE KEY");
        for (int i = 1; i <= 6; i++){
            System.out.println(i + "\t:\t" + disjointSet.findSet(String.valueOf(i)));
        }
        
        System.out.println("after the movement : "+ disjointSet.getSet("5"));
        System.out.println("set of 1 : "+ disjointSet.getSet("1"));
        
        disjointSet.split("1");
        System.out.println("set of 1 : "+ disjointSet.getSet("1"));
        System.out.println("set of 6 : "+ disjointSet.getSet("6"));
        
        disjointSet.printAllSets();
        
//        Iterator<Entry<String, Set<String>>> iterator = disjointSet.getDisjointSet().entrySet().iterator();
//        while (iterator.hasNext()) {
//          System.out.println(iterator.next());
//        }
    }
}
