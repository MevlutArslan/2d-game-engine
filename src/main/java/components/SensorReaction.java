package components;

import components.physics.RigidBody;
import components.rendering.SpriteRenderer;
import engine.Component;
import engine.Entity;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SensorReaction extends Component {

    Vector4f prevColor;

    @Override
    public void beginCollision(Entity collidingEntity, Contact contact, Vector2f contactNormal) {
        prevColor = this.parent.getComponent(SpriteRenderer.class).getColor();
        this.parent.getComponent(SpriteRenderer.class).setColor(new Vector4f(255, 0, 0, 1));
    }

    @Override
    public void endCollision(Entity collidingEntity, Contact contact, Vector2f contactNormal) {
        this.parent.getComponent(SpriteRenderer.class).setColor(new Vector4f(0, 255, 0, 1));
    }
}
