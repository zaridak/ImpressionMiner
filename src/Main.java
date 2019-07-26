import java.util.ArrayList;
import main.menu;
import main.myThread;
import targetURL.targetURL;

public class Main {   // ignore

    public static void main(String[] lala) {
        menu myMenu = new menu(new targetURL());//targetURL urls = new targetURL();
        //START MENU WORKING GOOD !!!!!!!!!!!!!

        do {
            try {
                myMenu.start();//menu(urls);
            } catch (Exception ex) {
                ex.printStackTrace();
                break;
            }
        } while (true);
        //END MENU WORKING GOOD !!!!!!!!!!!!!

        System.out.println("Gursia sto main menu");
        //   THREADS DO NOT DELETE WORKING GOOD
        ArrayList<myThread> threads = new ArrayList<>();

          /*  for(int i =0;i<3;i++) // lets say number of sites,url as thread name
                threads.add(new main.myThread(String.valueOf(i)));
            Scanner in = new Scanner(System.in);
            System.out.println("Starting running threads from vec");
            System.out.println("persimission to run");
            var ints = in.nextInt();
            start(threads);
            System.out.println("next int to pause");
            ints = in.nextInt();
            pause(threads);
            ints = in.nextInt();
            resume(threads);*/
    }
}