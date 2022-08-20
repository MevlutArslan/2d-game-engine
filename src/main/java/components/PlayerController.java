package components;

import components.physics.RigidBody;
import engine.Component;
import engine.input.KeyListener;
import engine.physics.Collider;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

// https://sharpcoderblog.com/blog/2d-platformer-character-controller
public class PlayerController extends Component {

    private float maxSpeed = 9.4f;
    private float jumpHeight = 6.5f;
    private boolean isFacingRight = true;
    private int moveDirection = 0;
    private float velocityMagnitude;

    private transient RigidBody rigidBody;
    private transient Collider collider;

    @Override
    public void start() {
        rigidBody = parent.getComponent(RigidBody.class);
        collider = parent.getComponent(Collider.class);
    }

    @Override
    public void update(float deltaTime) {
        if(KeyListener.isKeyPressed(GLFW_KEY_A) || KeyListener.isKeyPressed(GLFW_KEY_D)){
            moveDirection = KeyListener.isKeyPressed(GLFW_KEY_D) ? 1 : -1;
        }else{
            moveDirection = 0;
        }

        if(moveDirection > 0 && !isFacingRight){
            // flip
        }

        if(moveDirection < 0 && isFacingRight){
            // flip
        }

        rigidBody.setVelocity((moveDirection) * maxSpeed, rigidBody.getVelocity().y);

    }
}
