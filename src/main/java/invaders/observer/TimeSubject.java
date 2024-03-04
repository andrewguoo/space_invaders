package invaders.observer;

import java.util.ArrayList;
import java.util.List;

public class TimeSubject {

    private int time = 0;
    private List<TimeObserver> observers = new ArrayList<TimeObserver>();

    /**
     * Subscribes an observer, so that they get updates when the time changes
     * @param observer The observer that wishes to be updated when the time changes
     */
    public void attach(TimeObserver observer) {
        observers.add(observer);
    }

    /**
     * Sets the time
     * @param time The amount to set the time
     */
    public void setTime(int time) {
        this.time = time;
        notifyTimeObservers();
    }

    /**
     * Alerts all subscribers the change in time
     */
    private void notifyTimeObservers() {
        for (TimeObserver observer : observers) {
            observer.updateTime(time);
        }
    }

}
