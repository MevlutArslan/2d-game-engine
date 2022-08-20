package components.physics;

import engine.Component;
import engine.physics.Collider;
import engine.rendering.DebugDraw;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class BoxCollider extends Collider {
    private Vector2f halfSize = new Vector2f();
    private Vector2f offset = new Vector2f();

    private transient Vector2f fullSize = new Vector2f();

    @Override
    public void onUpdateEditor(float deltaTime){
        Vector2f center = new Vector2f(this.parent.transform.position).add(this.offset);

        DebugDraw.drawSquare(center, this.halfSize, this.parent.transform.rotation);
    }

    public Vector2f getHalfSize() {
        return halfSize;
    }

    public void setHalfSize(Vector2f halfSize) {
        this.halfSize = halfSize;
    }

    public Vector2f getOffset() {
        return offset;
    }

    public void setOffset(Vector2f offset) {
        this.offset = offset;
    }
}
