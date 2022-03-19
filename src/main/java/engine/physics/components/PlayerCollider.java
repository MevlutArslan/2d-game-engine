package engine.physics.components;

import engine.Component;
import engine.GameWindow;
import org.joml.Vector2f;

public class PlayerCollider extends Component {
    private transient Box2dCollider topBox = new Box2dCollider();
    private transient CircleCollider bottomCircle = new CircleCollider();

    private transient boolean resetFixtureNextFrame = false;

    public float width = 0.1f;
    public float height = 0.4f;
    public Vector2f offset = new Vector2f();

    @Override
    public void start(){
        this.bottomCircle.parent = this.parent;
        this.topBox.parent = this.parent;
        recalculateColliders();
    }

    @Override
    public void onUpdateEditor(float deltaTime){
        topBox.onUpdateEditor(deltaTime);
        bottomCircle.onUpdateEditor(deltaTime);

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

        bottomCircle.setRadius(circleRadius);
        bottomCircle.setOffset(new Vector2f(offset).sub(0, boxHeight / 4.0f));

        topBox.setHalfSize(new Vector2f(width / 2.0f, boxHeight / 2.0f));
        topBox.setOffset(offset);
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
                GameWindow.getPhysics().resetPlayerCollider(rigidBody2d, this);
            }
        }
    }

    public CircleCollider getBottomCircle() {
        return bottomCircle;
    }

    public Box2dCollider getTopBox() {
        return topBox;
    }

    public boolean isResetFixtureNextFrame() {
        return resetFixtureNextFrame;
    }
}
