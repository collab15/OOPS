import java.io.Serializable;

public class Task implements Serializable {

    private String id;
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

    public String getID(){
        return id;
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
        if(length >= 0) {
            return length;
        } else {
            return 0;
        }
    }

    public void setID(String id){
        this.id = id;
    }
    public void setLength(int length) {
        if (length >= 0) {
            this.length = length;
        }
    }

    public void reduceLength(int timeSpent) {
        if (timeSpent < 0) return;
        this.length -= timeSpent;
        if (this.length < 0) {
            this.length = 0;
        }
    }

    public void printDetails() {
        System.out.println("Task: " + name);
        System.out.println("Importance: " + importance);
        System.out.println("Urgency: " + urgency);
        System.out.println("Effort: " + effort);
        System.out.println("Remaining Length: " + length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
