import java.io.Serializable;

// Represents how much the AI weights should shift after a user picks a task.
// Stored in history so the LearningEngine can look back and average past signals.
// Serializable because we persist the history list to disk.
public class Delta implements Serializable {

    private static final long serialVersionUID = 1L;

    private double importance;
    private double urgency;
    private double effort;
    private double length;

    public Delta(double i, double u, double e, double l) {
        this.importance = i;
        this.urgency    = u;
        this.effort     = e;
        this.length     = l;
    }

    // Compares what the user chose vs. what the AI suggested and turns that
    // difference into a learning signal. If the user picks something more
    // important/urgent than the suggestion, importance/urgency deltas go
    // positive so the weights nudge in that direction next time.
    // Effort and length are inverted — if the user skipped a hard/long task,
    // those weights should go down slightly.
    public static Delta fromTaskComparison(Task selected, Task suggested) {

        double importance = selected.getImportance() - suggested.getImportance();
        double urgency    = selected.getUrgency()    - suggested.getUrgency();
        double effort     = suggested.getEffort()    - selected.getEffort();
        double length     = suggested.getLength()    - selected.getLength();

        return new Delta(importance, urgency, effort, length);
    }

    public double getImportance() { return importance; }
    public double getUrgency()    { return urgency; }
    public double getEffort()     { return effort; }
    public double getLength()     { return length; }
}
