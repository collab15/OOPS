package project;

import java.util.List;
import java.util.ArrayList;
//import java.util.Collections;

// abstract removed bec u cant create objects anyway everything is static
public class TaskSelectionHistory {
    // stores all the deltas
    private static List<Delta> deltas= new ArrayList<>();

    public static void addDelta(Delta delta) {
        deltas.add(delta);
    }

    // gets back n number of deltas from the backlog starting from the most recent one
    private static List<Delta> getDeltaBacklog(int n){

        if (n <= 0) return new ArrayList<>();

        List<Delta> last_n_Deltas = new ArrayList<>();

        int size = deltas.size();
        int start = Math.max(0, size - n);

        for (int i = size - 1; i >= start; i--) {
            last_n_Deltas.add(deltas.get(i));
        }
        // should we reverse the list to have the most recent delta at the end of the list???
        //Collections.reverse(last_n_Deltas); // this is just for display 

        return new ArrayList<>(last_n_Deltas); // instead of return last_n_Deltas; bec outside code can modify history result 
    }
}
