package database;

import main.myThread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
    private String formattedDate = "";
    // connection class
    private Connection conn = null;
    private Statement stmt = null;

    // end of  connection class
    public StringBuffer getDbBufferString() {
        return this.dbBufferString;
    }

    //keyWord -> totalTimes
    private LinkedHashMap<String, Integer> kwTotalOccurrences;
    // keyWord,  URL -> timesFound
    private LinkedHashMap<String, LinkedHashMap<String, Integer>> whichUrlAndCount;

    //keyword, Url ->Impression  or   URL,  keyWord impression
    private LinkedHashMap<String, LinkedHashMap<String, String>> kwUrlImpression;

    public Result(ArrayList<myThread> all, ArrayList<String> keyWords, ArrayList<String> searchedURLs) {
        this.urlsSearched = new ArrayList<>();
        this.urlsSearched = searchedURLs;
        keyWords = new ArrayList<>();

        // System.out.println("Result constructor calls me keywords "+keyWords.size());
        allThreads = new ArrayList<>();
        this.allThreads = all;
        this.allImpressions = new ArrayList<>();
        Impression.loadImpressionWords();  // doing this here only one time called
        whichUrlAndCount = new LinkedHashMap<>();
        kwTotalOccurrences = new LinkedHashMap<>();
        kwUrlImpression = new LinkedHashMap<>();
        this.dbBufferString = new StringBuffer();
        this.formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);
        } catch (Exception ex) {
            System.err.println("Exception at result construction getting DBConnection " + ex.getMessage());
        }
    }

    public Result() {
        try {
            conn = DBConnection.getInstance().getConnection();
            conn.setAutoCommit(false);
        } catch (Exception ex) {
            System.err.println("Exception at result construction getting DBConnection " + ex.getMessage());
        }
    }

    public void addImpression(Impression toAdd) {
        this.allImpressions.add(toAdd);
    }

    public int impressionsCount() {
        return this.allImpressions.size();
    }

    public void closeDBConnection() {
        try {
            this.conn.close();
        } catch (Exception ex) {
            System.err.println("Failed to close DB connection " + ex.getMessage());
        }
    }

    //keyword -> impression  for specific Url
    private LinkedHashMap<String, String> getImpressionMapByID(String url) {
        if (this.allImpressions != null && !this.allImpressions.isEmpty()) {
            for (Impression tmp : this.allImpressions)
                if (tmp != null && tmp.getURL().equals(url))
                    return tmp.getImpressionMap();
        }
        return null;
    }

    @Override
    public List<myThread> getAllThreads() {
        return this.allThreads;
    }


    @Override
    public void loadAllFromDB() {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * from keywords");
            String change = ""; //used to track when we reach the last index of the DB
            while (rs.next()) {

                if (!change.equals(rs.getString(1))) {
                    //printing at how many url the search took place
                    Statement stfurl = conn.createStatement();//distinct wont count duplicates
                    ResultSet totalurl = stfurl.executeQuery("select count (distinct url) as totalURL from results  where mdate ='" + change + "'");
                    if (totalurl.next()) { }
                    int count = totalurl.getInt("totalURL");
                    if(count > 0 )
                        System.out.println("Search took place at " + count + " urls");

                    //now retrieve which keywords exists in each individual search
                    ResultSet kws = stfurl.executeQuery("select distinct keyword as kw from results where mdate = '" + change + "'");
                    ArrayList<String> tmpKW = new ArrayList<>();
                    while (kws.next()) {
                        System.out.println("Searched for " + kws.getString("kw"));
                        tmpKW.add(kws.getString("kw")); // adding the keywords to tmpKW
                        Statement testconn = conn.createStatement();
                        ResultSet test = testconn.executeQuery("select url as aek from results where mdate = '" + change + "' and keyword = '" + kws.getString("kw") + "'" +
                                "and timesfound > 0");
                        int i = 0;
                        StringBuilder lola = new StringBuilder();
                        while (test.next()) {
                            lola.append(test.getString("aek"));
                            lola.append(" ");
                            i++;
                        }
                        System.out.println("Found at " + i + "/" + count + " target urls\n" + "URLS: " + lola);
                    }

                    Statement st2 = conn.createStatement();
                    ResultSet rs2 = st2.executeQuery("select * from results where mdate = '" + change + "'");
                    while (rs2.next()) { //iterating results table
                        System.out.println("At url " + rs2.getString(3) + " keyword: ->" + rs2.getString(2) + "<- found " + rs2.getInt(4) + " times, with impression " + rs2.getString(5));
                    }
                    System.out.println();

                }
                System.out.println("Date: " + rs.getString(1) + " KeyWord: ->" + rs.getString(2) + "<- timesFound: " + rs.getString(3));
                change = rs.getString(1);

            }// after while, printing the last index of the DB

            //printing the number of url the search took place for the last search in DB
            Statement stfurl = conn.createStatement();//distinct won't count duplicates
            ResultSet totalurl = stfurl.executeQuery("select count (distinct url) as totalURL from results  where mdate ='" + change + "'");
            if (totalurl.next()) {
            }
            int count = totalurl.getInt("totalURL");
            if(count > 0 )
                System.out.println("Search took place at " + count + " urls");

            //now retrieve which keywords exists in each individual search
            ResultSet kws = stfurl.executeQuery("select distinct keyword as kw from results where mdate = '" + change + "'");
            ArrayList<String> tmpKW = new ArrayList<>();
            while (kws.next()) {
                System.out.println("Searched for " + kws.getString("kw"));
                tmpKW.add(kws.getString("kw")); // adding the keywords to tmpKW
                Statement testconn = conn.createStatement();
                ResultSet test = testconn.executeQuery("select url as aek from results where mdate = '" + change + "' and keyword = '" + kws.getString("kw") + "'" +
                        "and timesfound > 0");
                var i = 0;
                StringBuilder lola = new StringBuilder();
                while (test.next()) {
                    lola.append(test.getString("aek"));
                    lola.append(" ");
                    i++;
                }
                System.out.println("Found at " + i + "/" + count + " target urls\n" + "URLS: " + lola);
            }

            Statement st2 = conn.createStatement();
            ResultSet rs2 = st2.executeQuery("select * from results where mdate = '" + change + "'");
            while (rs2.next()) { //iterating results table
                System.out.println("At url " + rs2.getString(3) + " keyword: ->" + rs2.getString(2) + "<- found " + rs2.getInt(4) + " times, with impression " + rs2.getString(5));
            }
            System.out.println();

            conn.commit();
            st.close();
            st2.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage() + " at loadAllFromDB");
        }
    }

//    @Override
//    public void saveResultsInDB(String resultString) {
//        //TODO STORE results in DB after calculation get singleResults from each thread
//        Connection c = null;
//        Statement stmt = null;
//        final String url = "jdbc:postgresql://localhost/postgres";
//        try {
//            c = DriverManager.getConnection(url);
//            c.setAutoCommit(false);
//            System.out.println("Opened database successfully");
//
//            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//            Date date = new Date();
//            String formatted = dateFormat.format(date);
//
//            PreparedStatement st = c.prepareStatement("INSERT INTO stats (impressions,timedate) VALUES (?, ?)");
//            st.setString(1, resultString);
//            st.setString(2, formatted);
//            st.executeUpdate();
//            c.commit();
//            c.close();
//        } catch (Exception e) {
//            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
//        }
//    }

    public void deleteAllDB() {
        try {
            Statement st = conn.createStatement();
            int rs = st.executeUpdate("TRUNCATE keywords, results, stats");
            System.out.println("Deleted");

        }catch (Exception ex){
            System.out.println("Ex at deleting db "+ex.getMessage());
            this.closeDBConnection();
        }
    }

    @Override //TODO ZARIDAK CONNECTION STRING
    public void saveResultsInDB(String resultString) {
        //TODO STORE results in DB after calculation get singleResults from each thread
      /*  Connection c = null;
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
        }*/

        for (Map.Entry<String, Integer> lol : kwTotalOccurrences.entrySet()) {
            // System.out.println("Keyword " + lol.getKey() + " Found " + lol.getValue() + " times");
            if (lol != null) {
                try {
                    PreparedStatement st = this.conn.prepareStatement("INSERT INTO keywords (mdate, keyword, totaltimesfound) values (?,?,?)");
                    st.setString(1, this.formattedDate);
                    st.setString(2, lol.getKey());
                    st.setInt(3, lol.getValue());
                    st.executeUpdate();
                    this.conn.commit();
                    //this.conn.close();
                    st.close();
                } catch (Exception e) {
                    try {
                        this.conn.close();
                    } catch (Exception ex) {
                        System.err.println("Failed closing DB conn after data insert" + ex.getMessage());
                    }
                    System.err.println("Exception at insert into keywords " + ": " + e.getMessage());
                }
            }
        }

        for (myThread tmpThread : this.allThreads) {
            LinkedHashMap<String, LinkedHashMap<String, Integer>> tmpSingleResult = tmpThread.getSingleResults();
            LinkedHashMap<String, String> tmpImpression = getImpressionMapByID(tmpThread.getURl());
            kwUrlImpression.put(tmpThread.getURl(), tmpImpression);

            int kwtmpFoundTimes = 0;
            String kwTmpName = "";

            // this.URL, < keyWord,timesFound>
            for (Map.Entry<String, LinkedHashMap<String, Integer>> tmp : tmpSingleResult.entrySet()) {
                if (tmp != null) {
                    //this.keyWordPerUrl += "At url " + tmp.getKey() + " found keywords: \n";

                    if (!tmp.getValue().isEmpty()) {
                        for (Map.Entry<String, Integer> run : tmp.getValue().entrySet()) {
                            kwTmpName = run.getKey();
                            //  System.out.println("Keyword ->"+run.getKey()+"<- Found "+run.getValue()+" Times.");
                            //  this.keyWordPerUrl += "Keyword ->" + run.getKey() + "<- Found " + run.getValue() + " Times. \n";
                            if (run.getValue() != null) {
                                try {
                                    PreparedStatement st = this.conn.prepareStatement("INSERT INTO results (mdate, keyword, url, timesfound, impression)" +
                                            " VALUES (?,?,?,?,?)");
                                    st.setString(1, this.formattedDate);
                                    st.setString(2, run.getKey());
                                    st.setString(3, tmp.getKey());
                                    st.setInt(4, run.getValue());
                                    st.setString(5, "-");
                                    st.executeUpdate();
                                    this.conn.commit();
                                    //this.conn.close();
                                } catch (Exception e) {
                                    System.err.println("Exception at insert into resuts " + e.getMessage());
                                }
                                kwtmpFoundTimes += run.getValue();
                            }
                        }
                    }
                }
                //     System.out.println("For gia url to kwtmpFoundTimes = "+kwtmpFoundTimes);
            }
            //System.out.println("And the Impressions Are: ****");

            for (Map.Entry<String, LinkedHashMap<String, String>> tmp : this.kwUrlImpression.entrySet()) {
                if (tmp.getValue().isEmpty()) {
                    continue;
                    //System.out.println("For url "+tmp.getKey()+" No impressions exist: ");
                } else {
                    //System.out.println("For url " + tmp.getKey() + " Impressions are: ");
                    for (Map.Entry<String, String> run : tmp.getValue().entrySet()) {
                        if (run != null && run.getKey() != null && run.getValue() != null && !run.getKey().isEmpty() && !run.getValue().isEmpty()) {
                            //System.out.println("Keyword: " + run.getKey() + " Impression is: " + run.getValue());
                            try {
                                PreparedStatement st = this.conn.prepareStatement("update results set impression = ? where url = ? and keyword = ? and mdate = ?");
                                st.setString(1, run.getValue());
                                st.setString(2, tmp.getKey());
                                st.setString(3, run.getKey());
                                st.setString(4, this.formattedDate);
                                st.executeUpdate();
                                this.conn.commit();
                                //this.conn.close();
                            } catch (Exception e) {
                                System.err.println("Exception at update results impressions" + e.getClass().getName() + ": " + e.getMessage());
                            }
                        }
                    }
                }
            }

            if (!tmpImpression.isEmpty()) {
                tmpImpression.forEach((key, value) -> {
                    //  System.out.println("For keyWord ->" + key + "<- Impression is " + value);
                });
            } else {
                //System.out.println("Nothing to show");
            }
        }
        System.out.println("*** Data saved in DB ***");
    }

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
        //System.out.println("\n\n****** SUGKEDRWTIKA RESULTS  ****\n\n");
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
                    //System.out.println("At url " + tmp.getKey() + "  found keywords:");
                    this.keyWordPerUrl += "At url " + tmp.getKey() + " found keywords: \n";

                    if (!tmp.getValue().isEmpty()) {
                        for (Map.Entry<String, Integer> run : tmp.getValue().entrySet()) {
                            kwTmpName = run.getKey();
                            //  System.out.println("Keyword ->"+run.getKey()+"<- Found "+run.getValue()+" Times.");
                            this.keyWordPerUrl += "Keyword ->" + run.getKey() + "<- Found " + run.getValue() + " Times. \n";

                            kwtmpFoundTimes += run.getValue();
                            //    System.out.println("kanw add map "+run.getKey()+" "+run.getValue());
                            if (run.getValue() >= 0) { //if exists
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
            // System.out.println("And the Impressions Are: ****");

            if (!tmpImpression.isEmpty()) {
                tmpImpression.forEach((key, value) -> {
                    // System.out.println("For keyWord ->" + key + "<- Impression is " + value);
                });
            } else {
                //   System.out.println("Nothing to show");
            }
        }
        //   System.out.println("\n\n**** END OF FINAL RESULTS");

        System.out.println("\n\nStore to DB\n\n");

        for (Map.Entry<String, Integer> lol : kwTotalOccurrences.entrySet()) {
            System.out.println("Keyword " + lol.getKey() + " Found " + lol.getValue() + " times");
            dbBufferString.append("Keyword " + lol.getKey() + " Found " + lol.getValue() + " times \n");
        }

        //System.out.println("keyword, url -> times found ");
        System.out.println(this.keyWordPerUrl);
        dbBufferString.append(this.keyWordPerUrl);
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
                if (thread!=null && thread.containsKeyWord(keyword)) {
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
            if (run.getKey() != null && run.getValue() != null && !run.getKey().isEmpty()) {
                System.out.println("keyword " + run.getKey() + " found at " + run.getValue() + "/" + this.urlsSearched.size() + " urls");
                dbBufferString.append("keyword " + run.getKey() + " found at " + run.getValue() + "/" + this.urlsSearched.size() + " urls \n");
            }
        }
        //kwURLName.forEach((k, v) -> System.out.println("keyword " + k + " found at url:  " + v));
        for (Map.Entry<String, String> run : kwURLName.entrySet()) {
            if (run.getKey() != null && run.getValue() != null && !run.getKey().isEmpty()) {
                System.out.println("keyword " + run.getKey() + " found at url:  " + run.getValue());
                dbBufferString.append("keyword " + run.getKey() + " found at url:  " + run.getValue() + " \n");
            }
        }
        // impression to DB
        for (Map.Entry<String, LinkedHashMap<String, String>> tmp : this.kwUrlImpression.entrySet()) {
            if (tmp.getValue().isEmpty()) {
                continue;
                //System.out.println("For url "+tmp.getKey()+" No impressions exist: ");
            } else {
                System.out.println("For url " + tmp.getKey() + " Impressions are: ");
                dbBufferString.append("For url " + tmp.getKey() + " Impressions are: \n");
//                tmp.getValue().forEach((k,v)->{
//                    StringBuffer aek = null;
//                    aek.append("Keyword: " + k + " Impression is: " + v);
//                    System.out.println("Keyword: " + k + " Impression is: " + v);
//                        });
                for (Map.Entry<String, String> run : tmp.getValue().entrySet()) {
                    if (run.getKey() != null && run.getValue() != null && !run.getKey().isEmpty() && !run.getValue().isEmpty()) {
                        System.out.println("Keyword: " + run.getKey() + " Impression is: " + run.getValue());
                        dbBufferString.append("Keyword: " + run.getKey() + " Impression is: " + run.getValue() + "\n");
                    }
                }
            }
        }

        //saveResultsInDB(aek.toString());
        System.out.println("\n\n");
    }

}
