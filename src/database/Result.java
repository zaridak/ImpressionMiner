package database;

import main.myThread;
import java.util.ArrayList;
import java.util.List;

public class Result implements ResultDAO {

    private List<myThread> allThreads;
    private List<String> keyWords;
    private ArrayList<String> urlsSearched;
    private ArrayList<Impression> allImpressions;

    // PRINT REQUEST 1
    // Sunolikos arithmos emfanisewn kathe keyword se OLA TA URL, ARA KEYWORD -> TIMES FOUND TOTAL

    //PRINT REQUEST 2
    //kathe keywrod, se posa URL uphrxe, ara  keyword -> arithmos URL pou ton perieixan

    //PRINT REQUEST 3
    //Gia kathe keyword, pososto emfanishs se URL  px to keyword: lola uphrxe se 5 apo 10 URL TODO MERGE WITH request 2

    public Result(ArrayList<myThread> all, ArrayList<String> keyWords, ArrayList<String> searchedURLs){
        this.urlsSearched = new ArrayList<>();
        this.urlsSearched = searchedURLs;
        keyWords = new ArrayList<>();
        this.keyWords = keyWords;
        allThreads = new ArrayList<>();
        this.allThreads = all;
        this.allImpressions = new ArrayList<>();
        Impression.loadImpressionWords();  // doing this here only one time called
    }

    public void addImpression(Impression toAdd){this.allImpressions.add(toAdd);}

    public int impressionsCount(){return this.allImpressions.size();}

    @Override
    public List<myThread> getAllThreads() {
        return this.allThreads;
    }

    @Override
    public void saveResultsInDB(ArrayList<myThread> allThreads) {
        //TODO STORE results in DB after calculation get singleResults from each thread
    }

    @Override
    public void printResults(ArrayList<myThread> allThreads) { }

}
