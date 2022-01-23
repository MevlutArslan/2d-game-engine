package engine;

public interface IGameLogic {

    void init() throws Exception;

    void input(GameWindow gameWindow);

    void update(float interval);

    void render(GameWindow gameWindow);
}
