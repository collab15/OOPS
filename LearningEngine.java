import java.util.List;

public class LearningEngine {

    private double learningFactor;
    private int extentOfBacklogToLearnFrom;

    public LearningEngine(double learningFactor, int extentOfBacklogToLearnFrom){
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
            d_importance += deltalog.getImportance(); 
            d_urgency += deltalog.getUrgency();
            d_effort += deltalog.getEffort();
            d_length += deltalog.getLength();
        }

        d_importance *= learningFactor;
        d_urgency *= learningFactor;
        d_effort *= learningFactor;
        d_length *= learningFactor;

        return new Delta(d_importance, d_urgency, d_effort, d_length);
    }
}
