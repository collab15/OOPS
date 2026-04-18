public class SessionManager implements TimerListener {

    private final TaskManager taskManager;
    private Task currentTask;
    private PomodoroTimer timer;

    private boolean sessionActive = false;

    public SessionManager(TaskManager tm) {
        this.taskManager = tm;
        this.timer = new PomodoroTimer(this);
    }
    public Task getCurrentTask() {
        return currentTask;
    }

    public boolean isSessionActive() {
        return sessionActive;
    }
    
    public void startSession(Task task) {
        if (task == null) return;

        this.currentTask = task;
        this.sessionActive = true;
        timer.start(task);
    }

    public void stopSession() {

        if (!sessionActive || currentTask == null) return;
        sessionActive = false;
        // stop timer safely
        timer.stop(currentTask);
    }

    @Override
    public void onTimerComplete(Task task) {

        sessionActive = false;

        System.out.println("Task completed: " + task.getName());

        taskManager.completeTask(task);
        currentTask = null;
    }

    @Override
    public void onTimerInterrupted(Task task, int timeRemaining) {

        sessionActive = false;
        System.out.println("Task interrupted: " + task.getName());
        task.reduceLength(timeRemaining);
        currentTask = null;
    }

}
