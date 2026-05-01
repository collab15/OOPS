// A 25-minute timer. Just a named wrapper around SimpleTimer so
// the calling code can say "new PomodoroTimer(...)" instead of
// "new SimpleTimer(..., 25 * 60)" — makes intent obvious at a glance.
public class PomodoroTimer extends SimpleTimer {

    private static final int POMODORO_DURATION = 25 * 60; // 1500 seconds

    public PomodoroTimer(TimerListener listener) {
        super(listener, POMODORO_DURATION);
    }
}
