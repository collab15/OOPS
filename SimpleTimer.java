
public class SimpleTimer {

    private final TimerListener listener;   // who to notify 
    private final int duration;          
    private volatile int timeRemaining;       
    private volatile boolean running = false;  // is timer active

    private Thread timerThread;

    public SimpleTimer(TimerListener listener, int duration) { 
        this.listener = listener; // store who to notify 
        this.duration = duration; // store how long the timer should run 
    }

    public void start(Task task) {

        // Prevent multiple starts
        if (running) return;

        running = true; // starts the timer 
        timeRemaining = duration; // initialize here

        timerThread = new Thread(() -> { // runs the timer in the background 
            try {
                while (timeRemaining > 0 && running) {
                    Thread.sleep(1000); // wait 1 second // pauses our background thread
                    timeRemaining--; // reduce time by 1 
                }

                if (running && timeRemaining == 0) {
                    running = false;
                    listener.onTimerComplete(task);
                }

            } catch (InterruptedException e) {
                // Thread was interrupted → treat as stop
            } finally {
                if (!running && timeRemaining > 0) {
                    listener.onTimerInterrupted(task, timeRemaining);
                }
            }
        } );
        timerThread.start();
    }

    public void stop(Task task) {

        if (!running) return;

        running = false;
        // Interrupt the thread so sleep() stops immediately
        if (timerThread != null) {
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
