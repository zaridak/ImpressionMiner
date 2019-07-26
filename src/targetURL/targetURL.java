package targetURL;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class targetURL {

    private ArrayList<String> urls;

    public targetURL() {
        urls = new ArrayList<>();
        urls.add("https://news.google.gr/");
        urls.add("http://www.gazzetta.gr/");
        urls.add("https://www.insider.gr/");
        urls.add("https://www.news247.gr/");
        urls.add("https://www.cnn.gr/");
    }

    public ArrayList<String> getTargetURLs(){return this.urls;}

    public int totalURLs() {
        return this.urls.size();
    }

    public boolean isFull() {
        return this.urls.size() == 10;
    }

    public boolean isEmpty() {
        return this.urls.isEmpty();
    }

    public boolean isValidURL(String urlStr) {
        try {
            URL url = new URL(urlStr);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public boolean addURL(String url) {
        if (isFull() || !isValidURL(url))
            return false;
        else
            this.urls.add(url);
        return true;
    }

    public boolean deleteURL(int toDelete) {
        if (isEmpty() || (toDelete >= this.urls.size()))
            return false;
        else {
            this.urls.remove(toDelete);
            return true;
        }
    }

    public void display() {
        var i = 0;
        for (String tmp : this.urls)
            System.out.println(i++ + ") " + tmp);
    }

    public void options() {
        System.out.println(" **Edit target Urls **");
        System.out.println("Press 1 to see url's list");
        System.out.println("Press 2 to delete a url from list");
        System.out.println("Press 3 to add a new url");
        System.out.println("Press 4 to return to main");
    }

    public void handleTheUrl() throws Exception {
        this.display();
        int selected = 0;
        Scanner input = new Scanner(System.in);

        try {
            System.out.println(" **Edit target Urls menu**");
            System.out.println("Press 1 to see url's list");
            System.out.println("Press 2 to delete a url from list");
            System.out.println("Press 3 to add a new url");
            System.out.println("Press 4 to return main menu");
            selected = input.nextInt();

            if (selected == 4) return;
            else if (selected == 1) display();
            else if (selected == 2) {
                System.out.println("Give the number of the url you want to delete");
                Scanner del = new Scanner(System.in);
                int toDel = input.nextInt();
                while (!deleteURL(toDel)) {
                    System.out.println("Enter an integer from the list");
                    toDel = input.nextInt();
                }
                System.out.println("Url deleted");
                display();
                return;
            } else if (selected == 3) {
                System.out.println("Enter the url");
                Scanner add = new Scanner(System.in);
                String toAdd = add.nextLine();
                while (!addURL(toAdd)) {
                    System.out.println("Url format error, try again");
                    toAdd = add.nextLine();
                }
                System.out.println("Url inserted\n");
                display();
                return;
            } else {
                System.out.println("Wrong input, returing");
                return;
            }
        } catch (Exception ex) {
            System.out.println("Wrong input, please try again");
        }

    }
}