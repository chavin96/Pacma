package pacman.view.observer;

//Subject interface for implementing the Observer pattern.

public interface Subject {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}
