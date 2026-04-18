public interface TimerListener {

    void onTimerComplete(Task task);
    void onTimerInterrupted(Task task, int timeRemaining);
}
