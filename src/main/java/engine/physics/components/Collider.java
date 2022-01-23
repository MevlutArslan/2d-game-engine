package engine.physics.components;

import engine.Component;
import org.joml.Vector2f;

public abstract class Collider extends Component {

    private Vector2f offset = new Vector2f();


    @Override
    public void start() {

    }

    @Override
    public void update(float deltaTime) {

    }

    public Vector2f getOffset() {
        return offset;
    }

}
