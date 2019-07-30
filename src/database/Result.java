package database;

import main.myThread;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

//import java.beans.Statement;

public class Result implements ResultDAO {

    private List<myThread> allThreads;
    private ArrayList<String> keyWords;
    private ArrayList<String> urlsSearched;
    private ArrayList<Impression> allImpressions;
    private String keyWordPerUrl = "";
    private StringBuffer dbBufferString = null;

    public StringBuffer getDbBufferString(){return this.dbBufferString;}
    // PRINT REQUEST 1
    // Sunolikos arithmos emfanisewn kathe keyword se OLA TA URL, ARA KEYWORD -> TIMES FOUND TOTAL

    //keyWord -> totalTimes
    LinkedHashMap<String,Integer> kwTotalOccurrences;
    // keyWord,  URL -> timesFound
    LinkedHashMap<String,LinkedHashMap<String,Integer>> whichUrlAndCount;

    //keyword, Url ->Impression  or   URL,  keyWord impression
    LinkedHashMap<String, LinkedHashMap<String,String>> kwUrlImpression;

    //PRINT REQUEST 2
    //kathe keywrod, se posa URL uphrxe, ara  keyword -> arithmos URL pou ton perieixan
    //PRINT REQUEST 3
    //Gia kathe keyword, pososto emfanishs se URL  px to keyword: lola uphrxe se 5 apo 10 URL TODO MERGE WITH request 2

    public Result(ArrayList<myThread> all, ArrayList<String> keyWords, ArrayList<String> searchedURLs){
        this.urlsSearched = new ArrayList<>();
        this.urlsSearched = searchedURLs;
        keyWords = new ArrayList<>();

        System.out.println("Result constructor calls me keywords "+keyWords.size());
        allThreads = new ArrayList<>();
        this.allThreads = all;
        this.allImpressions = new ArrayList<>();
        Impression.loadImpressionWords();  // doing this here only one time called
        whichUrlAndCount = new LinkedHashMap<>();
        kwTotalOccurrences = new LinkedHashMap<>();
        kwUrlImpression = new LinkedHashMap<>();
        this.dbBufferString = new StringBuffer();
    }

    public void addImpression(Impression toAdd){this.allImpressions.add(toAdd);}

    public int impressionsCount(){return this.allImpressions.size();}

    //keyword -> impression  for specific Url
    private LinkedHashMap<String,String> getImpressionMapByID(String url){
        if(this.allImpressions!=null && !this.allImpressions.isEmpty()){
            for(Impression tmp : this.allImpressions)
                if(tmp!=null && tmp.getURL().equals(url))
                    return tmp.getImpressionMap();
        }
        return null;
    }

    @Override
    public List<myThread> getAllThreads() {
        return this.allThreads;
    }

    @Override
    public void saveResultsInDB(String resultString) {
        //TODO STORE results in DB after calculation get singleResults from each thread
        Connection c = null;
        Statement stmt = null;
        final String url = "jdbc:postgresql://localhost/postgres";
        try {
            c = DriverManager.getConnection(url);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String formatted = dateFormat.format(date);

            PreparedStatement st = c.prepareStatement("INSERT INTO stats (impressions,timedate) VALUES (?, ?)");
            st.setString(1, resultString);
            st.setString(2, formatted);
            st.executeUpdate();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
        }
    }
                    /*  //TODO ZARIDAK CONNECTION STRING
    @Override
    public void saveResultsInDB(String resultString) {
        //TODO STORE results in DB after calculation get singleResults from each thread
        Connection c = null;
        Statement stmt = null;
        final String url = "jdbc:postgresql://localhost/postgres";
        final String user = "postgres";
        final String password = "root";
        try {
            c = DriverManager.getConnection(url, user, password);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String formatted = dateFormat.format(date);

            PreparedStatement st = c.prepareStatement("INSERT INTO stats (impression,date) VALUES (?, ?)");
            st.setString(1, resultString);
            st.setString(2, formatted);
            st.executeUpdate();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
        }
    } */

    /*
        //keyWord -> totalTimes
    LinkedHashMap<String,Integer> kwTotalOccurrences;
        // keyWord,  URL -> timesFound
    LinkedHashMap<String,LinkedHashMap<String,Integer>> whichUrlAndCount;
        //keyword, Url ->Impression
    LinkedHashMap<String, LinkedHashMap<String,String>> kwUrlImpression;
    */
    @Override
    public void printResults(ArrayList<myThread> allThreads) {
        /*iterate all threads
          for each thread get it's impression by:
           Impression tmp = getImpressionById(currentThread.getURL())
        */
        System.out.println("\n\n****** SUGKEDRWTIKA RESULTS  ****\n\n");
        //running all threads
        for (myThread tmpThread : this.allThreads) {
            LinkedHashMap<String, LinkedHashMap<String, Integer>> tmpSingleResult = tmpThread.getSingleResults();
            LinkedHashMap<String, String> tmpImpression = getImpressionMapByID(tmpThread.getURl());
            kwUrlImpression.put(tmpThread.getURl(), tmpImpression);

            int kwtmpFoundTimes = 0;
            String kwTmpName = "";

            // this.URL, < keyWord,timesFound>
            for (Map.Entry<String, LinkedHashMap<String, Integer>> tmp : tmpSingleResult.entrySet()) {
                if (tmp != null) {
                    dbBufferString.append("At url " + tmp.getKey() + "  found keywords: \n");
                    System.out.println("At url " + tmp.getKey() + "  found keywords:");
                    this.keyWordPerUrl += "At url " + tmp.getKey() + " found keywords: \n";

                    if (!tmp.getValue().isEmpty()) {
                        for (Map.Entry<String, Integer> run : tmp.getValue().entrySet()) {
                            kwTmpName = run.getKey();
                            //  System.out.println("Keyword ->"+run.getKey()+"<- Found "+run.getValue()+" Times.");
                            this.keyWordPerUrl += "Keyword ->" + run.getKey() + "<- Found " + run.getValue() + " Times. \n";
                            kwtmpFoundTimes += run.getValue();
                            //    System.out.println("kanw add map "+run.getKey()+" "+run.getValue());
                            if (run.getValue() > 0) { //if exists
                                if (!kwTotalOccurrences.containsKey(run.getKey())) {  //if not added create new entry
                                    kwTotalOccurrences.put(run.getKey(), run.getValue());
                                } else { // if already added update current count Variable (getValue)
                                    kwTotalOccurrences.replace(run.getKey(), run.getValue() + kwTotalOccurrences.get(run.getKey()));
                                }
                            }
                        }
                    }
                }
                //     System.out.println("For gia url to kwtmpFoundTimes = "+kwtmpFoundTimes);
            }
            System.out.println("And the Impressions Are: ****");

            if (!tmpImpression.isEmpty()) {
                tmpImpression.forEach((key, value) -> {
                    System.out.println("For keyWord ->" + key + "<- Impression is " + value);
                });
            } else {
                System.out.println("Nothing to show");
            }
        }

        System.out.println("\n\n**** END OF FINAL RESULTS");
        System.out.println("\n\nStore to DB\n\n");

        for (Map.Entry<String, Integer> lol : kwTotalOccurrences.entrySet()) {
            System.out.println("Keyword " + lol.getKey() + " Found " + lol.getValue() + " times");
            dbBufferString.append("Keyword " + lol.getKey() + " Found " + lol.getValue() + " times \n");

        }

        //System.out.println("keyword, url -> times found ");
        System.out.println(this.keyWordPerUrl);

        for (Map.Entry<String, LinkedHashMap<String, Integer>> tmp : whichUrlAndCount.entrySet()) {
            for (Map.Entry<String, Integer> run : tmp.getValue().entrySet()) {
                System.out.println("To url " + tmp.getKey() + " contains " + run.getKey() + " " + tmp.getValue() + " times");
            }
        }

        //keyword, NumberOfurlContains it
        Map<String, Integer> kwURLNumber = new HashMap<>();
        //keyword , conCat String of urls
        Map<String, String> kwURLName = new HashMap<>();
        this.keyWords = allThreads.get(0).getSearchTerms();

        for (String keyword : keyWords) {
            for (myThread thread : allThreads) {
                if (thread.containsKeyWord(keyword)) {
                    if (kwURLNumber.containsKey(keyword)) // an to periexei  hdh
                        kwURLNumber.replace(keyword, kwURLNumber.get(keyword) + 1);
                    else kwURLNumber.put(keyword, 1);

                    if (kwURLName.containsKey(keyword))
                        kwURLName.replace(keyword, kwURLName.get(keyword) + " " + thread.getURl());
                    else kwURLName.put(keyword, thread.getURl());
                }
            }
        }
        //kwURLNumber.forEach((k, v) -> System.out.println("keyword " + k + " found at " + v + "/" + this.urlsSearched.size() + " urls"));
        for (Map.Entry<String, Integer> run : kwURLNumber.entrySet()) {
            if(run.getKey()!=null && run.getValue()!=null && !run.getKey().isEmpty()) {
                System.out.println("keyword " + run.getKey() + " found at " + run.getValue() + "/" + this.urlsSearched.size() + " urls");
                dbBufferString.append("keyword " + run.getKey() + " found at " + run.getValue() + "/" + this.urlsSearched.size() + " urls \n");
            }
        }
        //kwURLName.forEach((k, v) -> System.out.println("keyword " + k + " found at url:  " + v));
        for (Map.Entry<String, String> run : kwURLName.entrySet()) {
            if(run.getKey()!=null && run.getValue()!=null && !run.getKey().isEmpty()) {
                System.out.println("keyword " + run.getKey() + " found at url:  " + run.getKey());
                dbBufferString.append("keyword " + run.getKey() + " found at url:  " + run.getKey()+ " \n");
            }
        }
        System.out.println("\n\n****Impression to DB****\n");
        for (Map.Entry<String, LinkedHashMap<String, String>> tmp : this.kwUrlImpression.entrySet()) {
            if (tmp.getValue().isEmpty()) {
                continue;
                //System.out.println("For url "+tmp.getKey()+" No impressions exist: ");
            } else {
                System.out.println("For url " + tmp.getKey() + " Impressions are: ");

//                tmp.getValue().forEach((k,v)->{
//                    StringBuffer aek = null;
//                    aek.append("Keyword: " + k + " Impression is: " + v);
//                    System.out.println("Keyword: " + k + " Impression is: " + v);
//                        });
                for (Map.Entry<String, String> run : tmp.getValue().entrySet()) {
                    if(run.getKey()!=null && run.getValue()!=null && !run.getKey().isEmpty() && !run.getValue().isEmpty()) {
                        System.out.println("Keyword: " + run.getKey() + " Impression is: " + run.getValue());
                        dbBufferString.append("Keyword: " + run.getKey() + " Impression is: " + run.getValue());
                    }
                }
            }
        }
        //TODO fix ΑΕΚ String's format and input stored in it
        //saveResultsInDB(aek.toString());
        System.out.println("\n\n");
    }
}
