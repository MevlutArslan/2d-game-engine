package components.physics;

import engine.Component;
import engine.rendering.DebugDraw;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;
import org.joml.Vector3f;

class Jbox2DDebugDraw extends org.jbox2d.callbacks.DebugDraw{

    public Jbox2DDebugDraw(IViewportTransform viewport) {
        super(viewport);
    }

    @Override
    public void drawPoint(Vec2 vec2, float v, Color3f color3f) {

    }

    @Override
    public void drawSolidPolygon(Vec2[] vec2s, int i, Color3f color3f) {

    }

    @Override
    public void drawCircle(Vec2 vec2, float v, Color3f color3f) {

    }

    @Override
    public void drawSolidCircle(Vec2 vec2, float v, Vec2 vec21, Color3f color3f) {

    }

    @Override
    public void drawSegment(Vec2 vec2, Vec2 vec21, Color3f color3f) {

    }

    @Override
    public void drawTransform(Transform transform) {

    }

    @Override
    public void drawString(float v, float v1, String s, Color3f color3f) {

    }
}

public class BoxCollider extends Component {
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
