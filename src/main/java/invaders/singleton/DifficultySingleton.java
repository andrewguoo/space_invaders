package invaders.singleton;

import invaders.engine.GameWindow;

public class DifficultySingleton {
    private String currentDifficulty;
    private static DifficultySingleton instance = null;

    /**
     * Gets the only instance of the difficulty in the system, as per Singleton rules
     * @return The only difficulty instance in the system
     */
    public static DifficultySingleton getInstance(){
        if (instance == null){
            instance = new DifficultySingleton();
        }
        return instance;
    }

    /**
     * Returns the difficulty level chosen
     * @return The difficulty level chosen
     */
    public String getCurrentDifficulty() {
        return currentDifficulty;
    }

    /**
     * Sets the difficulty level
     * @param difficulty The difficulty to set
     */
    public void setDifficulty(String difficulty) {
        currentDifficulty = difficulty;
    }

}