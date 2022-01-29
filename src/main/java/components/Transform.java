package components;

import engine.Component;
import org.joml.Vector2f;

public class Transform extends Component {

    public Vector2f position;
    public Vector2f scale;

    public float rotation = 0;

    public Transform(){
        this.position = new Vector2f();
        this.scale = new Vector2f();
        this.allowForRemoval = false;
    }

    public Transform(Vector2f position){
        this.position = position;
        this.scale = new Vector2f();
        this.allowForRemoval = false;
    }

    public Transform(Vector2f position, Vector2f scale){
        this.position = position;
        this.scale = scale;
        this.allowForRemoval = false;
    }

    public Transform copy(){
        return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
    }

    public void copyTo(Transform to){
        to.position.set(this.position);
        to.scale.set(this.scale);
    }

    @Override
    public boolean equals(Object o){
        if(o == null) return false;
        if(!(o instanceof Transform)) return false;

        Transform t = (Transform) o;
        return t.position.equals(this.position) && t.scale.equals(this.scale);
    }

    @Override
    public void start() {

    }

    @Override
    public void update(float deltaTime) {

    }
}
