package components.physics;

import engine.Component;
import engine.EntityCategory;
import engine.physics.CollisionGroup;
import engine.physics.PhysicsEngine;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.joml.Vector2f;

public class RigidBody extends Component {

    private transient Vector2f velocity = new Vector2f(0,0);

    private float linearDamping = 0.0f;
    private float angularDamping = 0.0f;
    private float gravityScale = 0.0f;
    private float mass = 0.0f;
    private float angularVelocity = 0.0f;
    private float friction = 0.0f;
    private float density = 1.0f;

    private boolean isSensor = false;
    private boolean allowSleep = true;
    private boolean isAwake = true;
    private boolean isFixedRotation = true;

    // The bullet flag only affects dynamic bodies.
    private boolean isBullet = false;

    // This means the body will not participate in collisions, ray casts, etc.
    private boolean isActive = true;

    private BodyType bodyType = BodyType.STATIC;

    private transient Body body = null;

    private CollisionGroup collisionGroup = CollisionGroup.ALL;

    private int collisionCategory = 0;
    private int collisionMask = 0;

    // to sycn up our entity with the physics world we use this method
    public void update(float deltaTime) {
        if (body != null) {
            if (this.bodyType == BodyType.DYNAMIC || this.bodyType == BodyType.KINEMATIC) {
                this.parent.transform.position.set(body.getPosition().x, body.getPosition().y);
                this.parent.transform.rotation = (float) Math.toDegrees(body.getAngle());
                Vec2 vel = body.getLinearVelocity();
                this.velocity.set(vel.x, vel.y);
            } else if (this.bodyType == BodyType.STATIC) {
                this.body.setTransform(
                        new Vec2(this.parent.transform.position.x, this.parent.transform.position.y),
                        (float)Math.toRadians(this.parent.transform.rotation));
            }
        }
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
    }

    public void setAngularDamping(float angularDamping) {
        this.angularDamping = angularDamping;
    }

    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public void setAllowSleep(boolean allowSleep) {
        this.allowSleep = allowSleep;
    }

    public void setAwake(boolean awake) {
        isAwake = awake;
    }

    public void setFixedRotation(boolean fixedRotation) {
        isFixedRotation = fixedRotation;
    }

    public void setBullet(boolean bullet) {
        isBullet = bullet;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public float getLinearDamping() {
        return linearDamping;
    }

    public float getAngularDamping() {
        return angularDamping;
    }

    public float getGravityScale() {
        return gravityScale;
    }

    public float getMass() {
        return mass;
    }

    public boolean allowSleep() {
        return allowSleep;
    }

    public boolean isAwake() {
        return isAwake;
    }

    public boolean isFixedRotation() {
        return isFixedRotation;
    }

    public boolean isBullet() {
        return isBullet;
    }

    public boolean isActive() {
        return isActive;
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public CollisionGroup getCollisionGroup() {
        return collisionGroup;
    }

    public void setCollisionGroup(CollisionGroup collisionGroup) {
        this.collisionGroup = collisionGroup;
    }


    public boolean isSensor() {
        return isSensor;
    }

    public void setIsSensor() {
        this.isSensor = true;
        if(body != null){
            PhysicsEngine.setIsSensor(this);
        }
    }

    public void setIsNotSensor(){
        this.isSensor = false;
        if (body != null) {
            PhysicsEngine.setNotSensor(this);
        }
    }


    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }


    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
        body.resetMassData();
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);
        if (body != null) {
            this.body.setLinearVelocity(new Vec2(velocity.x, velocity.y));
        }
    }

    public void setVelocity(float x, float y) {
        this.velocity.set(x, y);
        if(body != null){
            this.body.setLinearVelocity(new Vec2(x, y));
        }
    }

    public void setCollisionMask(int mask){
        this.collisionMask = mask;
    }

    public void addCollisionMask(int mask){
        this.collisionMask |= mask;
    }

    public int getCollisionMask(){
        return this.collisionMask;
    }

    public void setCollisionCategory(int category){
        this.collisionCategory = category;
    }

    public int getCollisionCategory(){
        return this.collisionCategory;
    }

}
