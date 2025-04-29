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
import com.badlogic.gdx.audio.Music;

public class MainMenu implements Screen {
    private BitmapFont font;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture backgroundTexture;
    private Music menuMusic;
    private SpriteBatch batch;

    public MainMenu() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
        batch = new SpriteBatch();
        font = new BitmapFont();
        backgroundTexture = new Texture(Gdx.files.internal("images/ui/background_menu.png"));
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/music/menu_music.mp3"));
    }

    @Override
    public void show() {
        // Start playing menu music
        menuMusic.setLooping(true);
        menuMusic.setVolume(0.3f);
        menuMusic.play();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        
        // Begin batch for rendering
        batch.begin();
        
        // Draw background
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);
        }
        
        // Draw title with larger font
        font.getData().setScale(8.0f);
        font.setColor(1, 0, 0, 1);
        String titleText = "ZOMBIE:APOCALYPSE";
        float titleWidth = font.getData().getGlyph('A').width * 0.6f * titleText.length() * 8.0f;
        font.draw(batch, titleText, camera.viewportWidth / 2 - titleWidth / 2 - 168f, camera.viewportHeight / 2 + 100);
        
        // Draw menu options with normal font
        font.getData().setScale(1.5f);
        font.setColor(1, 1, 1, 1);
        String startText = "Press SPACE to Start";
        String exitText = "Press ESC to Exit";
        float startWidth = font.getData().getGlyph('A').width * 0.6f * startText.length() * 1.5f;
        float exitWidth = font.getData().getGlyph('A').width * 0.6f * exitText.length() * 1.5f;
        font.draw(batch, startText, camera.viewportWidth / 2 - startWidth / 2, camera.viewportHeight / 2 - 50);
        font.draw(batch, exitText, camera.viewportWidth / 2 - exitWidth / 2, camera.viewportHeight / 2 - 100);
        
        batch.end();

        // Handle input
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            ((Game)Gdx.app.getApplicationListener()).setScreen(new GameScreen());
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
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
        // Handle pause logic if needed
    }

    @Override
    public void resume() {
        // Handle resume logic if needed
    }

    @Override
    public void hide() {
        // Stop menu music when leaving main menu
        menuMusic.stop();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        backgroundTexture.dispose();
        menuMusic.dispose();
    }
}
