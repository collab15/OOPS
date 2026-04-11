public class Delta {

    private double d_importance;
    private double d_urgency;
    private double d_effort;
    private double d_length;

    public Delta(double di, double du, double de, double dl) {
        this.d_importance = di;
        this.d_urgency = du;
        this.d_effort = de;
        this.d_length = dl;
    }

    public double getImportance() {
        return d_importance; 
    }
    public double getUrgency() { 
        return d_urgency; 
    }
    public double getEffort() {
        return d_effort; 
    }
    public double getLength() { 
        return d_length; 
    }
}
