package zombie.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class GameScreen implements Screen {
    Player player;
    List<BaseZombie> zombies;
    List<Bullet> bullets;
    Random rand;
    private int waveNumber = 1;
    private float timeSinceLastWave = 0;
    private static final float WAVE_INTERVAL = 15.0f;
    private static final int BASE_ZOMBIES_PER_WAVE = 5;
    private static final float DIFFICULTY_SCALING = 1.2f;
    private boolean isGameOver = false;
    private Texture backgroundTexture;
    private Texture ammoIconTexture;
    private Rectangle playAgainBounds;
    private Rectangle quitBounds;
    private Viewport viewport;
    private OrthographicCamera camera;
    private static GameScreen instance;
    private BitmapFont font;

    public GameScreen() {
        instance = this;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public static GameScreen getInstance() {
        return instance;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    @Override
    public void show() {
        try {
            init();
            Gdx.app.log("GameScreen", "Game screen initialized successfully");
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Failed to initialize game screen", e);
            e.printStackTrace();
        }
    }

    public void init() {
        // Initialize camera and viewport
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        camera.position.set(1280/2f, 720/2f, 0);
        camera.update();
        viewport = new FitViewport(1280, 720, camera);

        // Initialize game objects
        player = new Player(640, 360);  // Center of 1280x720
        bullets = new ArrayList<>();
        zombies = new ArrayList<>();
        rand = new Random();
        waveNumber = 1;
        timeSinceLastWave = 0;
        isGameOver = false;

        // Start level music
        SoundManager.getInstance().playLevelMusic();

        // Initialize button bounds
        playAgainBounds = new Rectangle(1280/2 - 150, 300, 300, 80);
        quitBounds = new Rectangle(1280/2 - 150, 200, 300, 80);

        // Load textures
        try {
            backgroundTexture = new Texture(Gdx.files.internal("images/ui/background.png"));
            ammoIconTexture = new Texture(Gdx.files.internal("images/ui/ammo_icon.png"));
            Gdx.app.log("GameScreen", "Textures loaded successfully");
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Failed to load textures", e);
            // Create placeholder textures if loading fails
            backgroundTexture = new Texture(1280, 720, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            ammoIconTexture = new Texture(20, 20, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        }

        font = Utils.getFont();

        spawnWave();
    }

    @Override
    public void render(float delta) {
        if (isGameOver) {
            drawGameOverScreen();
            return;
        }

        // Normal game rendering when not game over
        update();

        // Clear the screen
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update camera
        camera.update();
        
        // Get the game's batch and set up projection matrix
        SpriteBatch batch = ZombieGame.getBatch();
        batch.setProjectionMatrix(camera.combined);
        
        // Begin sprite batch for all texture rendering
        batch.begin();
        
        // Draw gameplay background
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, 1280, 720);
        }
        
        // Render game elements
        player.render(batch);
        for (BaseZombie z : zombies) {
            z.render(batch);
        }
        for (Bullet b : bullets) {
            b.render(batch);
        }
        
        batch.end();
        
        // Draw UI elements
        Utils.begin();
        
        // Draw wave number above ammo
        Utils.drawText("Wave: " + waveNumber + "/8", 45, 60);
        
        // Draw ammo info
        String ammoText = player.getCurrentWeapon() == Weapon.RIFLE ? 
            "Rifle: " + player.getRifleAmmo() : 
            "Shotgun: " + player.getShotgunAmmo();
        Utils.drawText(ammoText, 45, 30);
        
        // Draw health text in bottom right
        Utils.drawText("Health: " + player.getHealth() + "/100", 1100, 30);

        // Draw boss health bar if boss is present
        for (BaseZombie z : zombies) {
            if (z instanceof BossZombie) {
                BossZombie boss = (BossZombie)z;
                float healthPercentage = (float)boss.getHealth() / 300f; // 300 is max boss health
                
                // Draw boss health bar background
                batch.begin();
                batch.setColor(0.2f, 0.2f, 0.2f, 1f);
                batch.draw(backgroundTexture, 1280/2 - 200, 700, 400, 20);
                
                // Draw boss health bar
                batch.setColor(1f, 0f, 0f, 1f);
                batch.draw(backgroundTexture, 1280/2 - 200, 700, 400 * healthPercentage, 20);
                batch.setColor(1f, 1f, 1f, 1f);
                batch.end();

                // Draw boss health text
                Utils.drawText("Boss Health: " + boss.getHealth() + "/300", 1280/2 - 100, 720);
                break; // Only draw for first boss found
            }
        }
        
        Utils.end();
    }

    public void update() {
        if (isGameOver) {
            return; // Don't update game logic when game is over
        }

        float deltaTime = Gdx.graphics.getDeltaTime();
        
        // Update player and handle input
        player.update(bullets);
        
        // Update zombies and bullets
        for (BaseZombie z : zombies) {
            z.update(player);
        }
        for (Bullet b : bullets) {
            b.update(deltaTime);
        }

        // Check for bullet-zombie collisions
        for (Bullet b : bullets) {
            if (!b.isActive()) continue;
            for (BaseZombie z : zombies) {
                if (!z.isDead() && z.getBounds().overlaps(b.getBounds())) {
                    z.takeDamage((int)b.getDamage());
                    // Deactivate all bullets on contact, including shotgun pellets
                    b.setActive(false);
                    
                    if (z.isDead()) {
                        // If it's a boss, show win screen
                        if (z instanceof BossZombie) {
                            SoundManager.getInstance().stopAllMusic();
                            ZombieGame.getInstance().setScreen(new WinScreen());
                            return;
                        }
                        
                        // Randomly choose between shotgun or rifle ammo drop
                        if (rand.nextBoolean()) {
                            // Drop shotgun ammo (0-5)
                            int shotgunAmmo = rand.nextInt(6); // 0 to 5
                            player.addAmmo(Weapon.SHOTGUN, shotgunAmmo);
                        } else {
                            // Drop rifle ammo (5-15)
                            int rifleAmmo = 5 + rand.nextInt(11); // 5 to 15
                            player.addAmmo(Weapon.RIFLE, rifleAmmo);
                        }
                        
                        // 5% chance to heal 5 health when killing a zombie
                        if (rand.nextFloat() < 0.05f) {
                            player.health = Math.min(100, player.health + 5);
                        }
                    }
                    break;
                }
            }
        }

        // Clean up inactive bullets and dead zombies
        bullets.removeIf(b -> !b.isActive());
        zombies.removeIf(z -> z.isDead());

        // Spawn new wave if needed
        timeSinceLastWave += deltaTime;
        if (timeSinceLastWave >= WAVE_INTERVAL) {
            waveNumber++;
            spawnWave();
            timeSinceLastWave = 0;
        }

        checkGameOver();
    }

    private boolean checkCollision(Player player, BaseZombie zombie) {
        float playerCenterX = player.getX() + 15;
        float playerCenterY = player.getY() + 15;
        float zombieCenterX = zombie.getX() + 15;
        float zombieCenterY = zombie.getY() + 15;
        
        float distance = (float) Math.sqrt(
            Math.pow(playerCenterX - zombieCenterX, 2) +
            Math.pow(playerCenterY - zombieCenterY, 2)
        );
        
        if (distance < 30) { // Collision radius
            if (zombie.canAttack()) {
                player.takeDamage(5);  // Basic zombie damage
                zombie.resetAttackCooldown();
                return true;
            }
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        // Update viewport and center camera
        viewport.update(width, height, true);
        camera.position.set(1280/2f, 720/2f, 0);
        camera.update();
        Gdx.app.log("GameScreen", "Resized to " + width + "x" + height);
    }

    @Override
    public void pause() {
        // Pause game logic
    }

    @Override
    public void resume() {
        // Resume game logic
    }

    @Override
    public void hide() {
        // Cleanup when screen is hidden
    }

    @Override
    public void dispose() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (ammoIconTexture != null) {
            ammoIconTexture.dispose();
        }
        for (BaseZombie zombie : zombies) {
            zombie.dispose();
        }
        player.dispose();
        Bullet.dispose();
        SoundManager.getInstance().stopAllMusic();
        Gdx.app.log("GameScreen", "Disposed of all resources");
    }

    private void spawnWave() {
        // Don't spawn waves beyond 8
        if (waveNumber > 8) {
            return;
        }

        int zombiesToSpawn = (int) (BASE_ZOMBIES_PER_WAVE * Math.pow(DIFFICULTY_SCALING, waveNumber - 1));
        
        // Spawn boss on wave 8
        if (waveNumber == 8) {
            float spawnX, spawnY;
            if (rand.nextBoolean()) {
                spawnX = rand.nextBoolean() ? 0 : 1280;
                spawnY = rand.nextFloat() * 720;
            } else {
                spawnX = rand.nextFloat() * 1280;
                spawnY = rand.nextBoolean() ? 0 : 720;
            }
            zombies.add(new BossZombie(spawnX, spawnY));
            SoundManager.getInstance().playBossMusic();
            SoundManager.getInstance().playZombieGrowl();
            return;
        }
        
        for (int i = 0; i < zombiesToSpawn; i++) {
            float spawnX, spawnY;
            if (rand.nextBoolean()) {
                spawnX = rand.nextBoolean() ? 0 : 1280;
                spawnY = rand.nextFloat() * 720;
            } else {
                spawnX = rand.nextFloat() * 1280;
                spawnY = rand.nextBoolean() ? 0 : 720;
            }

            // Determine zombie type based on wave number and random chance
            BaseZombie zombie;
            float roll = rand.nextFloat();
            
            if (waveNumber >= 5 && roll < 0.2) {  // 20% chance for explosive zombie after wave 5
                zombie = new ExplosiveZombie(spawnX, spawnY);
            } else if (waveNumber >= 3 && roll < 0.4) {  // 20% chance for acid zombie after wave 3
                zombie = new AcidZombie(spawnX, spawnY);
            } else {
                zombie = new Zombie(spawnX, spawnY);
            }
            
            zombies.add(zombie);
            SoundManager.getInstance().playZombieGrowl();
        }
        
        Gdx.app.log("GameScreen", "Wave " + waveNumber + " spawned with " + zombiesToSpawn + " zombies");
    }

    private void drawGameOverScreen() {
        SpriteBatch batch = ZombieGame.getBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        // Draw game over background
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, camera.viewportWidth, camera.viewportHeight);
        }
        
        // Draw game over text
        font.getData().setScale(6.0f);
        font.setColor(1, 0, 0, 1);
        String gameOverText = "GAME OVER";
        float textWidth = font.getData().getGlyph('A').width * 0.6f * gameOverText.length() * 6.0f;
        font.draw(batch, gameOverText, camera.viewportWidth / 2 - textWidth / 2 - 70, camera.viewportHeight / 2 + 100);
        
        // Draw menu options
        font.getData().setScale(2.0f);
        font.setColor(1, 1, 1, 1);
        
        // Draw retry option
        String retryText = "Press R to Retry";
        float retryWidth = font.getData().getGlyph('A').width * 0.6f * retryText.length() * 2.0f;
        font.draw(batch, retryText, camera.viewportWidth / 2 - retryWidth / 2 + 10, camera.viewportHeight / 2 - 20);
        
        // Draw return to menu text
        String menuText = "Press ESC to return to main menu";
        float menuWidth = font.getData().getGlyph('A').width * 0.6f * menuText.length() * 2.0f;
        font.draw(batch, menuText, camera.viewportWidth / 2 - menuWidth / 2, camera.viewportHeight / 2 - 80);
        
        batch.end();

        // Handle input
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            ZombieGame.getInstance().setScreen(new GameScreen());
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ZombieGame.getInstance().setScreen(new MainMenu());
        }
    }

    private void checkGameOver() {
        if (player.getHealth() <= 0) {
            SoundManager.getInstance().stopAllMusic(); // Stop music when player dies
            ZombieGame.getInstance().setScreen(new GameOverScreen());
        }
    }
}
