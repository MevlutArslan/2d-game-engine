package engine.physics;

import engine.Component;
import engine.Entity;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class EngineContactListener implements ContactListener {

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
}
