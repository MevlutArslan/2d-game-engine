package engine;

public abstract class Component {

    public transient Entity parent;

    public abstract void start();

    public abstract void update(float deltaTime);

    public void imgui(){

    }
}
