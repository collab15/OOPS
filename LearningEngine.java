import java.util.List;

// Looks at the last N task selections the user made and works out how
// the weights should shift. Nothing is written here — it returns a Delta
// and lets AIEngine decide when to apply it.
public class LearningEngine {

    // 0.1 = cautious / slow drift, 0.3 = default, >1.0 = very aggressive
    private double learningFactor;

    // how far back in history to look — older selections are included
    // but their influence is diluted by the average
    private int extentOfBacklogToLearnFrom;

    private TaskSelectionHistory history;

    public LearningEngine(double learningFactor,
                          int extentOfBacklogToLearnFrom,
                          TaskSelectionHistory history) {
        this.learningFactor              = learningFactor;
        this.extentOfBacklogToLearnFrom  = extentOfBacklogToLearnFrom;
        this.history                     = history;
    }

    // sums all recent deltas, averages them so a single outlier doesn't
    // dominate, then scales by the learningFactor
    public Delta calculateCumulativeDelta() {

        List<Delta> deltaBacklog = history.getDeltaBacklog(extentOfBacklogToLearnFrom);

        if (deltaBacklog.isEmpty()) {
            return new Delta(0, 0, 0, 0); // nothing to learn from yet
        }

        double d_importance = 0;
        double d_urgency    = 0;
        double d_effort     = 0;
        double d_length     = 0;

        for (Delta delta : deltaBacklog) {
            d_importance += delta.getImportance();
            d_urgency    += delta.getUrgency();
            d_effort     += delta.getEffort();
            d_length     += delta.getLength();
        }

        int n = deltaBacklog.size();

        // average across all signals so the result doesn't blow up as history grows
        d_importance /= n;
        d_urgency    /= n;
        d_effort     /= n;
        d_length     /= n;

        // scale by how aggressively we want weights to move
        d_importance *= learningFactor;
        d_urgency    *= learningFactor;
        d_effort     *= learningFactor;
        d_length     *= learningFactor;

        return new Delta(d_importance, d_urgency, d_effort, d_length);
    }
}
