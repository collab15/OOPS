public class PomodoroTimer {

    private final TimerListener listener;
    private final int workDuration = 25 * 60;

    private volatile boolean running = false;
    private volatile int timeRemaining;

    private Thread timerThread;

    public PomodoroTimer(TimerListener listener) {
        this.listener = listener;
    }

    public void start(Task task) {

        if (running) return;

        running = true;
        timeRemaining = workDuration;

        timerThread = new Thread(() -> {
            try {
                while (timeRemaining > 0 && running) {
                    Thread.sleep(1000);
                    timeRemaining--;
                }

                if (running && timeRemaining == 0) {
                    running = false;
                    listener.onTimerComplete(task);
                }

            } catch (InterruptedException e) {

            } finally {
                if (!running && timeRemaining > 0) {
                    listener.onTimerInterrupted(task, timeRemaining);
                }
            }
        });
        timerThread.start();
    }

    public void stop(Task task) {

        if (!running) return;

        running = false;

        if (timerThread != null) { // or else nullpointerexception 
            timerThread.interrupt();
        }
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public boolean isRunning() {
        return running;
    }
}
