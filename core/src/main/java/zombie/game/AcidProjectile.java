package zombie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class AcidProjectile {
    private static final float SPEED = 450f; // Increased by 50% for faster projectiles
    private static final float SIZE = 10f;
    private static Texture acidTexture;
    private final Vector2 position;
    private final Vector2 direction;
    private final Rectangle bounds;
    private boolean active;
    private static final int DAMAGE = 5;

    static {
        try {
            acidTexture = new Texture(Gdx.files.internal("images/projectiles/acid_projectile.png"));
        } catch (Exception e) {
            Gdx.app.error("AcidProjectile", "Failed to load texture: " + e.getMessage());
            acidTexture = createDefaultTexture();
        }
    }

    private static Texture createDefaultTexture() {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(16, 16, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 1, 0, 1); // Green color for acid
        pixmap.fillCircle(8, 8, 6);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public AcidProjectile(float x, float y, float targetX, float targetY) {
        position = new Vector2(x, y);
        Vector2 target = new Vector2(targetX, targetY);
        direction = target.sub(position).nor();
        bounds = new Rectangle(x, y, SIZE, SIZE);
        active = true;
    }

    public void update(float deltaTime) {
        position.x += direction.x * SPEED * deltaTime;
        position.y += direction.y * SPEED * deltaTime;
        bounds.setPosition(position);

        // Deactivate if off screen
        if (position.x < 0 || position.x > Gdx.graphics.getWidth() ||
            position.y < 0 || position.y > Gdx.graphics.getHeight()) {
            active = false;
        }
    }

    public void render(SpriteBatch batch) {
        if (active) {
            float angle = (float) Math.toDegrees(Math.atan2(direction.y, direction.x));
            batch.draw(acidTexture,
                position.x - SIZE/2, position.y - SIZE/2,
                SIZE/2, SIZE/2,
                SIZE, SIZE,
                1, 1,
                angle,
                0, 0,
                acidTexture.getWidth(), acidTexture.getHeight(),
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

    public int getDamage() {
        return DAMAGE;
    }

    public static void dispose() {
        if (acidTexture != null) {
            acidTexture.dispose();
            acidTexture = null;
        }
    }
}