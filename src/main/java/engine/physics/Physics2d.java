package engine.physics;

import components.Transform;
import engine.Entity;
import engine.physics.components.Box2dCollider;
import engine.physics.components.CircleCollider;
import engine.physics.components.RigidBody2d;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.joml.Vector2f;

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


    public void add(Entity entity){
        RigidBody2d rigidBody = entity.getComponent(RigidBody2d.class);
        if (rigidBody != null && rigidBody.getRawBody() == null){
            Transform transform = entity.transform;

            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float)Math.toRadians(transform.rotation);
            bodyDef.position.set(transform.position.x, transform.position.y);
            // angular friction
            bodyDef.angularDamping = rigidBody.getAngularDamping();
            // linear friction
            bodyDef.linearDamping = rigidBody.getLinearDamping();
            bodyDef.fixedRotation = rigidBody.isFixedRotation();
            bodyDef.bullet = rigidBody.isContinuousCollision();

            switch (rigidBody.getBodyType()){
                case DYNAMIC -> bodyDef.type = BodyType.DYNAMIC;
                case STATIC -> bodyDef.type = BodyType.STATIC;
                case KINEMATIC -> bodyDef.type = BodyType.KINEMATIC;
            }

            PolygonShape shape = new PolygonShape();
            CircleCollider circleCollider;
            Box2dCollider boxCollider;

            if((circleCollider = entity.getComponent(CircleCollider.class)) != null){
                shape.setRadius(circleCollider.getRadius());
                // TODO
            } else if((boxCollider = entity.getComponent(Box2dCollider.class)) != null){
                Vector2f halfSize = new Vector2f(boxCollider.getHalfSize().mul(0.5f));
                Vector2f offset = boxCollider.getOffset();
                Vector2f origin = new Vector2f(boxCollider.getOrigin());
                shape.setAsBox(halfSize.x, halfSize.y, new Vec2(origin.x, origin.y), 0);

                Vec2 pos = bodyDef.position;
                float xPos = pos.x + offset.x;
                float yPos = pos.y + offset.y;
                bodyDef.position.set(xPos, yPos);
            }

            Body body = this.world.createBody(bodyDef);
            rigidBody.setRawBody(body);
            body.createFixture(shape, rigidBody.getMass());
        }
    }

    public void update(float deltaTime){
        physicsTime += deltaTime;

        if(physicsTime >= 0.0f ){
            physicsTime -= physicsTimeStep;
            world.step(physicsTime, velocityIterations, positionIteration);
        }
    }
}
