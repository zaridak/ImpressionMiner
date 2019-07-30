package database;

import main.myThread;

import java.util.ArrayList;
import java.util.List;

public interface ResultDAO {

    List<myThread> getAllThreads();
    void saveResultsInDB(String toInsert);
    void printResults(ArrayList<myThread> allThreads);

}
