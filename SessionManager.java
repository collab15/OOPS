public class SessionManager implements TimerListener {

    private final TaskManager taskManager;
    private Task currentTask;
    private PomodoroTimer timer;

    private boolean sessionActive = false;

    public SessionManager(TaskManager tm) {
        this.taskManager = tm;
        this.timer = new PomodoroTimer(this); // passes this so SessionManager listens to timer events
    }
    public Task getCurrentTask() {
        return currentTask;
    }

    public boolean isSessionActive() {
        return sessionActive;
    }
    
    public void startSession(Task task) {
        if (task == null) // do nothing if no task
            return;

        this.currentTask = task;
        this.sessionActive = true;
        timer.start(task); // starting the timer in background thread
    }

    public void stopSession() {
    // if there is no session running do nothing
        if (!sessionActive || currentTask == null)
            return;
        sessionActive = false;
        // stop timer safely interrupt the thread
        timer.stop(currentTask);
    }

    @Override
    public void onTimerComplete(Task task) {

        sessionActive = false; // session finished
        System.out.println("Task completed: " + task.getName());

        taskManager.completeTask(task); // this will mark task as completed in TaskManager
        currentTask = null;
    }

    @Override // if timer is stopped early or interrupted 
    public void onTimerInterrupted(Task task, int timeRemaining) {

        sessionActive = false;
        System.out.println("Task interrupted: " + task.getName());
        task.reduceLength(timeRemaining); /// this saves remaining time left for task
        currentTask = null;
    }

}
