public class Weights {
    double w_importance;
    double w_urgency;
    double w_effort;
    double w_length;

    Weights(double w_importance, double w_urgency, double w_effort, double w_length){
        this.w_importance = w_importance;
        this.w_urgency = w_urgency;
        this.w_effort = w_effort;
        this.w_length = w_length;
    }

    void updateByDelta(Delta delta){
        this.w_importance += delta.d_importance;
        this.w_urgency += delta.d_urgency;
        this.w_effort += delta.d_effort;
        this.w_length += delta.d_length;
    }
}
