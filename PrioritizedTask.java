// Pairs a Task with its computed priority score so the HeuristicEngine
// can sort and pick the top N without throwing away the Task reference.
// Kept separate from Task so Task stays a plain data object with no scoring logic baked in.
public class PrioritizedTask {

    private double priority;
    private Task task;

    public PrioritizedTask(double priority, Task task) {
        if (task == null) throw new IllegalArgumentException("Task cannot be null");
        this.priority = priority;
        this.task     = task;
    }

    public double getPriority() { return priority; }
    public Task   getTask()     { return task; }
}
