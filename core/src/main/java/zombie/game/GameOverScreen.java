package zombie.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameOverScreen implements Screen {
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backgroundTexture;

    public GameOverScreen() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
        batch = new SpriteBatch();
        font = new BitmapFont();
        backgroundTexture = new Texture(Gdx.files.internal("images/ui/background_menu.png"));
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        // Draw background
        batch.draw(backgroundTexture, 0, 0, 1280, 720);

        // Draw "GAME OVER" text
        font.getData().setScale(6.0f);
        font.setColor(1, 0, 0, 1);
        String gameOverText = "GAME OVER";
        float gameOverWidth = font.getData().getGlyph('A').width * 0.6f * gameOverText.length() * 6.0f;
        font.draw(batch, gameOverText, 1280/2 - gameOverWidth/2 - 75, 720/2 + 100);

        // Draw retry text
        font.getData().setScale(2.0f);
        font.setColor(1, 1, 1, 1);
        String retryText = "Press R to Retry";
        float retryWidth = font.getData().getGlyph('A').width * 0.6f * retryText.length() * 2.0f;
        font.draw(batch, retryText, 1280/2 - retryWidth/2, 720/2);

        // Draw return to menu text
        font.getData().setScale(2.0f);
        font.setColor(0.7f, 0.7f, 0.7f, 1);
        String returnText = "Press ESC to return to main menu";
        float returnWidth = font.getData().getGlyph('A').width * 0.6f * returnText.length() * 2.0f;
        font.draw(batch, returnText, 1280/2 - returnWidth/2 - 15, 720/2 - 50);

        batch.end();

        // Handle input
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            ((Game)Gdx.app.getApplicationListener()).setScreen(new GameScreen());
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ((Game)Gdx.app.getApplicationListener()).setScreen(new MainMenu());
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        backgroundTexture.dispose();
    }
}
