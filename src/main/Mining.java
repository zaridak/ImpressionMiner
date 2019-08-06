package main;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class Mining {

    private ArrayList<String> allURLs;
    private ArrayList<String> allKeyWords;
    private ArrayList<myThread> myThreads;

    //numofURL = numOfThreads
    Mining(ArrayList<String> allURLs, ArrayList<String> AllKeyWords){
        this.allURLs = allURLs;
        this.allKeyWords = AllKeyWords;
        this.myThreads = new ArrayList<>();
        IntStream.range(0, allURLs.size()).forEachOrdered(i -> myThreads.add(new myThread(String.valueOf(i), allURLs.get(i), this.allKeyWords)));
        //for (i=0;  i < allURLs.size()
    }

    ArrayList<myThread> getMyThreads(){return this.myThreads;}

    void start() {
        if(!myThreads.isEmpty()) myThreads.forEach(tmp->tmp.start());
    }

    public void pause() { //System.out.println("Kalw pause");
        if(!myThreads.isEmpty()) myThreads.forEach(tmp->tmp.suspend());
    }
    public void resume(){ //System.out.println("Kalw resume");
        if(!myThreads.isEmpty()) myThreads.forEach(tmp->tmp.resume());
    }

    void joinAll(){
        if(!myThreads.isEmpty()){
            myThreads.forEach(tmp->{
                try {
                    tmp.myJoin();
                }catch (Exception ex){
                    System.out.println("Exception throwed at Mining join "+ex.getMessage());
                }});
        }
    }
}
