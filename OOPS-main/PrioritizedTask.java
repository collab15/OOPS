
public class PrioritizedTask {

    private double priority;
    private Task task;

    public PrioritizedTask(double priority, Task task) {
        if (task == null) throw new IllegalArgumentException("Task cannot be null");
        this.priority = priority;
        this.task = task;
    }

    public double getPriority() {
        return priority;
    }

    // add setter if AI will recalculate scores dynamically
    public Task getTask() { 
        return task; 
    }
}
