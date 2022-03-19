package engine.physics;

import components.Transform;
import engine.Entity;
import engine.physics.components.*;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.joml.Vector2f;
import org.joml.Vector2i;

// https://box2d.org/documentation/md__d_1__git_hub_box2d_docs_hello.html
// https://www.youtube.com/watch?v=ltsq2GXjsuw
public class Physics2d {
    private final Vec2 gravity = new Vec2(0.0f, -10.0f);
    private final World world = new World(gravity);

    // Allow for a consistent update schedule
    private float physicsTime = 0.0f;
    private float physicsTimeStep = 1.0f / 60.0f;

    // TODO: Learn Iterative Impulse Resolver
    private int velocityIterations = 8;
    private int positionIteration = 3;

    public Physics2d() {
        world.setContactListener(new EngineContactListener());
    }

    public void add(Entity entity) {
        RigidBody2d rigidBody = entity.getComponent(RigidBody2d.class);
        if (rigidBody != null && rigidBody.getRawBody() == null) {
            Transform transform = entity.transform;

            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float) Math.toRadians(transform.rotation);
            bodyDef.position.set(transform.position.x, transform.position.y);
            // angular friction
            bodyDef.angularDamping = rigidBody.getAngularDamping();
            // linear friction
            bodyDef.linearDamping = rigidBody.getLinearDamping();
            bodyDef.fixedRotation = rigidBody.isFixedRotation();
            bodyDef.bullet = rigidBody.isContinuousCollision();
            bodyDef.gravityScale = rigidBody.getGravityScale();
            bodyDef.angularVelocity = rigidBody.getAngularVelocity();
            bodyDef.userData = rigidBody.parent;

            switch (rigidBody.getBodyType()) {
                case DYNAMIC -> bodyDef.type = BodyType.DYNAMIC;
                case STATIC -> bodyDef.type = BodyType.STATIC;
                case KINEMATIC -> bodyDef.type = BodyType.KINEMATIC;
            }

            Body body = this.world.createBody(bodyDef);
            body.m_mass = rigidBody.getMass();
            rigidBody.setRawBody(body);

            CircleCollider circleCollider;
            Box2dCollider boxCollider;
            PillboxCollider pillboxCollider;
            PlayerCollider playerCollider;

            if ((circleCollider = entity.getComponent(CircleCollider.class)) != null) {
                addCircleCollider(rigidBody, circleCollider);
                // TODO
            }
            if ((boxCollider = entity.getComponent(Box2dCollider.class)) != null) {
                addBox2DCollider(rigidBody, boxCollider);
            }
            if ((pillboxCollider = entity.getComponent(PillboxCollider.class)) != null) {
                addPillboxCollider(rigidBody, pillboxCollider);
            }

            if ((playerCollider = entity.getComponent(PlayerCollider.class)) != null) {
                addPlayerCollider(rigidBody, playerCollider);
            }
        }
    }

    public RaycastInfo raycast(Entity requestingEntity, Vector2f point1, Vector2f point2) {
        RaycastInfo callback = new RaycastInfo(requestingEntity);
        world.raycast(callback, new Vec2(point1.x, point1.y), new Vec2(point2.x, point2.y));

        return callback;
    }

    public void update(float deltaTime) {
        physicsTime += deltaTime;

        if (physicsTime >= 0.0f) {
            physicsTime -= physicsTimeStep;
            // TODO figure out the reason for index out of bound exception
            world.step(physicsTime, velocityIterations, positionIteration);
        }
    }

    public void resetCircleCollider(RigidBody2d rigidBody2d, CircleCollider circleCollider) {
        Body body = rigidBody2d.getRawBody();
        if (body == null) {
            return;
        }

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addCircleCollider(rigidBody2d, circleCollider);
        body.resetMassData();
    }

    private void addCircleCollider(RigidBody2d rigidBody2d, CircleCollider circleCollider) {
        Body body = rigidBody2d.getRawBody();
        if (body == null) {
            return;
        }
        CircleShape shape = new CircleShape();
        shape.setRadius(circleCollider.getRadius());
        // m_p is offset for circles
        shape.m_p.set(circleCollider.getOffset().x, circleCollider.getOffset().y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rigidBody2d.getFriction();
        fixtureDef.userData = circleCollider.parent;
        fixtureDef.isSensor = rigidBody2d.getIsSensor();
        body.createFixture(fixtureDef);

    }

    public void resetBox2DCollider(RigidBody2d rigidBody2d, Box2dCollider boxCollider) {
        Body body = rigidBody2d.getRawBody();
        if (body == null) {
            return;
        }

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addBox2DCollider(rigidBody2d, boxCollider);
        body.resetMassData();
    }

    private void addBox2DCollider(RigidBody2d rigidBody2d, Box2dCollider boxCollider) {
        Body body = rigidBody2d.getRawBody();
        if (body == null) {
            return;
        }
        PolygonShape shape = new PolygonShape();

        Vector2f halfSize = new Vector2f(boxCollider.getHalfSize()).mul(0.5f);
        Vector2f offset = boxCollider.getOffset();
        Vector2f origin = new Vector2f(boxCollider.getOrigin());
        shape.setAsBox(halfSize.x, halfSize.y, new Vec2(offset.x, offset.y), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rigidBody2d.getFriction();
        fixtureDef.userData = boxCollider.parent;
        fixtureDef.isSensor = rigidBody2d.getIsSensor();
        body.createFixture(fixtureDef);

    }

    public void resetPlayerCollider(RigidBody2d rigidBody, PlayerCollider playerCollider){
        Body body = rigidBody.getRawBody();
        if (body == null) return;

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addPlayerCollider(rigidBody, playerCollider);
        body.resetMassData();
    }

    private void addPlayerCollider(RigidBody2d rigidBody, PlayerCollider playerCollider) {
        Body body = rigidBody.getRawBody();
        if (body == null) {
            return;
        }

        addBox2DCollider(rigidBody, playerCollider.getTopBox());
        addCircleCollider(rigidBody, playerCollider.getBottomCircle());
    }

    public void resetPillboxCollider(RigidBody2d rigidBody2d, PillboxCollider pillboxCollider) {
        Body body = rigidBody2d.getRawBody();
        if (body == null) return;

        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++) {
            body.destroyFixture(body.getFixtureList());
        }

        addPillboxCollider(rigidBody2d, pillboxCollider);
        body.resetMassData();
    }

    private void addPillboxCollider(RigidBody2d rigidBody2d, PillboxCollider pillboxCollider) {
        Body body = rigidBody2d.getRawBody();
        if (body == null) {
            return;
        }

        addBox2DCollider(rigidBody2d, pillboxCollider.getBox());
        addCircleCollider(rigidBody2d, pillboxCollider.getTopCircle());
        addCircleCollider(rigidBody2d, pillboxCollider.getBottomCircle());
    }

    public void destroyEntity(Entity entity) {
        RigidBody2d rigidBody;
        if ((rigidBody = entity.getComponent(RigidBody2d.class)) != null) {
            if (rigidBody.getRawBody() != null) {
                world.destroyBody(rigidBody.getRawBody());
                rigidBody.setRawBody(null);
            }
        }
    }

    public void onUpdateEditor(float deltaTime) {
    }

    private int fixtureListSize(Body body) {
        int size = 0;
        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            size++;
            fixture = fixture.m_next;
        }

        return size;
    }

    public void setIsSensor(RigidBody2d rigidBody2d) {
        Body body = rigidBody2d.getRawBody();
        if (body == null) {
            return;
        }

        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            fixture.m_isSensor = true;
            fixture = fixture.m_next;
        }
    }

    public void setNotSensor(RigidBody2d rigidBody2d) {
        Body body = rigidBody2d.getRawBody();
        if (body == null) {
            return;
        }

        Fixture fixture = body.getFixtureList();
        while (fixture != null) {
            fixture.m_isSensor = false;
            fixture = fixture.m_next;
        }
    }

    public boolean isLocked() {
        return world.isLocked();
    }

    public Vector2f getGravity() {
        return new Vector2f(world.getGravity().x, world.getGravity().y);
    }
}
