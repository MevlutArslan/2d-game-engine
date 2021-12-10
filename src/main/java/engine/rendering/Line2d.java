package engine.rendering;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

// https://www.youtube.com/watch?v=j6lZ8UdjpjA&list=PLtrSb4XxIVbp8AKuEAlwNXDxr99e3woGE&index=25
public class Line2d {
    private Vector2f from;
    private Vector2f to;
    private Vector3f color;
    private int lifeTime;

    public Line2d(Vector2f from, Vector2f to, Vector3f color, int lifeTime) {
        this.from = from;
        this.to = to;
        this.color = color;
        this.lifeTime = lifeTime;
    }

    public int beginFrame(){
        this.lifeTime--;
        return this.lifeTime;
    }

    public Vector2f getFrom() {
        return from;
    }

    public Vector2f getTo() {
        return to;
    }

    public Vector3f getColor() {
        return color;
    }
}
