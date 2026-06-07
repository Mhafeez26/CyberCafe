package observer;

// Design Pattern: OBSERVER — session events listner interface
public interface SessionObserver {
    void onSessionStarted(int computerId);
    void onSessionEnded(int computerId);
}
