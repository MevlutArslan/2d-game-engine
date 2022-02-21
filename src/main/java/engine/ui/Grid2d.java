package engine.ui;

import engine.Component;
import engine.GameWindow;
import engine.camera.Camera;
import engine.rendering.DebugDraw;
import engine.utility.Constants;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Grid2d extends Component {
    @Override
    public void start() {

    }

    @Override
    public void onUpdateEditor(float deltaTime) {
        Camera camera = GameWindow.getScene().getCamera();
        Vector2f cameraPos = camera.cameraPosition;
        // I put it in a vector to stop the warnining IntelliJ gave
        // Changed from a fresh vector to camera's projectionSize because of the adjustment we do when we zoom
        Vector2f projectionSize = camera.getProjectionSize();

        // grid's starting point => (x,y) / grid's size
        // -1 is necessary because it starts from right side. -1 offsets it to the left side of the first box
        float firstX = ((cameraPos.x / Constants.GRID_SIZE) - 1) * Constants.GRID_SIZE;
        float firstY = ((cameraPos.y / Constants.GRID_SIZE) - 1) * Constants.GRID_SIZE;

        int numVtLines = (int) (projectionSize.x * camera.getZoomLevel()/ Constants.GRID_SIZE) + 2;
        int numHzLines = (int) (projectionSize.y * camera.getZoomLevel()/ Constants.GRID_SIZE) + 2;

        float height =  ((projectionSize.y * camera.getZoomLevel()) + Constants.GRID_SIZE * 2);
        float width =  ((projectionSize.x * camera.getZoomLevel()) + Constants.GRID_SIZE * 2);

        int maxLines = Math.max(numVtLines, numHzLines);

        Vector3f color = new Vector3f(0.2f, 0.2f, 0.2f);
        int counter = 0;
        for (int i = 0; i < maxLines; i++) {
            float x = firstX + (Constants.GRID_SIZE * i);
            float y = firstY + (Constants.GRID_SIZE * i);

            // there will always be more vertical lines than horizontal lines for a landscape view
            if (i < numVtLines) {
                DebugDraw.addLine2d(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);
            }

            if (i < numHzLines) {
                DebugDraw.addLine2d(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);
            }
        }
    }
}
