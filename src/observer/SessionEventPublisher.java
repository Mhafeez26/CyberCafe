package observer;

import java.util.ArrayList;
import java.util.List;

//design pattern:OBSERVER  session events publish to all registered events
//design pattern:SINGLETON only one publisher instance in system
public class SessionEventPublisher {

    private static SessionEventPublisher instance; //singleton
    private final List<SessionObserver> observers = new ArrayList<>();

    private SessionEventPublisher() {}

    public static SessionEventPublisher getInstance() {
        if (instance == null) instance = new SessionEventPublisher();
        return instance;
    }

    public void addObserver(SessionObserver o) {
        observers.add(o);
    }

    public void removeObserver(SessionObserver o) {
        observers.remove(o);
    }

    public void notifySessionStarted(int computerId) {
        for (SessionObserver o : observers) o.onSessionStarted(computerId);
    }

    public void notifySessionEnded(int computerId) {
        for (SessionObserver o : observers) o.onSessionEnded(computerId);
    }
}
