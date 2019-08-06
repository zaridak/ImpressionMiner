package main;

import database.Impression;
import database.Result;
import targetURL.targetURL;

import java.util.ArrayList;
import java.util.Scanner;

public class menu {

    private targetURL urls ;
    private ArrayList<String> searchTerms; //keywords
    private ArrayList<String> listOfURL;

    public menu(targetURL url){
        this.urls = url;
        searchTerms = new ArrayList<>();
    }

    private String posString(Scanner scan) {
        String input = "";
        boolean error = false;

        if (scan.hasNext()) {
            if (scan.hasNext()) {
                input = scan.nextLine();
                error = input.length()>1;
            } else {
                scan.next();
                error = true;
            }
        }
        while (error) {
            System.out.print("Invalid input custom. Please reenter: ");
            if (scan.hasNext()) {
                input = scan.nextLine();
                error = input.length()>1;
            } else {
                if (scan.hasNext())
                    scan.nextLine();
                error = true;
            }
        }
        return input;
    }

    private int posNum(Scanner scan) {
        int input = 0;
        boolean error = false;

        if (scan.hasNext()) {
            if (scan.hasNextInt()) {
                input = scan.nextInt();
                error = input <= 0;
            } else {
                scan.next();
                error = true;
            }
        }
        while (error) {
            System.out.print("Invalid input. Please reenter a menu option: ");
            if (scan.hasNextInt()) {
                input = scan.nextInt();
                error = input <= 0;
            } else {
                if (scan.hasNext())
                    scan.next();
                error = true;
            }
        }
        return input;
    }

    private void getSearchWords(Scanner go){
        int numberOfSearchTerms=0;
        System.out.println("Enter the number of search terms");
        numberOfSearchTerms = posNum(go);
        System.out.println("Enter "+numberOfSearchTerms+" search terms one by one, Enter seperated");
        for(int i =0;i<numberOfSearchTerms+1;i++)
            this.searchTerms.add((String)go.nextLine());
    }

    private void menu() {
        int userInput = 0;
        Scanner go = new Scanner(System.in);
        do {
            System.out.println("Press 1 to edit target URLs");
            System.out.println("Press 2 to Εnter search terms");
            System.out.println("Press 3 to Ιnit Mining");
            System.out.println("Press 4 to load all data from DataBase");
            System.out.println("Press 5 to Εxit");

            userInput = posNum(go);  // return in else if repeats menu
            if (userInput == 1) {
                try {
                    this.urls.handleTheUrl();
                } catch (Exception e) {
                    System.out.println("Ex at static menu " + e.getMessage());
                    e.printStackTrace();
                }
            }else if(userInput == 2){
                getSearchWords(go);
                printSR();
            }
            else if(userInput == 3){
                if(searchTerms.size()==0)
                    System.err.println("Can't start without search terms");
                else{
                    listOfURL = this.urls.getTargetURLs();
                    if(listOfURL.size() > 0){ //null check
                        Mining startMine = new Mining(this.urls.getTargetURLs(),this.searchTerms);
                        startMine.start();
                        startMine.joinAll();
                        Result res = new Result(startMine.getMyThreads(),this.searchTerms,this.listOfURL);
                        // run all threads and create a new Impression from them -> add it to res
                        startMine.getMyThreads().forEach( (tmp)->res.addImpression(new Impression(tmp.getURl(),this.searchTerms,tmp.getKeimeno())));
                        res.printResults(startMine.getMyThreads());
                        res.saveResultsInDB(res.getDbBufferString().toString());
                        res.closeDBConnection();
                        //System.out.println("TO STRING EINAI \n" + res.getDbBufferString().toString());
                    }
                }
            }
            else if(userInput == 4){
                Result res2 = new Result();
                res2.loadAllFromDB();
            }

            else if (userInput == 5) {
                go.close();
                System.exit(0);
            }

        } while (userInput != 5);
    }

    public void start(){
        this.menu();
    }

    private void removeSpaces(){
        var it = this.searchTerms.iterator();
        var tmp ="";
        while(it.hasNext()){
            tmp = it.next();
            if(tmp.equals(" ") || tmp.length()==0)
                it.remove();
        }
    }

    private void printSR(){ //    git 21
        if(this.searchTerms.size()>0)
            removeSpaces();
        System.out.println("Print SR");
        for(String lol:this.searchTerms) System.out.println("*"+lol+"*");
    }

}