package engine.physics.components;

import engine.Entity;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;

public class RaycastInfo implements RayCastCallback {

    public Fixture fixture;
    public Vector2f point;
    public Vector2f normal;
    public float fraction;
    public boolean hit;
    public Entity hitEntity;
    private Entity requestingEntity;

    public RaycastInfo(Entity entity){
        fixture = null;
        point = new Vector2f();
        normal = new Vector2f();
        fraction = 0.0f;
        hit = false;
        hitEntity = null;
        this.requestingEntity = entity;
    }
    @Override
    public float reportFixture(Fixture fixture, Vec2 pointHit, Vec2 normal, float fraction) {
        if (fixture.m_userData == requestingEntity){
            return 1;
        }
        this.fixture = fixture;
        this.point = new Vector2f(point.x, point.y);
        this.normal = new Vector2f(normal.x, normal.y);
        this.fraction = fraction;
        this.hit = fraction != 0;
        this.hitEntity = (Entity) fixture.m_userData;

        return fraction;
    }
}
