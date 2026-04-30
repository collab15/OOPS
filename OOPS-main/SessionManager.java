// Controls task sessions. Starts, pauses, resumes, and stops the timer,
// and updates task status based on what the timer reports back via TimerListener.
// The timer is not hardcoded — it is passed in at startSession() time so the
// user can choose between Pomodoro (25 min) or a custom duration each session.
public class SessionManager implements TimerListener {

    private Task currentTask;
    private Timer timer;                  // set when session starts, not at construction
    private boolean sessionActive = false;
    private final TaskManager taskManager;

    // Added sessionDuration field to track the original timer duration.
    // This is needed to correctly calculate how much time was actually spent working
    // when a session is interrupted early (see onTimerInterrupted below).
    private int sessionDuration = 0;

    public SessionManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public Task getCurrentTask()  { return currentTask; }
    public boolean isSessionActive() { return sessionActive; }
    public boolean isSessionPaused() { return timer != null && timer.isPaused(); }
    public int getTimeRemaining()    { return timer != null ? timer.getTimeRemaining() : 0; }

    // Starts a session with the given task and a caller-chosen timer.
    // Caller creates the timer (PomodoroTimer or SimpleTimer) and passes it in.
    public void startSession(Task task, Timer chosenTimer) {
        if (task == null || chosenTimer == null || sessionActive) return;

        this.timer    = chosenTimer;
        currentTask   = task;
        sessionActive = true;

        //Record the full duration before starting the timer.
        // getTimeRemaining() returns the full duration before start() is called.
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

    @Override
    public void onTimerComplete() {
        if (currentTask == null) return;
        sessionActive = false;
        taskManager.completeTask(currentTask);
        currentTask = null;
    }

    @Override
    public void onTimerInterrupted(int timeRemaining) {
        if (currentTask == null) return;
        sessionActive = false;

        int timeSpent = sessionDuration - timeRemaining;
        currentTask.reduceLength(timeSpent);

        currentTask = null;
    }
}
