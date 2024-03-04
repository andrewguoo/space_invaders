package invaders.observer;

import java.util.ArrayList;
import java.util.List;

public class ScoreSubject {

    private int score = 0;
    private List<ScoreObserver> observers = new ArrayList<ScoreObserver>();

    /**
     * Subscribes an observer, so that they get updates when the score changes
     * @param observer The observer that wishes to be updated when the score changes
     */
    public void attach(ScoreObserver observer) {
        observers.add(observer);
    }

    /**
     * Sets the score
     * @param points The amount to set the score
     */
    public void updateScore(int points) {
        score = points;
        notifyScoreObservers();
    }

    /**
     * Alerts all subscribers the change in score
     */
    private void notifyScoreObservers() {
        for (ScoreObserver observer : observers) {
            observer.updateScore(score);
        }
    }

}
