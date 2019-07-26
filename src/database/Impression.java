package database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Impression {

    LinkedHashMap<String, LinkedHashMap<String, String>> omg;//   URl  -->  <keyword,impressionStatus>
    LinkedHashMap<String, String> ImpressionMap; //  keyword -> impression
    private String URL; // ID
    private ArrayList<String> keywords;
    private static ArrayList<String> badWordsEn;
    private static ArrayList<String> badWordsGr;
    private static ArrayList<String> goodWordsEn;
    private static ArrayList<String> goodWordsGr;
    private String impression;
    private String text;

    public Impression(String url, ArrayList<String> keyWords, String keimeno) {
        this.URL = url;
        this.keywords = keyWords;
        this.text = keimeno;
        this.ImpressionMap = new LinkedHashMap<>();
        this.setImpression();// TODO ADD IT TO GET IMPRESSION FFS
    }

    public String getURL() {
        return this.URL;
    }

    private void setImpression() {

        String delimiter = "\t\n\r\f ";
        this.text = text.replaceAll("[\\-\\[\\]\\/\\+\\.(){}!`~;'<_=>?\\^:,]", " ").replaceAll(" +", " ").trim();
        StringTokenizer tokenizer = new StringTokenizer(text, delimiter);
        System.out.println("fix the func  212121? \n");

        while (tokenizer.hasMoreTokens()) {
            String currentToken = tokenizer.nextToken();
            if (!badWordsEn.contains(currentToken.toLowerCase()) && !badWordsGr.contains(currentToken.toLowerCase())) {
                //System.out.println("δε περιέχουν το "+currentToken.toLowerCase());
                //   System.out.println(currentToken);

            }
        }

        for (String keyWord : this.keywords) { //for each Keyword will search in text

        }

    }


    public String getImpressionUrlKeyWord(String url, String keyword) {
        //iterate the map when find url, iterate the getValue
        //when find keyword get the getValue which is the impression.... ffs
        int score = 0;
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(keyword);

        return "";
    }

    public static void loadImpressionWords() {
        badWordsEn = new ArrayList<>();
        badWordsGr = new ArrayList<>();
        goodWordsEn = new ArrayList<>();
        goodWordsGr = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("./StopWords/goodWordsGr.txt"))) {
            String line;
            while ((line = br.readLine()) != null)
                goodWordsGr.add(line.toLowerCase());
        } catch (Exception ex) {
            System.err.println("Exception thrown reading goodWordsGr.txt");
        }

        try (BufferedReader br = new BufferedReader(new FileReader("./StopWords/goodWordsEn.txt"))) {
            String line;
            while ((line = br.readLine()) != null)
                goodWordsEn.add(line.toLowerCase());
        } catch (Exception ex) {
            System.err.println("Exception thrown reading goodWordsEn.txt");
        }

        try (BufferedReader br = new BufferedReader(new FileReader("./StopWords/badWordsEn.txt"))) {
            String line;
            while ((line = br.readLine()) != null)
                badWordsEn.add(line.toLowerCase());
        } catch (Exception ex) {
            System.err.println("Exception thrown reading stopWordsEn.txt");
        }

        try (BufferedReader br = new BufferedReader(new FileReader("./StopWords/badWordsGr.txt"))) {
            String line;
            while ((line = br.readLine()) != null)
                badWordsGr.add(line.toLowerCase());
        } catch (Exception ex) {
            System.err.println("Exception thrown reading badWordsGr.txt");
        }
    }

}

// kathe thread exei ena impression, pou to kathena mesa krataei ena map URL-keyword kai ejagei impression