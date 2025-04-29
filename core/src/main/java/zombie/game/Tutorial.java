package zombie.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class Tutorial implements Screen {

    @Override
    public void init() {
        // Initialize any components if needed (e.g., fonts, assets)
    }

    @Override
    public void update() {
        // Check if ENTER key is pressed to start the game
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            GameState.setCurrentScreen(new GameScreen()); // Switch to gameplay screen
        }
    }

    @Override
    public void render(float delta) {
        // Clear the screen to prepare for rendering new content
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the tutorial text (simple instructions)
        Utils.drawText("Tutorial", 200, 300);
        Utils.drawText("WASD - Move", 200, 270);
        Utils.drawText("Left Click - Shoot", 200, 240);
        Utils.drawText("Right Click - Switch Weapon", 200, 210);
        Utils.drawText("Press ENTER to Start", 200, 180);
    }

    @Override
    public void resize(int width, int height) {
        // Handle resizing of the window if needed
    }

    @Override
    public void hide() {
        // Cleanup if needed when this screen is hidden
    }

    @Override
    public void pause() {
        // Handle pause logic if needed (e.g., stop animations)
    }

    @Override
    public void resume() {
        // Handle resume logic if needed (e.g., restart animations)
    }
}
