package components;

import engine.Component;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class VariableConnectionTestClass extends Component {

    public int integerTest = 0;
    public float floatTest = 0.0f;
    public Vector2f vector2fTest = new Vector2f();
    public Vector3f vector3fTest = new Vector3f();
    public Vector4f vector4fTest = new Vector4f();

    private Vector4f colorTest = new Vector4f();


    @Override
    public void start() {

    }

    @Override
    public void update(float deltaTime) {

    }
}
