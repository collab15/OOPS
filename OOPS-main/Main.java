public class Main {
    public static void main(String[] args) {

        KeyboardListener.start();

        TaskManager taskManager = new TaskManager();

        //intro screen
        UI.cls();
        System.out.println();
        UI.printFullWidth("=");
        UI.printEmpty();
        UI.printCenter("████████   █████    █████   ██   ██    ██   ██");
        UI.printCenter("   ██     ██   ██   ██      ██  ██      ██ ██ ");
        UI.printCenter("   ██     ███████   █████   █████        ███  ");
        UI.printCenter("    ██     ██   ██      ██   ██  ██      ██ ██  ");
        UI.printCenter("   ██     ██   ██   █████   ██   ██    ██   ██");
        UI.printEmpty();
        UI.printFullWidth("=");
        UI.printEmpty();
        UI.printCenter("Syncing...");
        UI.printEmpty();
        UI.printFullWidth("=");

        PartitionManager.sync(taskManager); // syncing storage and remote db

        taskManager.loadTasks();// loading tasks from storage


        SessionManager sessionManager = new SessionManager(taskManager);
        AIEngine.init(taskManager);

        // Pass AIEngine into MainMenu so it can re-suggest tasks on every visit
        Navigator.goTo(new MainMenu(taskManager, sessionManager));

        Navigator.run();

        KeyboardListener.stop();
    }
}