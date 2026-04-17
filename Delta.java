package project;

// delta represent changes to the weights based on learning from the past task selection
public class Delta {

    private final double d_importance;
    private final double d_urgency;
    private final double d_effort;
    private final double d_length;

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

// for example Delta delta = new Delta(1.5, -1, 0.5, 0);
// this increases importance weight decreases urgency weight slight inc effort leaves length unchanged.
