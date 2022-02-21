package engine.physics.components;

import engine.Component;
import engine.GameWindow;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2f;

// https://pub.dev/documentation/box2d_flame/latest/box2d/Body-class.html
public class RigidBody2d extends Component {
    private Vector2f velocity = new Vector2f();

    private float angularDamping = 0.8f;
    private float linearDamping = 0.9f;
    private float mass = 0;
    private Vector2f position = new Vector2f();

    private BodyType bodyType = BodyType.DYNAMIC;

    private boolean fixedRotation = false;

    private boolean continuousCollision = true;
    private transient Body rawBody = null;

    private float friction = 0.1f;
    private float angularVelocity = 0.0f;
    private float gravityScale = 1.0f;
    // Similar to Triggers in Unreal Engine.
    private boolean isSensor = false;

    @Override
    public void start() {

    }

    public void update(float deltaTime) {
        // make sure the entity's transform is matching the physics simulation
        if (rawBody != null) {
            this.parent.transform.position.set(rawBody.getPosition().x, rawBody.getPosition().y);
            this.parent.transform.rotation = (float) Math.toDegrees(rawBody.getAngle());
        }
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);
        if (rawBody != null) {
            this.rawBody.setLinearVelocity(new Vec2(velocity.x, velocity.y));
        }
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
        if (rawBody != null) {
            this.rawBody.setAngularVelocity(angularVelocity);
        }
    }

    public float getGravityScale(){
        return this.gravityScale;
    }

    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
        if (rawBody != null) {
            this.rawBody.setGravityScale(gravityScale);
        }
    }

    public void setFriction(float friction){
        this.friction = friction;
    }

    public float getFriction(){
        return this.friction;
    }

    public void setIsSensor() {
        this.isSensor = true;
        if (rawBody != null) {
            GameWindow.getPhysics().setIsSensor(this);
        }
    }

    public void setIsNotSensor() {
        this.isSensor = false;
        if (rawBody != null) {
            GameWindow.getPhysics().setNotSensor(this);
        }
    }

    public float getAngularDamping() {
        return angularDamping;
    }

    public void setAngularDamping(float angularDamping) {
        this.angularDamping = angularDamping;
    }

    public float getLinearDamping() {
        return linearDamping;
    }

    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public boolean isFixedRotation() {
        return fixedRotation;
    }

    public void setFixedRotation(boolean fixedRotation) {
        this.fixedRotation = fixedRotation;
    }

    public boolean isContinuousCollision() {
        return continuousCollision;
    }

    public void setContinuousCollision(boolean continuousCollision) {
        this.continuousCollision = continuousCollision;
    }

    public Body getRawBody() {
        return rawBody;
    }

    public void setRawBody(Body rawBody) {
        this.rawBody = rawBody;
    }

    public void addVelocity(Vector2f forceToAdd) {
        if (rawBody != null) {
            rawBody.applyForceToCenter(new Vec2(velocity.x, velocity.y));
        }
    }

    public void addImpulse(Vector2f impulse) {
        if (rawBody != null) {
            rawBody.applyLinearImpulse(new Vec2(velocity.x, velocity.y), rawBody.getWorldCenter());
        }
    }

    public float getAngularVelocity(){
        return this.angularVelocity;
    }

    public boolean getIsSensor() {
        return this.isSensor;
    }
}
