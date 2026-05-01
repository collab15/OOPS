// Controls the lifecycle of a work session: starting, pausing, resuming,
// and stopping the timer. Also receives timer callbacks and updates the
// task list accordingly.
//
// The timer is not hardcoded — it is passed into startSession() so the
// user can freely choose between Pomodoro or a custom duration each time.
public class SessionManager implements TimerListener {

    private Task currentTask;
    private Timer timer;                    // set at startSession(), null before then
    private boolean sessionActive = false;
    private final TaskManager taskManager;
    private int sessionDuration = 0;        // remembered so we can calculate timeSpent

    public SessionManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public Task    getCurrentTask()    { return currentTask; }
    public boolean isSessionActive()   { return sessionActive; }
    public boolean isSessionPaused()   { return timer != null && timer.isPaused(); }
    public int     getTimeRemaining()  { return timer != null ? timer.getTimeRemaining() : 0; }

    // The caller creates whichever timer they want and passes it in here.
    public void startSession(Task task, Timer chosenTimer) {
        if (task == null || chosenTimer == null || sessionActive) return;

        this.timer    = chosenTimer;
        currentTask   = task;
        sessionActive = true;

        // capture duration before start() resets the internal counter
        sessionDuration = chosenTimer.getTimeRemaining();

        timer.start(task);
    }

    public void pauseSession() {
        if (!sessionActive) return;
        timer.pause();
    }

    public void resumeSession() {
        if (!sessionActive) return;
        timer.resume();
    }

    public void stopSession() {
        if (!sessionActive) return;
        sessionActive = false;
        if (currentTask != null) {
            timer.stop(currentTask);
            currentTask = null;
        }
    }

    // timer hit zero — mark the task done
    @Override
    public void onTimerComplete() {
        if (currentTask == null) return;
        sessionActive = false;
        taskManager.completeTask(currentTask);
        currentTask = null;
    }

    // user stopped early — update how much time is left on the task
    // so the next session picks up roughly where this one left off
    @Override
    public void onTimerInterrupted(int timeRemaining) {
        if (currentTask == null) return;
        sessionActive = false;
        int timeSpent = sessionDuration - timeRemaining;
        currentTask.reduceLength(timeSpent);
        currentTask = null;
    }
}
