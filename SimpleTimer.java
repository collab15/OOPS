// General-purpose countdown timer that ticks on a background thread.
// The main thread (UI / keyboard) and the timer thread share three
// volatile fields — running, paused, timeRemaining — which is safe
// because volatile guarantees visibility across threads for simple reads
// and writes. The pauseLock object is used for the more coordinated
// pause/resume dance where one thread needs to wait for the other.
public class SimpleTimer implements Timer {

    private final TimerListener listener;
    private final int duration;           // total seconds this timer runs for
    private volatile int timeRemaining;
    private volatile boolean running = false;
    private volatile boolean paused  = false;

    // any thread that wants to pause or wake the timer must hold this lock
    private final Object pauseLock = new Object();

    private Thread timerThread;

    public SimpleTimer(TimerListener listener, int duration) {
        this.listener = listener;
        this.duration = duration;
    }

    @Override
    public void start(Task task) {

        if (running) return;

        running       = true;
        paused        = false;
        timeRemaining = duration;

        timerThread = new Thread(() -> {
            try {
                while (timeRemaining > 0 && running) {

                    // if pause() was called, block here until resume() wakes us
                    synchronized (pauseLock) {
                        while (paused && running) {
                            pauseLock.wait();  // releases the lock while sleeping
                        }
                    }

                    if (!running) break; // stop() called while we were paused

                    Thread.sleep(1000);
                    timeRemaining--;
                }

                // natural completion — only fire if nobody called stop()
                if (running && timeRemaining == 0) {
                    running = false;
                    listener.onTimerComplete();
                }

            } catch (InterruptedException e) {
                // stop() interrupted our sleep — fall through to finally

            } finally {
                // fire the interrupted callback only for early stops,
                // not after a clean completion
                if (!running && timeRemaining > 0) {
                    listener.onTimerInterrupted(timeRemaining);
                }
            }

        }, "timer-thread");

        timerThread.setDaemon(true); // won't keep the JVM alive if main exits
        timerThread.start();
    }

    @Override
    public void pause() {
        if (!running || paused) return;
        paused = true;
        // the timer thread will see paused == true on its next loop and block itself
    }

    @Override
    public void resume() {
        if (!running || !paused) return;
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll(); // wake up the waiting timer thread
        }
    }

    @Override
    public void stop(Task task) {
        if (!running) return;

        running = false;

        // if the timer is currently paused, wake it so it can exit cleanly
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }

        if (timerThread != null) {
            timerThread.interrupt(); // also breaks out of Thread.sleep immediately
        }
    }

    @Override
    public int getTimeRemaining() { return timeRemaining; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public boolean isPaused()  { return paused; }
}
