package engine.camera;

import engine.utility.Constants;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {

    private Matrix4f projectionMatrix, viewMatrix;
    public Vector2f cameraPosition;

    public Camera(Vector2f cameraPosition){
        this.cameraPosition = cameraPosition;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection(){
        this.projectionMatrix.identity();
        this.projectionMatrix.ortho(0, Constants.gridSize * 60,0.0f , Constants.gridSize * 34, 0.0f,  50.0f );
    }

    public Matrix4f getViewMatrix(){
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();

        this.viewMatrix.lookAt(new Vector3f(cameraPosition.x, cameraPosition.y, 30.0f),
                                            cameraFront.add(cameraPosition.x, cameraPosition.y, 30.0f),
                                            cameraUp);

        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix(){
        return this.projectionMatrix;
    }
}
