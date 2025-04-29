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

public class WinScreen implements Screen {
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backgroundTexture;
    private SoundManager soundManager;

    public WinScreen() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
        batch = new SpriteBatch();
        font = new BitmapFont();
        backgroundTexture = new Texture(Gdx.files.internal("images/ui/win_screen.png"));
        soundManager = SoundManager.getInstance();
    }

    @Override
    public void show() {
        soundManager.playWinMusic();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        // Draw background
        batch.draw(backgroundTexture, 0, 0, 1280, 720);

        // Draw "YOU WIN" text
        font.getData().setScale(6.0f);
        font.setColor(0, 1, 0, 1);
        String winText = "YOU WIN";
        float winWidth = font.getData().getGlyph('A').width * 0.6f * winText.length() * 6.0f;
        font.draw(batch, winText, 1280/2 - winWidth/2 - 20, 720 - 20);

        // Draw victory message
        font.getData().setScale(1.5f);
        font.setColor(0, 0, 0, 1);
        String victoryText = "You've successfully survived through the apocalypse and saved all mankind";
        float victoryWidth = font.getData().getGlyph('A').width * 0.6f * victoryText.length() * 1.5f;
        font.draw(batch, victoryText, 1280/2 - victoryWidth/2, 720 - 120);

        // Draw credits section
        font.getData().setScale(2.0f);
        font.setColor(0, 0, 0, 1);
        String creditsText = "=== CREDITS ===";
        float creditsWidth = font.getData().getGlyph('A').width * 0.6f * creditsText.length() * 2.0f;
        font.draw(batch, creditsText, 1280/2 - creditsWidth/2, 720 - 180);

        // Draw game title
        font.getData().setScale(3.0f);
        font.setColor(0, 0, 0, 1);
        String titleText = "ZOMBIE:APOCALYPSE";
        float titleWidth = font.getData().getGlyph('A').width * 0.6f * titleText.length() * 3.0f;
        font.draw(batch, titleText, 1280/2 - titleWidth/2 - 30, 720 - 220);

        // Draw development team
        font.getData().setScale(1.5f);
        font.setColor(0, 0, 0, 1);
        String teamText = "=== DEVELOPMENT TEAM ===";
        float teamWidth = font.getData().getGlyph('A').width * 0.6f * teamText.length() * 1.5f;
        font.draw(batch, teamText, 1280/2 - teamWidth/2 - 10, 720 - 260);

        // Draw team members
        font.getData().setScale(1.2f);
        font.setColor(0, 0, 0, 1);
        float y = 720 - 300;
        String[] teamMembers = {
            "Game Director: Tan Shuen Xian",
            "Music & Sound: Charles Dan Zhi Jhet",
            "Art & Animation: Tan Shuen Xian, Cui Yi Ning",
            "Programming: Alvin Chong Kah Ming, Chan Jun Xi, Tan Shuen Xian, Cui Yi Ning",
            "Documentation: Choong Yi He, Alvin Chong Kah Ming"
        };
        for (String member : teamMembers) {
            float memberWidth = font.getData().getGlyph('A').width * 0.6f * member.length() * 1.2f;
            font.draw(batch, member, 1280/2 - memberWidth/2, y);
            y -= 25;
        }

        // Draw special thanks
        font.getData().setScale(1.5f);
        font.setColor(0, 0, 0, 1);
        String thanksText = "=== SPECIAL THANKS ===";
        float thanksWidth = font.getData().getGlyph('A').width * 0.6f * thanksText.length() * 1.5f;
        font.draw(batch, thanksText, 1280/2 - thanksWidth/2 - 10, y - 15);

        font.getData().setScale(1.2f);
        font.setColor(0, 0, 0, 1);
        String[] specialThanks = {
            "ChatGPT, Deepseek"
        };
        for (String thanks : specialThanks) {
            float thanksMemberWidth = font.getData().getGlyph('A').width * 0.6f * thanks.length() * 1.2f;
            font.draw(batch, thanks, 1280/2 - thanksMemberWidth/2, y - 40);
            y -= 25;
        }

        // Draw tools and assets
        font.getData().setScale(1.5f);
        font.setColor(0, 0, 0, 1);
        String toolsText = "=== TOOLS & ASSETS ===";
        float toolsWidth = font.getData().getGlyph('A').width * 0.6f * toolsText.length() * 1.5f;
        font.draw(batch, toolsText, 1280/2 - toolsWidth/2 - 10, y - 60);

        font.getData().setScale(1.2f);
        font.setColor(0, 0, 0, 1);
        String[] tools = {
            "Made with JavaJDK",
            "© 2024 ZombieGame. All Rights Reserved.",
            "Thank you for playing!"
        };
        for (String tool : tools) {
            float toolWidth = font.getData().getGlyph('A').width * 0.6f * tool.length() * 1.2f;
            font.draw(batch, tool, 1280/2 - toolWidth/2, y - 85);
            y -= 25;
        }

        // Draw return to menu text
        font.getData().setScale(1.0f);
        font.setColor(0, 0, 0, 1);
        String returnText = "PRESS \"ESC\" to RETURN TO MAIN MENU";
        float returnWidth = font.getData().getGlyph('A').width * 0.6f * returnText.length();
        font.draw(batch, returnText, 1280/2 - returnWidth/2 - 15, 50);

        batch.end();

        // Handle input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            soundManager.stopAllMusic();
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