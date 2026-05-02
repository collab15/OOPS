// Central config for the whole app. Split into three inner classes so
// callers are explicit about what kind of setting they're touching —
// AI tuning, user preferences, or app-level constants.
public class Settings {

    // Controls how the learning engine behaves.
    public static class AI_Settings {

        // how many past selections the LearningEngine averages over
        private static int extentOfBacklogToLearnFrom = 10;

        // how aggressively weights shift per learning cycle:
        // 0.1 = very slow, 0.3 = default, 1.0 = immediate full correction
        private static double learningFactor = 0.3;

        public static int    getBacklogSize()              { return extentOfBacklogToLearnFrom; }
        public static double getLearningFactor()           { return learningFactor; }
        public static void   setBacklogSize(int size)      { extentOfBacklogToLearnFrom = size; }
        public static void   setLearningFactor(double f)   { learningFactor = f; }
    }

    // Controls what the user sees.
    public static class UserSettings {

        // how many tasks appear on the suggested list in the main menu
        private static int numberOfTasksToSuggest = 5;

        public static int  getSuggestionCount()        { return numberOfTasksToSuggest; }
        public static void setSuggestionCount(int n)   { numberOfTasksToSuggest = n; }
    }

    // App-wide constants that rarely change.
    public static class AppSettings {

        // character width of the UI border — change this if the terminal is narrower
        private static int displayWidth = 115;

        // root folder where all local .tsk / .sys files are kept
        private static String localStorageDirectory = "local/";

        public static int    getDisplayWidth()          { return displayWidth; }
        public static String getLocalStorageDirectory() { return localStorageDirectory; }
    }

    public static void loadSettings() {
        // load logic
    }
}
