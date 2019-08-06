package database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Impression {

    LinkedHashMap<String, String> ImpressionMap; //  keyword -> impression for one thread/url

    private String URL; // ID
    private ArrayList<String> keywords;
    private static ArrayList<String> badWordsEn;
    private static ArrayList<String> badWordsGr;
    private static ArrayList<String> goodWordsEn;
    private static ArrayList<String> goodWordsGr;
    private String text;

    public Impression(String url, ArrayList<String> keyWords, String keimeno) {
        this.URL = url;
        this.keywords = keyWords;
        this.text = keimeno;
        this.ImpressionMap = new LinkedHashMap<>();
        this.setImpression();// TODO ADD IT TO GET IMPRESSION FFS
    }

    String getURL() {
        return this.URL;
    }

    LinkedHashMap<String,String> getImpressionMap(){
        return this.ImpressionMap;
    }

    private void setImpression() {
        String delimiter = "\t\n\r\f ";
        this.text = text.replaceAll("[\\-\\[\\]/+\\.(){}!`~;'<_=>?\\^:,]", " ").replaceAll(" +", " ").trim();
        StringTokenizer tokenizer = new StringTokenizer(text, delimiter);

//        while (tokenizer.hasMoreTokens()) {
//            String currentToken = tokenizer.nextToken();
//            if (!badWordsEn.contains(currentToken.toLowerCase()) && !badWordsGr.contains(currentToken.toLowerCase())) {
//            }
//        }
        String[] splited = this.text.split("\\s+");
        List<String> textSplited = Arrays.asList(splited);
        //positionOfKeyword contains the index of a keyword found each time at the text.
        ArrayList<Integer> positionOfKeyword = new ArrayList<>();

        System.out.println("Impression for Thread with URL "+this.URL);
        for (String keyWord : this.keywords) { //for each Keyword will search in text

            if(text.contains(keyWord)){ // if keyWord exists in text
                int positiveScore =0, negativeScore=0;
                for(int pos = 0; pos < textSplited.size();pos++){  // for each word of the text
                    if(textSplited.get(pos).equals(keyWord))
                        positionOfKeyword.add(pos);//if keyword exists, save the index of it
                } // position mapping for
                    //System.out.println("To keyword "+keyWord+" exists at");

                //iterate the array holding position of keywords in text
                for(int i=0;i<positionOfKeyword.size();i++){
                    //checking for impression words forward 15 words
                    var localCounter1 =0;
                    for(var forward = positionOfKeyword.get(i);forward < textSplited.size();forward++ ){
                        positiveScore += positiveScore(textSplited.get(forward));
                        negativeScore += negativeScore(textSplited.get(forward));
                        localCounter1++;
                        if(localCounter1 == 15) //if 15 words reached, break
                            break;
                    }//forward for

                    //checking or impression words backward 15 words
                    var locaCounter2 = 0;
                    for(var backward = positionOfKeyword.get(i); backward >=0;backward--){
                        //if(textSplited.get(backward).matches("(.*)Υποχρεωτικά(.*)") )
                            //System.out.println("Brika good word backward "+textSplited.get(i));
                        positiveScore += positiveScore(textSplited.get(backward));
                        negativeScore += negativeScore(textSplited.get(backward));
                        locaCounter2++;
                        if(locaCounter2 == 15) //if 15 words reached, break
                            break;
                    } // backward for

                }//iterate the array holding position of keywords in text
                // no we have both scores. adding them to the map
                if(positiveScore > negativeScore) ImpressionMap.put(keyWord,"Positive");
                else if(negativeScore > positiveScore) ImpressionMap.put(keyWord,"Negative");
                else ImpressionMap.put(keyWord,"Neutral");
            }//End of IF keyword exists in text

        }//keyword for

        //keywords.forEach(tmp->getImpressionUrlKeyWord(this.URL,tmp));
        printImpressionMap();
    }

    public void printImpressionMap() {
        ImpressionMap.forEach((key, value) -> { System.out.println("For keyWord ->"+key+"<- Impression is "+value); });
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

    private int positiveScore(String input){
        int score = 0;
        if(goodWordsEn.isEmpty() && goodWordsGr.isEmpty()){
            System.err.println("No words found for Good Impressions");
            return 0;
        }
        for(String tmp : goodWordsEn){
            if(input.matches("(.*)"+tmp+"(.*)"))
                score++;
        }
        for(String tmp : goodWordsGr){
            if(input.matches("(.*)"+tmp+"(.*)"))
                score++;
        }
        return score;
    }

    private int negativeScore(String input){
        int score = 0;
        if(badWordsGr.isEmpty() && badWordsEn.isEmpty()){
            System.err.println("No words found for Bad Impressions");
            return 0;
        }
        for(String tmp : badWordsEn){
            if(input.matches("(.*)"+tmp+"(.*)"))
                score++;
        }
        for(String tmp : badWordsGr){
            if(input.matches("(.*)"+tmp+"(.*)"))
                score++;
        }
        return score;
    }

}
// kathe thread exei ena impression, pou to kathena mesa krataei ena map URL-keyword kai ejagei impression