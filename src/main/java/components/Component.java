package components;

public abstract class Component {

    public Entity parent;

    public abstract void start();

    public abstract void update(float deltaTime);

    public void imgui(){

    }
}
