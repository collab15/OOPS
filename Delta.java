public class Delta {

    private double importance;
    private double urgency;
    private double effort;
    private double length;

    public Delta(double i, double u, double e, double l) {
        this.importance = i;
        this.urgency = u;
        this.effort = e;
        this.length = l;
    }

    // =========================
    // STATIC FACTORY METHOD
    // =========================
    public static Delta fromTaskComparison(Task selected, Task suggested) {

        // simple heuristic difference model (you can improve later)
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