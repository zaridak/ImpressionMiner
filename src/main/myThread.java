package main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class myThread implements Runnable {

    public Thread t;
    private String threadName;
    boolean suspended = false;
    private String url;
    private ArrayList<String> keyWords;
    private String keimeno="";

    // this.URL, < keyWord,timesFound>
    private LinkedHashMap<String, LinkedHashMap <String,Integer> > singleResults;
    public ArrayList<String> getSearchTerms(){
        return this.keyWords;
    }
    public boolean containsKeyWord(String keyword){
        LinkedHashMap<String,Integer> tmp = singleResults.get(this.url);
        if(tmp.containsKey(keyword) && tmp.get(keyword)>0)
            return true;
        else
            return false;
    }

    public myThread(String name,String url,ArrayList<String> keyWords) {
        this.singleResults = new LinkedHashMap<>();
        this.threadName = name;
        this.url = url;
        this.keyWords = keyWords;
        System.out.println("Thread Constructor called, searching at URL "+this.url+" the keywords: ");
        keyWords.forEach(tmp-> System.out.println(tmp+" "));
    }

    public LinkedHashMap<String, LinkedHashMap <String,Integer> > getSingleResults(){return this.singleResults;}
    public String getKeimeno(){return this.keimeno;}
    public String getURl(){return this.url;}

    public void myJoin() throws Exception {
        try{
            this.t.join();
        }catch (Exception ex){
            System.out.println("Exception trowed at myThread join "+ex.getMessage());
        }
    }

    public String getName() { return this.threadName; }

    public void run() {

        try {
            StringBuffer got = new StringBuffer();
            try {
                Document doc = Jsoup.connect(this.url).get();
                String title = doc.title();
                Element body = doc.body();
                for (Element headline : body.getAllElements())
                    got.append(headline.text());
            } catch (IOException e) {
                System.err.println("URL "+this.url+" not available");// e.printStackTrace();
            }
            Pattern pattern;
            Matcher matcher;
            int timesFound = 0;
            LinkedHashMap <String,Integer> keyWordCount = new LinkedHashMap<>(); // keyWord,timesFound to insert to singleResults
            for(String tmpKey : keyWords){
                timesFound = 0;
                pattern = Pattern.compile(tmpKey);
                matcher = pattern.matcher(got);
                while (matcher.find()){
                    timesFound++;
                }
                this.keimeno = got.toString();
                //System.out.println("Eimai to thread "+this.threadName+" brika to "+tmpKey+" sto "+this.url+" "+timesFound+" fores");
                keyWordCount.put(tmpKey,timesFound);
            }
            singleResults.put(this.url,keyWordCount); // adding the results to map
            for(Map.Entry<String, LinkedHashMap<String, Integer>> tmp : singleResults.entrySet()){
                if(tmp!=null){
                    System.out.println("At url "+tmp.getKey()+"  found keywords:");
                    if(!tmp.getValue().isEmpty()){
                        tmp.getValue().forEach((k,v)-> System.out.println("Keyword: ->"+k+"<-  Found: "+v+" Times."));
                    }
                }
            }
            //Thread.sleep(1000);
            synchronized (this) { // used to pause the threads
                while (suspended) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Thread " + threadName + " interrupted.");
        }
        //     System.out.println("Thread " + threadName + " exiting.");
    }

    public void start() { //System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }

    void suspend() {
        suspended = true;
    }

    synchronized void resume() {
        suspended = false;
        //notify();
        notifyAll();
    }

}
