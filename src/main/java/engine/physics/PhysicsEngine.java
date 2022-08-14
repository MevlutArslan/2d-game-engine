package engine.physics;

import components.physics.CircleCollider;
import components.physics.RigidBody;
import engine.Component;
import engine.Entity;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

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
        this.world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Entity entityA = (Entity) contact.getFixtureA().getUserData();
                Entity entityB = (Entity) contact.getFixtureB().getUserData();

                WorldManifold worldManifold = new WorldManifold();
                contact.getWorldManifold(worldManifold);

                Vector2f aContactNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
                Vector2f bContactNormal = new Vector2f(aContactNormal).negate();

                for(Component c : entityA.getAllComponents()){
                    c.beginCollision(entityB, contact, aContactNormal);
                }

                for(Component c : entityB.getAllComponents()){
                    c.beginCollision(entityA, contact, bContactNormal);
                }

            }

            @Override
            public void endContact(Contact contact) {
                Entity entityA = (Entity) contact.getFixtureA().getUserData();
                Entity entityB = (Entity) contact.getFixtureB().getUserData();

                WorldManifold worldManifold = new WorldManifold();
                contact.getWorldManifold(worldManifold);

                Vector2f aContactNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
                Vector2f bContactNormal = new Vector2f(aContactNormal).negate();

                for(Component c : entityA.getAllComponents()){
                    c.endCollision(entityB, contact, aContactNormal);
                }

                for(Component c : entityB.getAllComponents()){
                    c.endCollision(entityA, contact, bContactNormal);
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold manifold) {
                Entity entityA = (Entity) contact.getFixtureA().getUserData();
                Entity entityB = (Entity) contact.getFixtureB().getUserData();

                WorldManifold worldManifold = new WorldManifold();
                contact.getWorldManifold(worldManifold);

                Vector2f aContactNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
                Vector2f bContactNormal = new Vector2f(aContactNormal).negate();

                for(Component c : entityA.getAllComponents()){
                    c.preSolve(entityB, contact, aContactNormal);
                }

                for(Component c : entityB.getAllComponents()){
                    c.preSolve(entityA, contact, bContactNormal);
                }
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse contactImpulse) {
                Entity entityA = (Entity) contact.getFixtureA().getUserData();
                Entity entityB = (Entity) contact.getFixtureB().getUserData();

                WorldManifold worldManifold = new WorldManifold();
                contact.getWorldManifold(worldManifold);

                Vector2f aContactNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
                Vector2f bContactNormal = new Vector2f(aContactNormal).negate();

                for(Component c : entityA.getAllComponents()){
                    c.postSolve(entityB, contact, aContactNormal);
                }

                for(Component c : entityB.getAllComponents()){
                    c.postSolve(entityA, contact, bContactNormal);
                }
            }
        });
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
            bodyDef.angle = (float)(entity.transform.rotation * Math.PI / 180f);

            Body body = world.createBody(bodyDef);
            body.m_mass = rigidBody.getMass();
            rigidBody.setBody(body);

            CircleCollider circleCollider;

            if((circleCollider = entity.getComponent(CircleCollider.class)) != null){
                createCircleCollider(rigidBody, circleCollider);
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
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rigidBody.getFriction();
        fixtureDef.userData = circleCollider.parent;
        fixtureDef.isSensor = rigidBody.isSensor();
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
