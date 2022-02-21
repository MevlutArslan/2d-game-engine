package engine.camera;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {

    private Matrix4f projectionMatrix, viewMatrix, inverseProjectionMatrix, inverseViewMatrix;
    public Vector2f cameraPosition;

    private float projectionWidth = 6;
    private float projectionHeight = 3;
    private Vector2f projectionSize = new Vector2f(projectionWidth, projectionHeight);

    private float zoomLevel = 1.0f;

    public Camera(Vector2f cameraPosition) {
        this.cameraPosition = cameraPosition;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjectionMatrix = new Matrix4f();
        this.inverseViewMatrix = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection() {
        this.projectionMatrix.identity();

        // first 4 parameters specify the boundaries of the projection
        this.projectionMatrix.ortho(0, projectionSize.x  * zoomLevel,
                0.0f, projectionSize.y * zoomLevel,
                0.0f, 50.0f);
        this.projectionMatrix.invert(this.inverseProjectionMatrix);
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        this.viewMatrix.identity();

        this.viewMatrix.lookAt(new Vector3f(cameraPosition.x, cameraPosition.y, 30.0f),
                cameraFront.add(cameraPosition.x, cameraPosition.y, 30.0f),
                cameraUp);

        this.viewMatrix.invert(this.inverseViewMatrix);

        return this.viewMatrix;
    }

    public Vector2f getProjectionSize() {
        return projectionSize;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f getInverseViewMatrix() {
        return this.inverseViewMatrix;
    }

    public Matrix4f getInverseProjectionMatrix() {
        return this.inverseProjectionMatrix;
    }

    public float getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(float zoomLevel){
        this.zoomLevel = zoomLevel;
    }

    public void addZoomLevel(float zoomIncrement){
        zoomLevel += zoomIncrement;
    }
}
