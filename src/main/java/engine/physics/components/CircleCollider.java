package engine.physics.components;

import engine.Component;

// https://www.youtube.com/watch?v=ltsq2GXjsuw&list=PLtrSb4XxIVbp8AKuEAlwNXDxr99e3woGE&index=38
public class CircleCollider extends Collider {
    private float radius = 1f;

    @Override
    public void start() {

    }

    @Override
    public void update(float deltaTime) {

    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
