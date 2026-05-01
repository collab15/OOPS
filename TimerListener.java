// Callback interface that the timer fires when a session ends.
// SessionManager implements this so it can react without the timer
// needing to know anything about tasks or the UI.
public interface TimerListener {

    // fired when the countdown reaches zero naturally
    void onTimerComplete();

    // fired when the user manually stops the timer before it finishes;
    // timeRemaining tells the caller how many seconds were left
    void onTimerInterrupted(int timeRemaining);
}
