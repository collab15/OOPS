import java.io.Serializable;

// A single task the user wants to work on.
// Serializable because we write tasks to disk as .tsk files.
public class Task implements Serializable {

    private String id;
    private String name;
    private int importance;  // how much this task matters (1-10)
    private int urgency;     // how time-sensitive it is (1-10)
    private int effort;      // how hard it is to do (1-10)
    private int length;      // estimated minutes remaining

    public Task(String name, int importance, int urgency, int effort, int length) {
        this.name       = name;
        this.importance = importance;
        this.urgency    = urgency;
        this.effort     = effort;
        this.length     = length;
    }

    public String getID()        { return id; }
    public String getName()      { return name; }
    public int getImportance()   { return importance; }
    public int getUrgency()      { return urgency; }
    public int getEffort()       { return effort; }

    // guard against length going negative from reduceLength calls
    public int getLength() {
        if (length >= 0) {
            return length;
        } else {
            return 0;
        }
    }

    public void setID(String id) { this.id = id; }

    public void setLength(int length) {
        if (length >= 0) {
            this.length = length;
        }
    }

    // called at the end of a session to track how much time is left on the task
    public void reduceLength(int timeSpent) {
        if (timeSpent < 0) return;
        this.length -= timeSpent;
        if (this.length < 0) {
            this.length = 0;
        }
    }

    public void printDetails() {
        System.out.println("Task: "             + name);
        System.out.println("Importance: "       + importance);
        System.out.println("Urgency: "          + urgency);
        System.out.println("Effort: "           + effort);
        System.out.println("Remaining Length: " + length);
    }

    // two Task objects are equal if they share the same UUID —
    // name/importance/etc. can change but the ID is permanent
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        if (id == null || task.id == null) return false;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
