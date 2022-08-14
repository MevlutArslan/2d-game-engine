package engine.camera;

import engine.Component;
import engine.Entity;

// https://www.youtube.com/watch?v=qNQXOIAlbyI&t=796s
public class GameCamera extends Component {

    private transient Camera camera;
    private transient Entity player;

    public GameCamera(Camera camera){
        this.camera = camera;
    }

    @Override
    public void start(){
    }

    @Override
    public void update(float deltaTime){
//        if(player != null){
//            // This is just an example code, don't do this it will not work.
//            this.camera.cameraPosition.y = player.transform.position.y;
//        }
    }

    public Camera getCamera() {
        return camera;
    }
}
