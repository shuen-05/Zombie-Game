package zombie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

public class ExplosiveZombie extends BaseZombie {
    private static final float EXPLOSION_RADIUS = 100f;
    private static final int EXPLOSION_DAMAGE = 50;
    private static final float TRIGGER_DISTANCE = 30f;
    private boolean isExploding = false;
    private float explosionTimer = 0.8f;
    private static Texture explosionTexture;

    static {
        try {
            explosionTexture = new Texture(Gdx.files.internal("images/projectiles/explosion.png"));
        } catch (Exception e) {
            Gdx.app.error("ExplosiveZombie", "Failed to load explosion texture", e);
        }
    }

    public ExplosiveZombie(float x, float y) {
        super(x, y, 15, 2.5f, 0f,  // 15 health, 25% faster speed, no attack cooldown
              "images/zombies/explosive_zombie.png",
              "images/zombies/explosive_zombie_triggered.png");
    }

    @Override
    public void update(Player player) {
        if (isDead) return;

        // Update rotation to face player
        updateRotation(player);

        float dx = player.getX() - x;
        float dy = player.getY() - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (isExploding) {
            explosionTimer -= Gdx.graphics.getDeltaTime();
            if (explosionTimer <= 0) {
                explode(player);
                isDead = true;
                return;
            }
        } else if (distance <= TRIGGER_DISTANCE) {
            isExploding = true;
            isAttacking = true;
            // Deal 50 damage on contact
            player.takeDamage(50);
        }

        // Move towards player if not exploding
        if (!isExploding && distance > 0) {
            x += (dx / distance) * moveSpeed;
            y += (dy / distance) * moveSpeed;
            bounds.setPosition(x, y);
        }
    }

    @Override
    public void attack(Player player) {
        // Explosive zombies don't have a regular attack
    }

    private void explode(Player player) {
        // Calculate distance to player
        float dx = player.getX() + 15 - x;
        float dy = player.getY() + 15 - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Play explosion sound
        SoundManager.getInstance().playExplosion();

        // Damage player if within explosion radius with smoother falloff
        if (distance <= EXPLOSION_RADIUS) {
            float damageMultiplier = (float) Math.pow(1 - (distance / EXPLOSION_RADIUS), 0.5);
            int damage = (int)(EXPLOSION_DAMAGE * damageMultiplier);
            player.takeDamage(Math.max(1, damage));
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // First render the zombie texture with full opacity
        if (!isDead) {
            // Save the current color
            com.badlogic.gdx.graphics.Color originalColor = batch.getColor();
            
            // Ensure zombie is rendered with full opacity
            batch.setColor(1f, 1f, 1f, 1f);
            super.render(batch);
            
            // Restore the original color
            batch.setColor(originalColor);
        }
        
        // Then render the explosion effect if exploding
        if (isExploding && explosionTexture != null) {
            float scale = 1f + (1f - explosionTimer) * 2f; // Scale up as explosion progresses
            float alpha = explosionTimer; // Fade out as explosion progresses
            
            // Save the current color
            com.badlogic.gdx.graphics.Color originalColor = batch.getColor();
            
            // Set the color with alpha only for the explosion
            batch.setColor(1f, 1f, 1f, alpha);
            
            // Draw the explosion texture centered on the zombie
            float explosionSize = EXPLOSION_RADIUS * 2 * scale;
            batch.draw(explosionTexture,
                x + SIZE/2 - explosionSize/2,
                y + SIZE/2 - explosionSize/2,
                explosionSize/2, explosionSize/2,
                explosionSize, explosionSize,
                1f, 1f,
                rotationAngle,
                0, 0,
                explosionTexture.getWidth(), explosionTexture.getHeight(),
                false, false);
            
            // Restore the original color
            batch.setColor(originalColor);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (explosionTexture != null) {
            explosionTexture.dispose();
        }
    }
} 