package engine.api_access;

public class GameWindowInitializer {

    /**
     * Determines how fast the game loop will run
     * Is 60 Frames by default
     */
    private float timeStep = 1.0f / 60.0f;

    public GameWindowInitializer(float timeStep){
        this.timeStep = timeStep;
    }
}
