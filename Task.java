public class Task {

    private String name;
    private int importance;
    private int urgency;
    private int effort;
    private int length;

    public Task(String name, int importance, int urgency, int effort, int length) {
        this.name = name;
        this.importance = importance;
        this.urgency = urgency;
        this.effort = effort;
        this.length = length;
    }

    public String getName() {
        return name;
    }
    public int getImportance() {
        return importance; 
    }
    public int getUrgency() { 
        return urgency;
    }
    public int getEffort() { 
        return effort;
    }
    public int getLength() { 
        return length; 
    }

    public void setLength(int length) { 
        this.length = length;
    }
    public void reduceLength(int timeSpent) {
        this.length -= timeSpent;
    }
    public void printDetails(){
        // printf
    }
}
