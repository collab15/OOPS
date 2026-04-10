import java.util.List;
import java.util.ArrayList;

public abstract class TaskSelectionHistory {

    static List<Delta> deltas= new ArrayList<>();

    static List<Delta> getDeltabacklog(int n){

        List<Delta> last_n_Deltas = new ArrayList<>();
        
        int size = deltas.size();
        int start = Math.max(0, size - n);

        for (int i = size - 1; i >= start; i--) {
            last_n_Deltas.add(deltas.get(i));
        }

        return last_n_Deltas;
    }
    
}
