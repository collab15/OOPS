// General-purpose countdown timer that runs on a background thread.
// Supports start, pause, resume, and stop.
// Notifies a TimerListener when the timer finishes naturally or is stopped early.
public class SimpleTimer implements Timer {

    private final TimerListener listener;
    private final int duration;            // total duration in seconds
    private volatile int timeRemaining;
    private volatile boolean running = false;
    private volatile boolean paused  = false;

    private final Object pauseLock = new Object(); // used to block/unblock the timer thread

    private Thread timerThread;

    public SimpleTimer(TimerListener listener, int duration) {
        this.listener = listener;
        this.duration = duration;
    }

    @Override
    public void start(Task task) {

        if (running) return;

        running = true;
        paused  = false;
        timeRemaining = duration;

        timerThread = new Thread(() -> {
            try {
                while (timeRemaining > 0 && running) {

                    // If paused, block here until resume() calls notify
                    synchronized (pauseLock) {
                        while (paused && running) {
                            pauseLock.wait();
                        }
                    }

                    if (!running) break; // stop() was called while paused

                    Thread.sleep(1000);
                    timeRemaining--;
                }

                if (running && timeRemaining == 0) {
                    running = false;
                    listener.onTimerComplete();
                }

            } catch (InterruptedException e) {
                // Interrupted by stop() — handled in finally

            } finally {
                // Fire interrupted only if stopped early (not after natural completion)
                if (!running && timeRemaining > 0) {
                    listener.onTimerInterrupted(timeRemaining);
                }
            }

        }, "timer-thread");

        timerThread.setDaemon(true); // won't block JVM shutdown
        timerThread.start();
    }

    @Override
    public void pause() {
        if (!running || paused) return;
        paused = true;
        // timer thread will block itself on next loop iteration
    }

    @Override
    public void resume() {
        if (!running || !paused) return;
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll(); // unblock the timer thread
        }
    }

    @Override
    public void stop(Task task) {
        if (!running) return;

        running = false;

        // If paused, wake the thread up so it can exit cleanly
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }

        if (timerThread != null) {
            timerThread.interrupt(); // also wakes up sleep() immediately
        }
    }

    @Override
    public int getTimeRemaining() {
        return timeRemaining;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isPaused() {
        return paused;
    }
}

/*we have two threads running at the same time main thread running the ui -> keypresses wtvr
 and the timer thread counting down the seconds in the background and they both share the
  same variables running paused timeRemaining if both threads read and write the variables 
  at the same time can cause provbkems the synchronised pauseLock ensures only one thread can be here at a time 
then the wait thing when thats called timer thread falls asleep stops executing lock released 
main thread can now enter the synchronised block 

the notifyAll() wakes up any thread sleeping in the pauseLock.wait() lock released */