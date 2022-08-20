package components.physics;

import engine.Component;
import engine.physics.Collider;
import engine.rendering.DebugDraw;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.joml.Vector2f;

public class CircleCollider extends Collider {
    private float radius = 0.0f;
    private Vector2f offset = new Vector2f();

    @Override
    public void onUpdateEditor(float deltaTime){
        Vector2f center = new Vector2f(this.parent.transform.position).add(this.offset);
        DebugDraw.drawCircle(center, this.radius);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Vector2f getOffset() {
        return offset;
    }

    public void setOffset(Vector2f offset) {
        this.offset = offset;
    }
}
