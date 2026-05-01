import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.logging.Level;
import java.util.logging.Logger;

// Intercepts global key events using JNativeHook so the app can read
// arrow keys and Enter without the terminal needing to be in raw mode.
//
// Two threads are involved: the JNativeHook dispatch thread fires
// nativeKeyPressed() and writes lastKey; the main/UI thread calls
// listen() and blocks until a key arrives. The shared lock object
// coordinates them — wait() releases it so the other thread can enter.
public class KeyboardListener implements NativeKeyListener {

    private static String  lastKey       = null;
    private static final Object lock     = new Object();
    private static volatile boolean running       = false;
    private static volatile boolean acceptingInput = true;

    public static void start() {
        if (running) return;
        try {
            // suppress the verbose JNativeHook log output
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
            logger.setUseParentHandlers(false);

            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new KeyboardListener());
            running = true;
        } catch (Exception e) {
            System.err.println("Keyboard start failed: " + e.getMessage());
        }
    }

    public static void stop() {
        try {
            if (running) {
                GlobalScreen.unregisterNativeHook();
                running = false;
            }
        } catch (Exception e) {
            System.err.println("Keyboard stop failed: " + e.getMessage());
        }
    }

    // called before switching to Scanner-based text input (e.g. ActionHandler)
    // so arrow-key events don't bleed into the Scanner buffer
    public static void pause() {
        acceptingInput = false;
    }

    // called when returning from Scanner-based input back to key-driven navigation
    public static void resume() {
        synchronized (lock) {
            acceptingInput = true;
            lock.notifyAll();
        }
    }

    // blocks the calling thread until the next key press arrives;
    // drains any key that came in while we were paused
    public static String listen() {
        synchronized (lock) {
            while (true) {
                if (lastKey != null) {
                    String key = lastKey;
                    lastKey = null;
                    return key;
                }
                try {
                    lock.wait();
                } catch (InterruptedException ignored) {}
            }
        }
    }

    // only UP / DOWN / ENTER / BACKSPACE are meaningful to the app;
    // everything else is ignored to avoid noise
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {

        if (!acceptingInput) return;

        synchronized (lock) {
            switch (e.getKeyCode()) {
                case NativeKeyEvent.VC_UP:        lastKey = "UP";        break;
                case NativeKeyEvent.VC_DOWN:      lastKey = "DOWN";      break;
                case NativeKeyEvent.VC_ENTER:     lastKey = "ENTER";     break;
                case NativeKeyEvent.VC_BACKSPACE:  lastKey = "BACKSPACE"; break;
                default: return; // ignore everything else
            }
            lock.notifyAll(); // wake up listen()
        }
    }

    @Override public void nativeKeyReleased(NativeKeyEvent e) {}
    @Override public void nativeKeyTyped(NativeKeyEvent e)    {}
}
