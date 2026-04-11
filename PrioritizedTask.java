public class PrioritizedTask {

    private double priority;
    private Task task;

    public PrioritizedTask(double priority, Task task) {
        this.priority = priority;
        this.task = task;
    }

    public double getPriority() {
        return priority;
    }
    public Task getTask() { 
        return task; 
    }
}
