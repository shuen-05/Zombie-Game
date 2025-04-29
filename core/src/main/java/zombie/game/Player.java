package zombie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import java.util.List;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Player {
    float x, y;
    int health = 100;
    int armor = 0;
    Weapon currentWeapon = Weapon.RIFLE;
    private static final float MOVEMENT_SPEED = 3.0f;
    private static final float DIAGONAL_SPEED = (float) (MOVEMENT_SPEED / Math.sqrt(2));
    private static final float GAME_WIDTH = 1280f;
    private static final float GAME_HEIGHT = 720f;
    private float shootCooldown = 0;
    private Texture idleTexture;
    private Texture shootTexture;
    private Texture madnessTexture;
    private boolean isShooting = false;
    private float shootAnimationTime = 0;
    private static final float SHOOT_ANIMATION_DURATION = 0.1f;
    private boolean isMoving = false;
    private float walkAnimationTime = 0;
    private static final float WALK_ANIMATION_DURATION = 0.2f;
    private boolean walkState = false;
    private Rectangle bounds;
    private static final float SIZE = 50f;
    private float rotationAngle = 0;
    private int rifleAmmo = 50;
    private int shotgunAmmo = 15;
    private boolean isMadnessMode = false;
    private float walkTimer = 0;

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
        this.health = 100;
        this.armor = 0;
        this.bounds = new Rectangle(x, y, SIZE, SIZE);
        
        try {
            // Load textures directly
            idleTexture = new Texture(Gdx.files.internal("images/player/player_idle.png"));
            shootTexture = new Texture(Gdx.files.internal("images/player/player_shoot.png"));
            madnessTexture = new Texture(Gdx.files.internal("images/player/optimus_madness.png"));
            Gdx.app.log("Player", "Player textures loaded successfully");
        } catch (Exception e) {
            Gdx.app.error("Player", "Failed to load player textures", e);
        }
    }

    public void update(List<Bullet> bullets) {
        // Handle madness mode toggle
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.P)) {
            isMadnessMode = true;
        } else {
            isMadnessMode = false;
        }

        // Calculate rotation angle based on mouse position using viewport unproject
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        GameScreen.getInstance().getViewport().unproject(mousePos);
        
        // Calculate distance to mouse
        float dx = mousePos.x - (x + SIZE/2);
        float dy = mousePos.y - (y + SIZE/2);
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        // Only update rotation if mouse is outside deadzone
        if (distance > 5f) {
            rotationAngle = (float) Math.toDegrees(Math.atan2(dy, dx));
        }

        boolean movingUp = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean movingDown = Gdx.input.isKeyPressed(Input.Keys.S);
        boolean movingLeft = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean movingRight = Gdx.input.isKeyPressed(Input.Keys.D);

        isMoving = movingUp || movingDown || movingLeft || movingRight;

        // Update walk animation
        if (isMoving) {
            walkAnimationTime -= Gdx.graphics.getDeltaTime();
            if (walkAnimationTime <= 0) {
                walkState = !walkState;
                walkAnimationTime = WALK_ANIMATION_DURATION;
            }
        } else {
            walkState = false;
        }

        // Handle diagonal movement
        if (movingUp && movingRight) {
            x += DIAGONAL_SPEED;
            y += DIAGONAL_SPEED;
        } else if (movingUp && movingLeft) {
            x -= DIAGONAL_SPEED;
            y += DIAGONAL_SPEED;
        } else if (movingDown && movingRight) {
            x += DIAGONAL_SPEED;
            y -= DIAGONAL_SPEED;
        } else if (movingDown && movingLeft) {
            x -= DIAGONAL_SPEED;
            y -= DIAGONAL_SPEED;
        } else {
            if (movingUp) y += MOVEMENT_SPEED;
            if (movingDown) y -= MOVEMENT_SPEED;
            if (movingLeft) x -= MOVEMENT_SPEED;
            if (movingRight) x += MOVEMENT_SPEED;
        }

        // Keep player within game bounds
        x = Math.max(0, Math.min(x, GAME_WIDTH - SIZE));
        y = Math.max(0, Math.min(y, GAME_HEIGHT - SIZE));

        // Update bounds position
        bounds.setPosition(x, y);

        // Handle shooting
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && shootCooldown <= 0) {
            shoot(bullets);
            shootCooldown = currentWeapon.getShootCooldown(); // Use weapon's cooldown value
        }

        // Handle weapon switching
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            switchWeapon();
        }

        // Update shoot cooldown
        if (shootCooldown > 0) {
            shootCooldown -= Gdx.graphics.getDeltaTime();
        }

        // Update shoot animation
        if (isShooting) {
            shootAnimationTime -= Gdx.graphics.getDeltaTime();
            if (shootAnimationTime <= 0) {
                isShooting = false;
            }
        }
    }

    private void shoot(List<Bullet> bullets) {
        isShooting = true;
        shootAnimationTime = SHOOT_ANIMATION_DURATION;

        if (currentWeapon == Weapon.RIFLE && rifleAmmo > 0) {
            float angle = rotationAngle * (float) Math.PI / 180f;
            float dirX = (float) Math.cos(angle);
            float dirY = (float) Math.sin(angle);
            bullets.add(new Bullet(x + SIZE/2, y + SIZE/2, dirX, dirY, false));
            rifleAmmo--;
            SoundManager.getInstance().playRifleShot();
        } else if (currentWeapon == Weapon.SHOTGUN && shotgunAmmo > 0) {
            float angle = rotationAngle * (float) Math.PI / 180f;
            float dirX = (float) Math.cos(angle);
            float dirY = (float) Math.sin(angle);
            // Create multiple pellets for shotgun
            for (int i = 0; i < 30; i++) {
                bullets.add(new Bullet(x + SIZE/2, y + SIZE/2, dirX, dirY, true));
            }
            shotgunAmmo--;
            SoundManager.getInstance().playShotgunShot();
        }
    }

    private void switchWeapon() {
        Gdx.app.log("Player", "Current weapon before switch: " + currentWeapon);
        if (currentWeapon == Weapon.RIFLE) {
            currentWeapon = Weapon.SHOTGUN;
            Gdx.app.log("Player", "Switched to SHOTGUN");
        } else {
            currentWeapon = Weapon.RIFLE;
            Gdx.app.log("Player", "Switched to RIFLE");
        }
    }

    public void render(SpriteBatch batch) {
        try {
            float scale = 1.0f;
            if (isMoving) {
                walkTimer += Gdx.graphics.getDeltaTime() * 5;
                scale = 1.0f + (float)Math.sin(walkTimer) * 0.1f;
            }

            // Draw player texture with rotation
            if (isShooting) {
                batch.draw(shootTexture, x, y, SIZE/2, SIZE/2, SIZE, SIZE, 1, 1, rotationAngle, 0, 0, shootTexture.getWidth(), shootTexture.getHeight(), false, false);
            } else if (isMoving) {
                // Create a simple walking effect by slightly scaling the texture
                Texture currentTexture = isMadnessMode ? madnessTexture : idleTexture;
                batch.draw(currentTexture, x, y, SIZE/2, SIZE/2, SIZE, SIZE, scale, scale, rotationAngle, 0, 0, currentTexture.getWidth(), currentTexture.getHeight(), false, false);
            } else {
                Texture currentTexture = isMadnessMode ? madnessTexture : idleTexture;
                batch.draw(currentTexture, x, y, SIZE/2, SIZE/2, SIZE, SIZE, 1, 1, rotationAngle, 0, 0, currentTexture.getWidth(), currentTexture.getHeight(), false, false);
            }

            // Draw health and bullet count with different colors and sizes
            BitmapFont font = Utils.getFont();
            
            // Save current color and scale
            Color originalColor = font.getColor();
            float originalScale = font.getScaleX();
            
            // Draw health in red at 2.0x scale
            font.setColor(1, 0, 0, 1); // Red
            font.getData().setScale(2.0f);
            font.draw(batch, "Health: " + health + "/100", 1000, 100);
            
            // Draw gun type and bullet count in yellow at 2.0x scale
            font.setColor(1, 1, 0, 1); // Yellow
            String ammoText = (currentWeapon == Weapon.RIFLE ? "Rifle: " + rifleAmmo : "Shotgun: " + shotgunAmmo);
            font.draw(batch, ammoText, 1000, 150);
            
            // Reset font color and scale
            font.setColor(originalColor);
            font.getData().setScale(originalScale);
        } catch (Exception e) {
            Gdx.app.error("Player", "Failed to render player", e);
        }
    }

    public void dispose() {
        if (idleTexture != null) {
            idleTexture.dispose();
        }
        if (shootTexture != null) {
            shootTexture.dispose();
        }
        if (madnessTexture != null) {
            madnessTexture.dispose();
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public int getHealth() {
        return health;
    }

    public int getArmor() {
        return armor;
    }

    public void takeDamage(int damage) {
        if (armor > 0) {
            armor -= damage;
            if (armor < 0) {
                health += armor; // Apply remaining damage to health
                armor = 0;
            }
        } else {
            health -= damage;
        }
        
        // Play damage sound effect
        SoundManager.getInstance().playTakeDamage();
        
        if (health <= 0) {
            health = 0;
            GameScreen.getInstance().setGameOver(true);
        }
    }

    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    public int getRifleAmmo() {
        return rifleAmmo;
    }

    public int getShotgunAmmo() {
        return shotgunAmmo;
    }

    public void addAmmo(Weapon weapon, int amount) {
        if (weapon == Weapon.RIFLE) {
            rifleAmmo += amount;
        } else {
            shotgunAmmo += amount;
        }
    }

    public void addArmor(int amount) {
        armor = Math.min(100, armor + amount);
    }
}
