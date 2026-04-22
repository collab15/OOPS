public class Weights {

    private double importance;
    private double urgency;
    private double effort;
    private double length;

    public Weights(double i, double u, double e, double l) {
        this.importance = i;
        this.urgency = u;
        this.effort = e;
        this.length = l;
    }
    
    public void applyDelta(Delta delta) {
        this.importance += delta.getImportance();
        this.urgency += delta.getUrgency();
        this.effort += delta.getEffort();
        this.length += delta.getLength();
    }

    public double getImportance() {
        return importance; 
    }
    public double getUrgency() { 
        return urgency; 
    }
    public double getEffort() { 
        return effort;
    }
    public double getLength() { 
        return length; 
    }
}
