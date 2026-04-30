public interface TimerListener {

    void onTimerComplete();
    void onTimerInterrupted(int timeRemaining);
}
