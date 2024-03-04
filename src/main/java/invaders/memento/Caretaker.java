package invaders.memento;

public class Caretaker {

    private Memento previousState = null;

    /**
     * Records the previous state (only one allowed at any given time)
     * @param previousState The state to be recorded
     */
    public void setPreviousState(Memento previousState) {
        this.previousState = previousState;
    }

    /**
     * Gets the previous state recorded
     * @return The previous state recorded
     */
    public Memento getPreviousState() {
        return previousState;
    }

}
