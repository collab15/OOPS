import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.logging.Level;
import java.util.logging.Logger;

public class KeyboardListener implements NativeKeyListener {

    private static String lastKey = null;
    private static final Object lock = new Object();
    private static volatile boolean running = false;

    private static volatile boolean acceptingInput = true;

    // ---------------- START ----------------
    public static void start() {
        if (running) return;

        try {
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

    // ---------------- INPUT CONTROL ----------------
    public static void pause() {
        acceptingInput = false;
    }

    public static void resume() {
        synchronized (lock) {
            acceptingInput = true;
            lock.notifyAll();
        }
    }

    // ---------------- LISTENER ----------------
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

    // ---------------- KEY EVENTS ----------------
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {

        if (!acceptingInput) return;

        synchronized (lock) {

            switch (e.getKeyCode()) {

                case NativeKeyEvent.VC_UP:
                    lastKey = "UP";
                    break;

                case NativeKeyEvent.VC_DOWN:
                    lastKey = "DOWN";
                    break;

                case NativeKeyEvent.VC_ENTER:
                    lastKey = "ENTER";
                    break;

                case NativeKeyEvent.VC_BACKSPACE:
                    lastKey = "BACKSPACE";
                    break;

                default:
                    return;
            }

            lock.notifyAll();
        }
    }

    @Override public void nativeKeyReleased(NativeKeyEvent e) {}
    @Override public void nativeKeyTyped(NativeKeyEvent e) {}
}