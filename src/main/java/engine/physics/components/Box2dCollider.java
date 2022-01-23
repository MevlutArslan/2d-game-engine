package engine.physics.components;

import engine.Component;
import org.joml.Vector2f;

// https://www.youtube.com/watch?v=ltsq2GXjsuw&list=PLtrSb4XxIVbp8AKuEAlwNXDxr99e3woGE&index=38
public class Box2dCollider extends Collider {
    private Vector2f halfSize = new Vector2f(1,1);
    private Vector2f origin = new Vector2f();

    @Override
    public void start() {

    }

    @Override
    public void update(float deltaTime) {

    }

    public Vector2f getHalfSize() {
        return halfSize;
    }

    public void setHalfSize(Vector2f halfSize) {
        this.halfSize = halfSize;
    }


    public Vector2f getOrigin() {
        return origin;
    }
}
