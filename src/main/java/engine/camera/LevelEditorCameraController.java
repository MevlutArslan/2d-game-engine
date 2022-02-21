package engine.camera;

import engine.Component;
import engine.GameWindow;
import engine.input.KeyListener;
import engine.input.MouseListener;
import engine.ui.ViewPortWindow;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class LevelEditorCameraController extends Component {
    // 60 frames per second is 16ms;
    private float dragThreshold = 0.016f;
    private float cameraSensitivity = 30.0f;
    private float scrollSensitivity = 0.1f;

    private boolean resetCamera = false;

    private float lerpTime = 0.0f;

    private Vector2f originalCameraPosition = new Vector2f();

    private final Camera camera = GameWindow.getScene().getCamera();

    @Override
    public void start() {

    }

    public void onUpdateEditor(float deltaTime){
        if(!ViewPortWindow.getWantCaptureMouse()){
            return;
        }
        // need to move the camera to the opposing direction at an increasing speed over deltaTime
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragThreshold > 0) {
            originalCameraPosition = new Vector2f(MouseListener.getWorldCoordsX(), MouseListener.getWorldCoordsY());
            dragThreshold -= deltaTime;
        } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            Vector2f targetCameraPosition = new Vector2f();
            targetCameraPosition.x = MouseListener.getWorldCoordsX();
            targetCameraPosition.y = MouseListener.getWorldCoordsY();

            Vector2f distanceBetween = new Vector2f(targetCameraPosition).sub(originalCameraPosition);
            camera.cameraPosition.sub(distanceBetween.mul(deltaTime).mul(cameraSensitivity));
            originalCameraPosition.lerp(targetCameraPosition, deltaTime);
        }

        if(KeyListener.isKeyPressed(GLFW_KEY_RIGHT)){
            camera.cameraPosition.add(cameraSensitivity,0);
        }
        else if(KeyListener.isKeyPressed(GLFW_KEY_LEFT)){
            camera.cameraPosition.sub(cameraSensitivity, 0);
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_UP)){
            camera.cameraPosition.add(0,cameraSensitivity);
        }
        else if(KeyListener.isKeyPressed(GLFW_KEY_DOWN)){
            camera.cameraPosition.sub(0,cameraSensitivity);
        }

        if (dragThreshold <= 0.0f && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            dragThreshold = 0.016f;
        }

        // Scrolling is inverted on MacOS
        if(MouseListener.scrollY() != 0){
            // Has to be incremented in exponential steps
            float incrementBy = (float)Math.pow(Math.abs(MouseListener.scrollY() * scrollSensitivity),
                    1/ camera.getZoomLevel());
            // Returns the signum function of the argument; zero if the argument is zero, 1.0f if the argument is greater than zero,
            // -1.0f if the argument is less than zero.
            incrementBy *=  -Math.signum(MouseListener.scrollY());

            // make sure we don't show more of the grid than the code can currently handle
            if(camera.getZoomLevel() + incrementBy < 4f && camera.getZoomLevel() + incrementBy > 0.3f){
                camera.addZoomLevel(incrementBy);
            }
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_C)) {
            resetCamera = true;
        }

        if(resetCamera){
            // TODO: Fix the camera moving infinetly. Maybe lock it to 0,0 after a certain point
            camera.cameraPosition.lerp(new Vector2f(0,0), lerpTime);
            camera.setZoomLevel(camera.getZoomLevel() +
                    ((1.0f - camera.getZoomLevel()) * lerpTime));
            lerpTime += 0.1f * deltaTime;

            if (Math.abs(camera.cameraPosition.x) <= 5.0f &&
                    Math.abs(camera.cameraPosition.y) <= 5.0f) {
                lerpTime = 0.0f;
                camera.cameraPosition.set(0f, 0f);
                camera.setZoomLevel(1.0f);
                resetCamera = false;
            }
        }
    }
}
