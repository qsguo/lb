package loadbalance;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
 
public class DisjointSets {
    private List<Map<Integer, Set<Integer>>> disjointSet=null;
 
    public DisjointSets(){
        disjointSet = new ArrayList<Map<Integer, Set<Integer>>>();
    }
    
    public DisjointSets(Set<Integer> set){
      disjointSet = new ArrayList<Map<Integer, Set<Integer>>>();
      List<Integer> sortedList = new ArrayList<>(set);
      Collections.sort(sortedList);
      Map<Integer, Set<Integer>> map = null;
      map.put(sortedList.get(0), set);
      disjointSet.add(map);
  }
 
    public void createSet(int element){
    	if (this.findSet(element)!=-1)  //element is already in DisjointSet
			return;
		
        Map<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();
        Set<Integer> set = new HashSet<Integer>();
        set.add(element);
        map.put(element, set);
        disjointSet.add(map);
    }
 
    public void union(int first, int second){
        int first_rep = findSet(first);
        int second_rep = findSet(second);
 
        Set<Integer> first_set = null, second_set = null;
 
        for (int index = 0; index < disjointSet.size(); index++){
            Map<Integer, Set<Integer>> map = disjointSet.get(index);
            if (map.containsKey(first_rep)){
                first_set = map.get(first_rep);
            } 
            else if (map.containsKey(second_rep)){ 
                second_set = map.get(second_rep);
            }
        }
 
        if (first_set != null && second_set != null){
        	first_set.addAll(second_set);
        }
        
        int newRep = (first_rep < second_rep) ? first_rep : second_rep;
 
        for (int index = 0; index < disjointSet.size(); index++){
            Map<Integer, Set<Integer>> map = disjointSet.get(index);
            if (map.containsKey(first_rep)){
                map.put(newRep, first_set);
            } 
            else if (map.containsKey(second_rep)){
                map.remove(second_rep);
                disjointSet.remove(index);
            }
        }
 
        return;
    }

    
    
    public int findSet(int element){
        for (int index = 0; index < disjointSet.size(); index++){
            Map<Integer, Set<Integer>> map = disjointSet.get(index);
            Set<Integer> keySet = map.keySet();
            for (Integer key : keySet){
                Set<Integer> set = map.get(key);
                if (set.contains(element)){
                    return key;
                }
            }
        }
        return -1;
    }
    
    public int printAllSets() {
      Iterator<Map<Integer, Set<Integer>>> iterator = disjointSet.iterator();
      System.out.println("size is "+disjointSet.size());
      Set<Set<Integer>> dSets = null;
      
      for (int i = 0; i < disjointSet.size(); i++) {
        System.out.println(getRepByIndex(i));
      }
      
      while (iterator.hasNext()) {
//        dSets.addAll(iterator.next().values());
        System.out.println(iterator.next().values());
      }
      System.out.println(dSets);
      return 1;
    }
    
    
    public boolean moveElement(int element, int destiny){
        int rep1 = findSet(element);
        
        if (rep1==-1) {
          System.out.println("this element is not exited");
          return false;
        }
        else {
          int rep2 = findSet(destiny);
          if (rep2 == -1) {
            createSet(destiny);
        }
          
         
          Set<Integer> set1 = null, set2 = null;
          for (int index = 0; index < disjointSet.size(); index++){
            Map<Integer, Set<Integer>> map = disjointSet.get(index);
            
            if (map.containsKey(rep1)){
              set1 = map.get(rep1);
              
              set1.remove(element); //delete element from set1
              if (set1.isEmpty()) {
                map.remove(rep1);
                disjointSet.remove(index);
              }

//              System.out.println("------------- : "+ rep1);
              if (rep1==element) { //change representative
                List<Integer> sortedList = new ArrayList<>(set1);
                Collections.sort(sortedList);
                rep1 = sortedList.get(0);
                map.put(rep1, set1);
              }
            }
            else if (map.containsKey(rep2)){ 
                set2 = map.get(rep2);
                set2.add(element);
                int newRep = (element<rep2)? element :rep2;
                map.put(newRep, set2);
                System.out.println("---"+element);
            }
          }
       
          createSet(element);
          union(element, destiny);
       return true;
       }
    }
    
    
    public Set<Integer> getSet(int element){
      int rep = findSet(element);
      
      Set<Integer> set = null;
      for (int index = 0; index < disjointSet.size(); index++){
        Map<Integer, Set<Integer>> map = disjointSet.get(index);
        if (map.containsKey(rep)){
          set = map.get(rep);
        } 
      }
      
      return set;
  }
    
    
    public Set<Integer> getRepByIndex(int idx){
    	return disjointSet.get(idx).keySet();
    }
    
    public Collection<Set<Integer>> getValue(int idx){
    	return disjointSet.get(idx).values();
    }
 
    public int getNumberofDisjointSets(){
        return disjointSet.size();
    }
 
    public static void main(String... arg){
        DisjointSets disjointSet = new DisjointSets();
 
        for (int i = 1; i <= 5; i++){
            disjointSet.createSet(i);
        }
 
        System.out.println("ELEMENT : REPRESENTATIVE KEY");
        for (int i = 1; i <= 5; i++){
            System.out.println(i + "\t:\t" + disjointSet.findSet(i));
        } 
 
        System.out.println("\n the set for 10 : "+ disjointSet.findSet(10));
        disjointSet.union(1, 10);
        System.out.println("The number of disjoint set after a union : "+ disjointSet.getNumberofDisjointSets());
        System.out.println("Performing unions ");
        disjointSet.union(1, 2);
        disjointSet.union(1, 3);
        disjointSet.union(1, 4);
        disjointSet.union(1, 5);
        System.out.println("The number of disjoint set after a union: "+ disjointSet.getNumberofDisjointSets());
 
 
        System.out.println("ELEMENT : REPRESENTATIVE KEY");
        for (int i = 1; i <= 5; i++){
            System.out.println(i + "\t:\t" + disjointSet.findSet(i));
        }
 
        System.out.println("\nThe number of disjoint set : "+ disjointSet.getNumberofDisjointSets());
        System.out.println("\n set : "+ disjointSet.getSet(1));
        
        disjointSet.moveElement(1, 1);
        System.out.println("ELEMENT : REPRESENTATIVE KEY");
        for (int i = 1; i <= 5; i++){
            System.out.println(i + "\t:\t" + disjointSet.findSet(i));
        }
        System.out.println("after the movement : "+ disjointSet.getSet(5));
        System.out.println("6 after the movement : "+ disjointSet.getSet(1));
        
        disjointSet.printAllSets();
        
       
    }
}
