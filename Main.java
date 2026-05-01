public class Main {

    public static void main(String[] args) {

        // start listening for arrow keys / Enter before anything else renders
        KeyboardListener.start();

        TaskManager taskManager = new TaskManager();

        // intro / loading screen
        UI.cls();
        System.out.println();
        UI.printFullWidth("=");
        UI.printEmpty();
        UI.printCenter("████████   █████    █████   ██   ██    ██   ██");
        UI.printCenter("   ██     ██   ██   ██      ██  ██      ██ ██ ");
        UI.printCenter("   ██     ███████   █████   █████        ███  ");
        UI.printCenter("   ██     ██   ██      ██   ██  ██      ██ ██ ");
        UI.printCenter("   ██     ██   ██   █████   ██   ██    ██   ██");
        UI.printEmpty();
        UI.printFullWidth("=");
        UI.printEmpty();
        UI.printCenter("Syncing...");
        UI.printEmpty();
        UI.printFullWidth("=");

        // sync with Supabase; sets Status to ONLINE or OFFLINE
        PartitionManager.sync(taskManager);

        // load whatever .tsk files are now on disk into memory
        taskManager.loadTasks();

        SessionManager sessionManager = new SessionManager(taskManager);

        // loads weights and wires up the heuristic + learning engines
        AIEngine.init(taskManager);

        Navigator.goTo(new MainMenu(taskManager, sessionManager));

        // blocks here — processes key presses and drives menu transitions
        Navigator.run();

        KeyboardListener.stop();
    }
}
