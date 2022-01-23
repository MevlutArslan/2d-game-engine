package engine.observers;

import engine.Entity;

// Game Programming Patterns by Robert Nystrom
public interface Observer {
    void onNotify(Entity entity, Event event);
}
