package engine.physics.components;

import engine.Component;
import engine.GameWindow;
import org.joml.Vector2f;

public class PillboxCollider extends Component {
    private transient CircleCollider topCircle = new CircleCollider();
    private transient CircleCollider bottomCircle = new CircleCollider();
    private transient Box2dCollider box = new Box2dCollider();
    private transient boolean resetFixtureNextFrame = false;

    public float width = 0.1f;
    public float height = 0.2f;
    public Vector2f offset = new Vector2f();

    @Override
    public void start(){
        this.topCircle.parent = this.parent;
        this.bottomCircle.parent = this.parent;
        this.box.parent = this.parent;
        recalculateColliders();
    }

    @Override
    public void onUpdateEditor(float deltaTime){
        topCircle.onUpdateEditor(deltaTime);
        bottomCircle.onUpdateEditor(deltaTime);
        box.onUpdateEditor(deltaTime);

        if(resetFixtureNextFrame){
            resetFixture();
        }
    }

    @Override
    public void update(float deltaTime){
        if(resetFixtureNextFrame){
            resetFixture();
        }
    }

    public void setWidth(float newWidth){
        this.width = newWidth;
        recalculateColliders();
        resetFixture();
    }

    public void setHeight(float newHeight){
        this.height = newHeight;
        recalculateColliders();
        resetFixture();
    }


    public void recalculateColliders(){
        float circleRadius = width / 4.0f;
        float boxHeight = height - 2 * circleRadius;

        topCircle.setRadius(circleRadius);
        bottomCircle.setRadius(circleRadius);

        topCircle.setOffset(new Vector2f(offset).add(0, boxHeight / 4.0f));
        bottomCircle.setOffset(new Vector2f(offset).sub(0, boxHeight / 4.0f));

        box.setHalfSize(new Vector2f(width / 2.0f, boxHeight / 2.0f));
        box.setOffset(offset);
    }

    public void resetFixture(){
        if(GameWindow.getPhysics().isLocked()){
            resetFixtureNextFrame = true;
            return;
        }
        resetFixtureNextFrame = false;

        if(parent != null){
            RigidBody2d rigidBody2d = parent.getComponent(RigidBody2d.class);
            if(rigidBody2d != null){
                GameWindow.getPhysics().resetPillboxCollider(rigidBody2d, this);
            }
        }
    }
    public CircleCollider getTopCircle() {
        return topCircle;
    }

    public CircleCollider getBottomCircle() {
        return bottomCircle;
    }

    public Box2dCollider getBox() {
        return box;
    }

    public boolean isResetFixtureNextFrame() {
        return resetFixtureNextFrame;
    }
}
