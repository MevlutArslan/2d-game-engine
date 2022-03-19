package components;

import engine.Component;
import engine.Entity;
import engine.GameWindow;
import engine.input.KeyListener;
import engine.physics.components.RaycastInfo;
import engine.physics.components.RigidBody2d;
import engine.rendering.DebugDraw;
import engine.utility.AssetPool;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {

    private float moveSpeed = 1.9f;
    private float jumpBoost = 0.5f;
    private float jumpImpulse = 3.0f;
    private float slowDownForce = 0.5f;
    // maximum velocity attainable
    private Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);

    // I dont want these values to show in the properties panels so I am marking them as transient
    private transient boolean isJumping = false;
    private transient boolean isOnGround = false;
    private transient float groundDebounce = 0.0f;
    private transient float groundDebounceTime = 0.1f;
    private transient int jumpTime = 0;
    private transient float playerWidth;
    private transient RigidBody2d rigidBody2d;
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f currentVelocity = new Vector2f();


    @Override
    public void start() {
        this.rigidBody2d = parent.getComponent(RigidBody2d.class);
        this.playerWidth = parent.transform.scale.x;
        this.rigidBody2d.setGravityScale(0.0f);
    }

    @Override
    public void update(float deltaTime) {
        if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
            // if we make the player's width positive then its facing right
            this.parent.transform.scale.x = this.playerWidth;
            this.acceleration.x = this.moveSpeed;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
            this.parent.transform.scale.x = -this.playerWidth;
            this.acceleration.x = -this.moveSpeed;
        } else {
            this.acceleration.x = 0;
            if (this.currentVelocity.x > 0) {
                this.currentVelocity.x = Math.max(0, this.currentVelocity.x - this.slowDownForce);
            } else if (this.currentVelocity.x < 0) {
                this.currentVelocity.x = Math.min(0, this.currentVelocity.x + this.slowDownForce);
            }
        }

        checkOnGround();
        if (KeyListener.isKeyPressed(GLFW_KEY_SPACE) && (jumpTime > 0 || isOnGround || groundDebounce > 0)) {
            if ((isOnGround || groundDebounce > 0) && jumpTime == 0) {
                AssetPool.getSound("src/main/resources/audio/mixkit-player-jumping-in-a-video-game-2043.ogg").play();
                jumpTime = 28;
                this.currentVelocity.y = jumpImpulse;
            } else if (jumpTime > 0) {
                jumpTime--;
                this.currentVelocity.y = ((jumpTime / 2.2f) * jumpBoost);
            } else {
                this.currentVelocity.y = 0;
            }
        }else if (!isOnGround) {
            if (this.jumpTime > 0) {
                this.currentVelocity.y *= 0.35f;
                this.jumpTime = 0;
            }
            groundDebounce -= deltaTime;
            this.acceleration.y = GameWindow.getPhysics().getGravity().y * 0.7f;
        }else {
            this.currentVelocity.y = 0;
            this.acceleration.y = 0;
            groundDebounce = groundDebounceTime;
        }

        this.acceleration.y = GameWindow.getPhysics().getGravity().y * 0.7f;

        this.currentVelocity.x += this.acceleration.x * deltaTime;
        this.currentVelocity.y += this.acceleration.y * deltaTime;
        this.currentVelocity.x = Math.max(Math.min(this.currentVelocity.x, this.terminalVelocity.x), -this.terminalVelocity.x);
        this.currentVelocity.y = Math.max(Math.min(this.currentVelocity.y, this.terminalVelocity.y), -this.terminalVelocity.y);

        this.rigidBody2d.setVelocity(this.currentVelocity);
        this.rigidBody2d.setAngularVelocity(0);
    }

    public void checkOnGround() {
        float innerPlayerWidth = this.playerWidth * 0.6f;
        float yValue = -0.23f;
        //Left side raycast
        Vector2f leftRaycastBegin = new Vector2f(this.parent.transform.position);
        leftRaycastBegin.sub(innerPlayerWidth / 2.0f, 0.0f);
        Vector2f leftRaycastEnd = new Vector2f(leftRaycastBegin).add(0.0f, yValue);

        RaycastInfo leftRaycastInfo = GameWindow.getPhysics().raycast(this.parent, leftRaycastBegin, leftRaycastEnd);

        Vector2f rightRaycastBegin = new Vector2f(leftRaycastBegin).add(innerPlayerWidth, 0.0f);
        Vector2f rightRaycastEnd = new Vector2f(leftRaycastEnd).add(innerPlayerWidth, 0.0f);
        RaycastInfo rightRaycastInfo = GameWindow.getPhysics().raycast(this.parent, rightRaycastBegin, rightRaycastEnd);

        isOnGround = (leftRaycastInfo.hit && leftRaycastInfo.hitEntity != null && leftRaycastInfo.hitEntity.getComponent(Ground.class) != null) ||
                (rightRaycastInfo.hit && rightRaycastInfo.hitEntity != null && rightRaycastInfo.hitEntity.getComponent(Ground.class) != null);

        DebugDraw.addLine2d(leftRaycastBegin, leftRaycastEnd, new Vector3f(1,0,0));
        DebugDraw.addLine2d(rightRaycastBegin, rightRaycastEnd, new Vector3f(1,0,0));

    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public float getMoveSpeed() {
        return this.moveSpeed;
    }

    @Override
    public void beginCollision(Entity collidingEntity, Contact contact, Vector2f contactNormal) {
        if (parent.isDead()) return;

        if (collidingEntity.getComponent(Ground.class) != null) {
            if (Math.abs(contactNormal.x) > 0.8f) {
                this.currentVelocity.x = 0;
            } else if (contactNormal.y > 0.8f) {
                this.currentVelocity.y = 0;
                this.acceleration.y = 0;
                this.jumpTime = 0;
            }
        }
    }
}
