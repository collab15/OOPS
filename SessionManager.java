package project;

public class SessionManager implements TimerListener {
// controls task sessions and interacts with the timer and task manager to update task status based on session outcomes

    private Task currentTask;
    private PomodoroTimer timer;
    private boolean sessionActive = false;
    private final TaskManager taskManager;

    public SessionManager(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.timer = new PomodoroTimer(this);
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public boolean isSessionActive() {
        return sessionActive;
    }
// sets current task starts timer marks session as activeo do nothing if task null 
    public void startSession(Task task) {
        if (task == null || sessionActive) return;

        System.out.println("Started task: " + task.getName());
        this.currentTask = task;
        this.sessionActive = true;
        timer.start(task);
    }

    public void stopSession() {
        if (!sessionActive) return;
        sessionActive = false;
        if (currentTask != null) {
            timer.stop(currentTask);
            currentTask = null;
        }
    }

    @Override // when timer finishes marks task complete resets session
    public void onTimerComplete(Task task) {
        if (task == null) return;
        sessionActive = false;
        System.out.println("Task completed: " + task.getName());

        // call on the stored instance, not the class statically
        taskManager.completeTask(task);
        currentTask = null;
    }

    @Override
    public void onTimerInterrupted(Task task, int timeRemaining) {
        if (task == null) return;// nullpointerexception without this 
        sessionActive = false;
        System.out.println("Task interrupted: " + task.getName());
        task.reduceLength(timeRemaining);
        currentTask = null;
    }
}
