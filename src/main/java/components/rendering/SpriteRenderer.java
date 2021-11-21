package components.rendering;

import components.Component;
import components.Entity;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {

    private Vector4f color;

    public SpriteRenderer(Vector4f color){
        this.color = color;
    }

    @Override
    public void start() {

    }

    @Override
    public void update(Entity entity, float deltaTime) {

    }

    public Vector4f getColor(){
        return this.color;
    }
}
