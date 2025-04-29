package zombie.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class ZombieGame extends Game {
    private static SpriteBatch batch;
    private OrthographicCamera camera;
    private static ZombieGame instance;

    @Override
    public void create() {
        instance = this;
        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 960);
        camera.update();

        // Initialize batch
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        // Initialize Utils with batch
        Utils.init(batch);
        // Initialize TextureLoader with game's batch
        TextureLoader.init(batch);
        // Initialize GameState
        GameState.init();

        // Set the initial screen to MainMenu
        try {
            setScreen(new MainMenu());
            Gdx.app.log("ZombieGame", "Main menu screen set successfully");
        } catch (Exception e) {
            Gdx.app.error("ZombieGame", "Failed to set main menu screen", e);
            e.printStackTrace();
        }
    }

    public static ZombieGame getInstance() {
        return instance;
    }

    public static SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public void render() {
        try {
            // Clear the screen
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // Let the current screen handle its own rendering
            super.render();
        } catch (Exception e) {
            Gdx.app.error("ZombieGame", "Error in render loop", e);
            e.printStackTrace();
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        camera.setToOrtho(false, 1280, 960);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        Utils.dispose();
    }
}
