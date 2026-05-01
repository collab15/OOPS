// Contract every timer in the app must follow.
// SessionManager talks to this interface, not to any concrete class,
// so swapping in a new timer type (e.g. a future break timer) doesn't
// touch SessionManager at all.
public interface Timer {

    void start(Task task);
    void pause();           // freezes the countdown, keeps timeRemaining intact
    void resume();          // picks up from where it paused
    void stop(Task task);   // cancels the session and fires onTimerInterrupted
    int  getTimeRemaining();
    boolean isRunning();
    boolean isPaused();
}
