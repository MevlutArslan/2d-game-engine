package components;

public abstract class Component {

    public abstract void start();

    public abstract void update(Entity entity, float deltaTime);
}
