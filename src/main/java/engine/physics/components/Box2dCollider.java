package engine.physics.components;

import engine.Component;
import engine.rendering.DebugDraw;
import org.joml.Vector2f;

// https://www.youtube.com/watch?v=ltsq2GXjsuw&list=PLtrSb4XxIVbp8AKuEAlwNXDxr99e3woGE&index=38
public class Box2dCollider extends Component {
    private Vector2f halfSize = new Vector2f(1,1);
    private Vector2f origin = new Vector2f();
    private Vector2f offset = new Vector2f();


    @Override
    public void onUpdateEditor(float deltaTime) {
        Vector2f center = new Vector2f(this.parent.transform.position).add(this.offset);
        DebugDraw.drawSquare(center, this.halfSize, (int)this.parent.transform.rotation);
    }

    public Vector2f getOffset() {
        return this.offset;
    }

    public void setOffset(Vector2f newOffset) {
        this.offset.set(newOffset);
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
