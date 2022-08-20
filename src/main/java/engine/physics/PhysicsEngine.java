package engine.physics;

import components.physics.BoxCollider;
import components.physics.CircleCollider;
import components.physics.RigidBody;
import engine.Component;
import engine.Entity;
import engine.ui.ViewPortPanel;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

import javax.swing.text.View;

// https://box2d.org/documentation/md__d_1__git_hub_box2d_docs_dynamics.html#autotoc_md113
public class PhysicsEngine {
    private final Vec2 gravity = new Vec2(0.0f, -10.0f);
    private World world = new World(gravity);
    // A time step of 1/60 seconds will usually deliver a high quality simulation.
    private final float fixedTimeStep = 1 / 60f;
    private final int velocityIterations = 10;
    private final int positionIterations = 8;

    private float time = 0;

    public PhysicsEngine(){
        this.world.setContactListener(new MyContactListener());
    }

    // https://stackoverflow.com/questions/41493043/box2d-move-bodies-at-same-rate-regardless-of-fps
    public void update(float deltaTime){
        time += deltaTime;

        if(time >= 0.0f){
            time -= fixedTimeStep;
            world.step(fixedTimeStep, velocityIterations, positionIterations);
        }
    }

    public void add(Entity entity) {
        RigidBody rigidBody = entity.getComponent(RigidBody.class);
        if (rigidBody != null && rigidBody.getBody() == null) {

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = rigidBody.getBodyType();
            bodyDef.angularVelocity = rigidBody.getAngularVelocity();
            bodyDef.gravityScale = rigidBody.getGravityScale();
            bodyDef.position = new Vec2(entity.transform.position.x, entity.transform.position.y);
            bodyDef.fixedRotation = rigidBody.isFixedRotation();
            bodyDef.linearDamping = rigidBody.getLinearDamping();
            bodyDef.angularDamping = rigidBody.getAngularDamping();
            bodyDef.bullet = rigidBody.isBullet();
            bodyDef.active = rigidBody.isActive();
            bodyDef.allowSleep = rigidBody.allowSleep();
            bodyDef.awake = rigidBody.isAwake();
            bodyDef.userData = rigidBody.parent;

            // Radians = Degrees × π/180°. TODO : test this formula
            bodyDef.angle = (float) Math.toRadians(entity.transform.rotation);
            Body body = world.createBody(bodyDef);
            body.m_mass = rigidBody.getMass();
            rigidBody.setBody(body);

            CircleCollider circleCollider;
            BoxCollider boxCollider;

            if((circleCollider = entity.getComponent(CircleCollider.class)) != null){
                createCircleCollider(rigidBody, circleCollider);
            }

            if((boxCollider = entity.getComponent(BoxCollider.class)) != null){
                createBoxCollider(rigidBody, boxCollider);
            }


        }
    }

    public void destroyEntity(Entity entity) {
        RigidBody rigidBody;
        if ((rigidBody = entity.getComponent(RigidBody.class)) != null) {
            if (rigidBody.getBody() != null) {
                world.destroyBody(rigidBody.getBody());
                rigidBody.setBody(null);
            }
        }
    }

    public void createBoxCollider(RigidBody rigidBody, BoxCollider boxCollider){
        Body body = rigidBody.getBody();

        if(body == null){
            return;
        }

        PolygonShape shape = new PolygonShape();
        Vector2f halfSize = new Vector2f(boxCollider.getHalfSize()).mul(0.5f);
        Vector2f offset = boxCollider.getOffset();

        shape.setAsBox(halfSize.x, halfSize.y, new Vec2(offset.x, offset.y), (float) Math.toRadians(rigidBody.parent.transform.rotation));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        fixtureDef.density = rigidBody.getDensity();
        fixtureDef.friction = rigidBody.getFriction();
        fixtureDef.userData = boxCollider.parent;
        fixtureDef.isSensor = rigidBody.isSensor();
        fixtureDef.filter.categoryBits = rigidBody.getCollisionCategory();
        fixtureDef.filter.maskBits = rigidBody.getCollisionMask();

        body.createFixture(fixtureDef);

    }

    public void createCircleCollider(RigidBody rigidBody, CircleCollider circleCollider){
        Body body = rigidBody.getBody();

        if(body == null){
            return;
        }

        CircleShape shape = new CircleShape();
        shape.setRadius(circleCollider.getRadius());
        // m_p is offset for circles
        shape.m_p.set(circleCollider.getOffset().x, circleCollider.getOffset().y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = rigidBody.getDensity();
        fixtureDef.friction = rigidBody.getFriction();
        fixtureDef.userData = circleCollider.parent;
        fixtureDef.isSensor = rigidBody.isSensor();
        fixtureDef.filter.groupIndex = rigidBody.getCollisionGroup().getGroupIndex();

        body.createFixture(fixtureDef);

    }

    public static void setIsSensor(RigidBody rigidBody){
        if(rigidBody.getBody() == null){
            return;
        }
        // Linked list
        Fixture fixture = rigidBody.getBody().getFixtureList();

        while(fixture != null){
            fixture.m_isSensor = true;
            fixture = fixture.m_next;
        }
    }

    public static void setNotSensor(RigidBody rigidBody){
        if(rigidBody.getBody() == null){
            return;
        }
        // Linked list
        Fixture fixture = rigidBody.getBody().getFixtureList();

        while(fixture != null){
            fixture.m_isSensor = false;
            fixture = fixture.m_next;
        }
    }



    public Vec2 getGravity() {
        return gravity;
    }
}
