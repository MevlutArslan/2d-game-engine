package engine.observers;

import engine.Entity;

import java.util.ArrayList;
import java.util.List;

// Game Programming Patterns by Robert Nystrom
public class EventSystem {
    private static List<Observer> observers = new ArrayList<>();

    public static void addObserver(Observer observer){
        observers.add(observer);
    }

    public static void notify(Entity entity, Event event){
        for(Observer observer : observers){
            observer.onNotify(entity, event);
        }
    }

}
