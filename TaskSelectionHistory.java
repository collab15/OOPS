
package project;

import java.util.List;
import java.util.ArrayList;

public class TaskSelectionHistory {

    // instance field  history belongs to one history object, not a global
    private List<Delta> deltas = new ArrayList<>();  // stores all the deltas

    public void addDelta(Delta delta) {
        deltas.add(delta);
    }

    // FIX: instance method so LearningEngine can call it on the injected object
    public List<Delta> getDeltaBacklog(int n) {

        if (n <= 0) return new ArrayList<>();

        List<Delta> lastNDeltas = new ArrayList<>();

        int size  = deltas.size();
        int start = Math.max(0, size - n);

        for (int i = size - 1; i >= start; i--) {
            lastNDeltas.add(deltas.get(i));
        }
        // should we reverse the list to have the most recent delta at the end of the list???
        //Collections.reverse(last_n_Deltas); // this is just for display 

        return new ArrayList<>(lastNDeltas);
    }
}

