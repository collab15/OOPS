import java.util.List;
import java.util.ArrayList;

public class TaskSelectionHistory {

    // instance field history belongs to one history object, not a global
    private List<Delta> deltas = new ArrayList<>();  // stores all the deltas

    public void addDelta(Delta delta) {
        deltas.add(delta);
    }

    public List<Delta> getDeltaBacklog(int n) {

        if (n <= 0) return new ArrayList<>();

        List<Delta> lastNDeltas = new ArrayList<>();

        int size  = deltas.size();
        int start = Math.max(0, size - n);

        for (int i = size - 1; i >= start; i--) {
            lastNDeltas.add(deltas.get(i));
        }

        return lastNDeltas;
    }
}