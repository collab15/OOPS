// A 25-minute timer — a named specialization of SimpleTimer.
// Exists so that SessionManager and any other caller can express intent
public class PomodoroTimer extends SimpleTimer {

    private static final int POMODORO_DURATION = 25 * 60; // 25 minutes in seconds

    public PomodoroTimer(TimerListener listener) {
        super(listener, POMODORO_DURATION);
    }
}