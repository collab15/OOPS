import java.util.List;

public class LearningEngine {
    private double learningFactor;
    private int extentOfBacklogToLearnFrom;

    LearningEngine(double learningFactor, int extentOfBacklogToLearnFrom){
        this.learningFactor = learningFactor;
        this.extentOfBacklogToLearnFrom = extentOfBacklogToLearnFrom;
    }

    public Delta calculateCumulativeDelta(){

        List<Delta> deltabacklog = TaskSelectionHistory.getDeltabacklog(extentOfBacklogToLearnFrom);

        if (deltabacklog.isEmpty()) {
            return new Delta(0,0,0,0);
        }

        double d_importance = 0;
        double d_urgency = 0;
        double d_effort = 0;
        double d_length = 0;

        for(Delta deltalog : deltabacklog){
            d_importance += deltalog.d_importance;
            d_urgency += deltalog.d_urgency;
            d_effort += deltalog.d_effort;
            d_length += deltalog.d_length;
        }

        d_importance *= learningFactor;
        d_urgency *= learningFactor;
        d_effort *= learningFactor;
        d_length *= learningFactor;

        Delta cumulativeDelta = new Delta(d_importance, d_urgency, d_effort, d_length);

        return cumulativeDelta;

    }

}
