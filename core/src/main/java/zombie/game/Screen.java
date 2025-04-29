package zombie.game;

public interface Screen {
    void init();
    void update();
    void render(float delta);
    void resize(int width, int height); // Make sure this is here
    void hide();
    void pause();
    void resume();
}