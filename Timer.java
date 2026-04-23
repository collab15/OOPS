// Contract for all timer types in the app.
// SessionManager depends on this interface, not on any concrete timer class,
// so you can swap in a different timer without touching SessionManager.
public interface Timer {

    void start(Task task);
    void pause();          // freezes countdown, keeps timeRemaining intact
    void resume();         // continues countdown from where it paused
    void stop(Task task);  // cancels session entirely, fires onTimerInterrupted
    int getTimeRemaining();
    boolean isRunning();
    boolean isPaused();
}