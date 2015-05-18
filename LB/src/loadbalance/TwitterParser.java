package loadbalance;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import javax.annotation.processing.Filer;

import twitter4j.JSONException;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.URLEntity;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.omg.IOP.Codec;

public class TwitterParser {
  
  public Map<Integer, ArrayList<String>> output = new HashMap<Integer, ArrayList<String>>();
  public Map<Integer, ArrayList<String>> input = new HashMap<Integer, ArrayList<String>>();
  Map<Integer, Integer> occurance = null;
  Map<String, Integer> groups = null;
  private String path = null;
  public int Count = 0;

  
  public TwitterParser(String str) {
    path = str;
    // TODO Auto-generated constructor stub
  }

  /** 
   * Filter: Select data from States and UK
   * @param runTime: time period if input tweets
   * @param downParallelism: parallelism of downstream operators;
   */
  public TwitterParser(int start, int end, int downParallelism, ArrayList<Integer> plan) throws  TwitterException, IOException{
      
      String path = "/home/qingsongguo/workspace/tweetstream.txt";
      long startTime = 1361975052;
      FileReader fr = new FileReader(path);
      BufferedReader br = new BufferedReader(fr);
      
      String content = br.readLine();
      
      while(content != null && plan.contains(0)){
          if(content.contains("created") ){
              try{
                  
                  Status s = TwitterObjectFactory.createStatus(content);
  
                  if(s.getCreatedAt().getTime()  >= startTime*1000 + end * 1000){
                      break;
                  }
                  
                  //start from the selected start time;
                  if(s.getCreatedAt().getTime() >= startTime * 1000 + start * 1000){
                      if(s.getPlace()!=null){
                          String code = s.getPlace().getCountryCode();
                          if(code.equals("GB") || code.equals("US")){
                              Count ++;
                              int key = Count % downParallelism;
                              if(output.containsKey(key) == false){
                                  output.put(key, new ArrayList<String>(Arrays.asList(content)));
                              }
                              else{
                                  output.get(key).add(content);
                              }
                          }
                      }
                  }
              }catch(TwitterException e){continue;}
          }
          content = br.readLine();
          System.out.println(content);
      }
      
      br.close();
      fr.close();     
  }
  
  
  
  public void groupByCountry(int num ) throws  TwitterException, IOException{
    groups = new HashMap<String, Integer>(300);
    
    FileReader fr = new FileReader(path);
    BufferedReader br = new BufferedReader(fr);
    
    String code = null;
    int value = 0, count = 0;
    
    String content = br.readLine();
    while(content != null){
        if(content.contains("created") ){
            try{
                Status s = TwitterObjectFactory.createStatus(content);
                System.out.println("----"+s);
                
              if (count< num) {
                if(s.getPlace()!=null){
                  code = s.getPlace().getCountryCode();
                  
                  if (groups.containsKey(code)) {
                    value = 1 + groups.get(code).intValue();
                  }
                  else {
                    value = 1;
                  }
                  
                  groups.put(code, value);
                  count++;
                }
              }
              
              else {
                System.out.println("group size: "+ groups.keySet().size()+ " groups: "+ groups);
                return;
              }
                
            }catch(TwitterException e){
              continue;
          }
        }
        content = br.readLine();
    }
    
    br.close();
    fr.close();
    
  }
  
  
  public void getNumberByMinutes(int num ) throws  TwitterException, IOException{
    occurance = new HashMap<Integer, Integer>(num);
    
    FileReader fr = new FileReader(path);
    BufferedReader br = new BufferedReader(fr);
    
    int value = 0, time = 0, index=0;
    int count = 0, curTime = 0;
    
    String content = br.readLine();
    
    while(content != null){
        if(content.contains("created") ){
            try{
                Status s = TwitterObjectFactory.createStatus(content);
                System.out.println("s: " + s);
                
                int minute = s.getCreatedAt().getMinutes();
                int hour = s.getCreatedAt().getHours();
                time = 100*hour + minute;
                
                if (time > curTime) {
                  curTime = time;
                  count++;
                  if (count> num) {
                    System.out.println("index"+ index +" count: "+ count+ " occurance: "+ occurance);
                    return;
                  }
                }
                
                if (occurance.containsKey(time)) {
                  value = 1 + occurance.get(time).intValue();
                }
                else {
                  value = 1;
                }
                occurance.put(time, value);
                index++;
                
            }catch(TwitterException e){}
        }
        content = br.readLine();
    }
    
    br.close();
    fr.close();
    
  }
  
  
  public void groupStatistics(int num, String infile, FileWriter ofile ) throws  TwitterException, IOException{
    Map<String, Integer> histogram = new HashMap<String, Integer>(200);
    Map<String, Integer> codeMap = new HashMap<String, Integer>(200);
    String str = "NULL";
    codeMap.put("NULL",1);
    histogram.put(Integer.toString(1), 0);
    
    FileReader fr = new FileReader(infile);
    BufferedReader br = new BufferedReader(fr);
    String content = br.readLine();
    int value = 0, count = 0, curMinute = 24, id=1, time=0, curTime=0, hour=0, minute=0;
    
    while((content != null) && (count< num)){
        if(content.contains("created") ){
            try{
                Status s = TwitterObjectFactory.createStatus(content);
                
                String code = null;
                if (s.getPlace()!=null) {
                  code = s.getPlace().getCountryCode();
                  if (code==null) {
                    id = 1;
                  }
                  else if (codeMap.containsKey(code)) {
                    id = codeMap.get(code).intValue();
                  }
                  else {
                    id = codeMap.keySet().size()+1;
//                    System.out.println("code: "+code + " id:"+id);
                    codeMap.put(code, id);
                  }
                  hour = s.getCreatedAt().getHours();
                  minute = s.getCreatedAt().getMinutes();
                  time = hour*100 + minute;
//                  curTime = s.getCreatedAt().getHours()*100 + curMinute;
              
                  if ( time <= curTime) {
                    if (!histogram.containsKey(Integer.toString(id))) {
                      value = 1;
                    }
                    else {
                      value = 1 + histogram.get(Integer.toString(id)).intValue();
                    }
                    
                    histogram.put(Integer.toString(id), value);
                  }
                  else {
                    curTime = time;
                    System.out.println("count: "+ count + " curTime " + curTime+ " time "+time);
                    for (Map.Entry<String, Integer> entry : histogram.entrySet()) {
                      ofile.write(entry.toString()+" ");
                    }
                    ofile.write("\n");
                    histogram.clear();
                    histogram.put(Integer.toString(1), 0);
                    histogram.put(Integer.toString(id), 1);
                    count++;
                  }
                }
                else {
                  histogram.put("1", histogram.get("1").intValue()+1);
//                  continue;
                }
                
            }catch(TwitterException e){
              continue;
          }
        }
//        else {
//          histogram.put(Integer.toString(1), histogram.get(Integer.toString(1)).intValue()+1);
//        }
        content = br.readLine();
    }
    
    br.close();
    fr.close();
//    System.out.println("number of items:" + codeMap.keySet().size());
  }
 
  
  public void getStatistics(Map<Integer, String> map, int wsize, int num ) throws  TwitterException, IOException{
    Statistics st = new Statistics(map);
    groups = new HashMap<String, Integer>(300);
    
    FileReader fr = new FileReader(path);
    BufferedReader br = new BufferedReader(fr);
    
    String code = null;
    int value = 0, count = 0;
    
    String content = br.readLine();
    while(content != null){
        if(content.contains("created") ){
            try{
                Status s = TwitterObjectFactory.createStatus(content);
                
              if (count< num) {
                if(s.getPlace()!=null){
                  code = s.getPlace().getCountryCode();
                  
                  if (groups.containsKey(code)) {
                    value = 1 + groups.get(code).intValue();
                  }
                  else {
                    value = 1;
                  }
                  
                  groups.put(code, value);
                  count++;
                }
              }
              
              else {
                System.out.println("group size: "+ groups.keySet().size()+ " groups: "+ groups);
                return;
              }
                
            }catch(TwitterException e){continue;}
        }
        content = br.readLine();
    }
    
    br.close();
    fr.close();
  }
  
  
  //main1
  public static void main(String[] args) throws TwitterException, IOException {
    String path = "/home/qingsongguo/workspace/tweetstream.txt";
    String path1 = "/home/qingsongguo/workspace/tw.txt";
    FileWriter of = new FileWriter(path1);
    TwitterParser tparser = new TwitterParser(path);
//    Map<Integer, String> map = new HashMap<Integer, String>(300);
    tparser.groupStatistics(7200, path, of);
    of.close();
  }

//  //tweets jason
//  public static void main(String[] args) throws IOException {
//    String path = "/home/qingsongguo/workspace/tweets-nov-2012/2012-11-02.json";
////    String path = "/home/qingsongguo/workspace/web-clicks-nov-2009/2009-11-01.json";
//    FileReader fr = new FileReader(path);
//    BufferedReader br = new BufferedReader(fr);
//    String line = br.readLine();
//    
//    JSONParser parser = new JSONParser();
//    while(line!=null) {
//      try {
//        Object obj = parser.parse(line);
//        JSONObject jObject = (JSONObject) obj;
////        getTldString(jObject.get("urls").toString())
////        System.out.println("top-level domain: " + jObject.get("urls").toString());
//        
//        System.out.println("top-level domain: " + getTldString(jObject.get("urls").toString()));
//        
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//      line = br.readLine();
//    }
//    
//    br.close();
//    fr.close();
//  }
//  
//  //main-3
//  public static void main(String[] args) throws TwitterException, IOException {
//    String path = "/home/qingsongguo/workspace/tweetstream.txt";
//    TwitterParser tparser = new TwitterParser(path);
//    tparser.getNumberByMinutes(120);
//    
//    
//    FileWriter fout = new FileWriter("/home/qingsongguo/out.txt");
//    String line = null;
//    int index = 1;
//    
//    Iterator<Entry<Integer, Integer>> iterator = tparser.occurance.entrySet().iterator();
//    while (iterator.hasNext()) {
//      Map.Entry<Integer, Integer> entry = iterator.next();
//      line = String.valueOf(index++) + "  " + entry.getValue() + "\n";
//      fout.append(line);
//    }
//  
//    fout.close();
//  
//  }
  
  
  public void getStatitics(String path) throws TwitterException, IOException {
//    String path = "/home/qingsongguo/workspace/tweetstream.txt";
    FileReader fileReader = new FileReader(path);
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    String line = bufferedReader.readLine();
    
    int count = 0;
    Date time = null;
    
    while (line!=null) {
      try {
        Status s = TwitterObjectFactory.createStatus(line);
        if (s.getCreatedAt()!=null) {
          time = s.getCreatedAt();
        }
        
        if (count ==1) {
          System.out.println("Start time: " + s.getCreatedAt());
        }
        
      } catch (TwitterException e) {
//          continue;
      }
      
      line = bufferedReader.readLine();
      if (line==null) {
        System.out.println("End time: "+time);
        System.out.println("The total number of tweets: "+ count);
      }
      else {
        count++;
      }
    }
    bufferedReader.close();
    fileReader.close();
  }
  
  
//  //main-3
//  public static void main(String[] args) throws TwitterException, IOException {
//    String path = "/home/qingsongguo/workspace/tweets2009-6.txt";
//    FileReader fileReader = new FileReader(path);
//    BufferedReader bufferedReader = new BufferedReader(fileReader);
//    String line = bufferedReader.readLine();
//    int count = 0;
//    
//    while (line!=null && count<10) {
//      System.out.println(line);
//      count++;
//    }
//    
//    bufferedReader.close();
//    fileReader.close();
//  }

}




