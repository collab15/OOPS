public interface Timer {

    void start(Task task);
    void pause();
    void stop (Task task);
    int getTimeRemaining();
    boolean isRunning();
}