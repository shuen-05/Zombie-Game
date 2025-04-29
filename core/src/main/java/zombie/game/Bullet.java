package zombie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

public class Bullet {
    private static final float RIFLE_SPEED = 700f;
    private static final float SHOTGUN_SPEED = 400f;
    private static final float RIFLE_SIZE = 10f;
    private static final float SHOTGUN_SIZE = 6f;
    private static final float SHOTGUN_SPREAD = 15f; // Spread angle in degrees
    private static final float SHOTGUN_DAMAGE = 1.5f; // 1.5 damage per pellet
    private static final float SHOTGUN_MAX_RANGE = 250f; // Maximum range in pixels (approximately 5 meters)
    private static Texture rifleTexture;
    private static Texture shotgunTexture;
    private final Vector2 position;
    private final Vector2 direction;
    private final Rectangle bounds;
    private boolean active;
    private final Texture texture;
    private final float speed;
    private final float size;
    private final float damage;
    private final boolean isShotgun;
    private float distanceTraveled;

    static {
        try {
            rifleTexture = new Texture(Gdx.files.internal("images/projectiles/bullet.png"));
            shotgunTexture = new Texture(Gdx.files.internal("images/projectiles/shotgun_pellet.png"));
        } catch (Exception e) {
            Gdx.app.error("Bullet", "Failed to load textures: " + e.getMessage());
            rifleTexture = createDefaultTexture();
            shotgunTexture = createShotgunTexture();
        }
    }

    private static Texture createDefaultTexture() {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(16, 16, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fillCircle(8, 8, 4);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private static Texture createShotgunTexture() {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(16, 16, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 0.8f, 0, 1);
        pixmap.fillCircle(8, 8, 3);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public Bullet(float x, float y, float dirX, float dirY, boolean isShotgun) {
        position = new Vector2(x, y);
        direction = new Vector2(dirX, dirY).nor();
        this.size = isShotgun ? SHOTGUN_SIZE : RIFLE_SIZE;
        this.damage = isShotgun ? SHOTGUN_DAMAGE : 5f; // Rifle does 5 damage
        bounds = new Rectangle(x, y, size, size);
        active = true;
        this.texture = isShotgun ? shotgunTexture : rifleTexture;
        this.speed = isShotgun ? SHOTGUN_SPEED : RIFLE_SPEED;
        this.isShotgun = isShotgun;
        this.distanceTraveled = 0f;

        // Apply spread for shotgun pellets
        if (isShotgun) {
            float angle = MathUtils.atan2(direction.y, direction.x);
            angle += MathUtils.random(-SHOTGUN_SPREAD, SHOTGUN_SPREAD) * MathUtils.degreesToRadians;
            direction.set(MathUtils.cos(angle), MathUtils.sin(angle));
        }
    }

    public void update(float deltaTime) {
        float moveX = direction.x * speed * deltaTime;
        float moveY = direction.y * speed * deltaTime;
        position.x += moveX;
        position.y += moveY;
        bounds.setPosition(position);

        // Update distance traveled for shotgun pellets
        if (isShotgun) {
            distanceTraveled += Math.sqrt(moveX * moveX + moveY * moveY);
            if (distanceTraveled >= SHOTGUN_MAX_RANGE) {
                active = false;
                return;
            }
        }

        // Deactivate if off screen using game viewport size
        if (position.x < 0 || position.x > 1280 ||
            position.y < 0 || position.y > 960) {
            active = false;
        }
    }

    public void render(SpriteBatch batch) {
        if (active) {
            float angle = (float) Math.toDegrees(Math.atan2(direction.y, direction.x));
            batch.draw(texture,
                position.x - size/2, position.y - size/2,
                size/2, size/2,
                size, size,
                1, 1,
                angle,
                0, 0,
                texture.getWidth(), texture.getHeight(),
                false, false);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getDamage() {
        return damage;
    }

    public boolean isShotgun() {
        return texture == shotgunTexture;
    }

    public static void dispose() {
        if (rifleTexture != null) {
            rifleTexture.dispose();
            rifleTexture = null;
        }
        if (shotgunTexture != null) {
            shotgunTexture.dispose();
            shotgunTexture = null;
        }
    }
}
