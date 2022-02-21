package engine.physics.components;

import engine.Component;
import engine.rendering.DebugDraw;
import org.joml.Vector2f;

// https://www.youtube.com/watch?v=ltsq2GXjsuw&list=PLtrSb4XxIVbp8AKuEAlwNXDxr99e3woGE&index=38
public class CircleCollider extends Component {
    private float radius = 1f;
    private Vector2f offset = new Vector2f();

    @Override
    public void onUpdateEditor(float deltaTime) {
        Vector2f center = new Vector2f(this.parent.transform.position).add(this.offset);
        DebugDraw.drawCircle(center, this.radius);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
