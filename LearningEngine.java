package project;

import java.util.List;

public class LearningEngine {

    private double learningFactor; // controls how strong the learning is ex slow(0.1) full (1.0) agressive (x>1)
    private int extentOfBacklogToLearnFrom; // how many past task selections to learn from

    public LearningEngine(double learningFactor, int extentOfBacklogToLearnFrom){
        this.learningFactor = learningFactor;
        this.extentOfBacklogToLearnFrom = extentOfBacklogToLearnFrom;
    }

    public Delta calculateCumulativeDelta(){
        List<Delta> deltabacklog = TaskSelectionHistory.getDeltabacklog(extentOfBacklogToLearnFrom);
        if (deltabacklog.isEmpty()) {
            return new Delta(0,0,0,0); // if no past data no learning return zero delta
        }
        double d_importance = 0;
        double d_urgency = 0;
        double d_effort = 0;
        double d_length = 0;

        //  going thru each past learning event in the backlog and summing it to get a cumulative delta
        //  which represents the overall learning from the past selections
        for(Delta deltalog : deltabacklog){
            d_importance += deltalog.getImportance(); 
            d_urgency    += deltalog.getUrgency();
            d_effort     += deltalog.getEffort();
            d_length     += deltalog.getLength();
        }

        d_importance *= learningFactor;
        d_urgency *= learningFactor;
        d_effort *= learningFactor;
        d_length *= learningFactor;

        return new Delta(d_importance, d_urgency, d_effort, d_length);
    }
}
