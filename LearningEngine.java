
import java.util.List;

public class LearningEngine {

    private double learningFactor; // controls how strong the learning is ex slow(0.1) full (1.0) agressive (x>1)
    private int extentOfBacklogToLearnFrom; // how many past task selections to learn from

    private TaskSelectionHistory history;

    public LearningEngine(double learningFactor, int extentOfBacklogToLearnFrom, TaskSelectionHistory history) {
        this.learningFactor = learningFactor;
        this.extentOfBacklogToLearnFrom = extentOfBacklogToLearnFrom;
        this.history = history;
    }

    public Delta calculateCumulativeDelta() {

        // FIX: call on the injected instance, not the class directly
        List<Delta> deltaBacklog = history.getDeltaBacklog(extentOfBacklogToLearnFrom);

        if (deltaBacklog.isEmpty()) {
            return new Delta(0, 0, 0, 0);
        }

        double d_importance = 0;
        double d_urgency    = 0;
        double d_effort     = 0;
        double d_length     = 0;
        //  going thru each past learning event in the backlog and summing it to get a cumulative delta
        //  which represents the overall learning from the past selections
        for (Delta delta : deltaBacklog) {
            d_importance += delta.getImportance();
            d_urgency    += delta.getUrgency();
            d_effort     += delta.getEffort();
            d_length     += delta.getLength();
        }

        d_importance *= learningFactor;
        d_urgency    *= learningFactor;
        d_effort     *= learningFactor;
        d_length     *= learningFactor;

        return new Delta(d_importance, d_urgency, d_effort, d_length);
    }
}
