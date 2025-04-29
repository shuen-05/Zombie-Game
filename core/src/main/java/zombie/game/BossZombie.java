package zombie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class BossZombie extends BaseZombie {
    private static final float BOSS_SIZE = 187.5f; // 2.5 times player size (half of original)
    private static final float BOSS_SPEED = 0.0703125f; // 7.03125% of player speed (50% slower than before)
    private static final float EXPLOSION_COOLDOWN = 15f;
    private static final float ACID_COOLDOWN = 1f;
    private static final float CONTACT_DAMAGE_COOLDOWN = 2f;
    private static final int EXPLOSION_DAMAGE = 50;
    private static final int CONTACT_DAMAGE = 5;
    private static final int ACID_DAMAGE = 5;
    private static final int BOSS_HEALTH = 1000; // Boss health increased to 1000
    
    private float explosionTimer = 0f;
    private float acidTimer = 0f;
    private float contactDamageTimer = 0f;
    private List<AcidProjectile> acidProjectiles;
    private Texture bossTexture;
    
    // Add the missing fields
    private int damage;
    private float attackCooldown;
    private float explosionCooldown;
    private float acidSpitCooldown;
    private Rectangle bounds;

    public BossZombie(float x, float y) {
        super(x, y, BOSS_HEALTH, 1.5f, 0.75f, // Using BOSS_HEALTH constant
              "images/zombie/boss_zombie.png",
              "images/zombie/boss_zombie.png");
        this.damage = 20;
        this.attackCooldown = 2.0f;
        this.explosionCooldown = 5.0f;
        this.acidSpitCooldown = 3.0f;
        this.acidProjectiles = new ArrayList<>();
        this.bossTexture = new Texture(Gdx.files.internal("images/bosses/boss.png"));
        // Initialize bounds with the correct size
        this.bounds = new Rectangle(x, y, BOSS_SIZE, BOSS_SIZE);
    }

    @Override
    public void update(Player player) {
        if (isDead) return;

        // Update timers
        float deltaTime = Gdx.graphics.getDeltaTime();
        explosionTimer += deltaTime;
        acidTimer += deltaTime;
        contactDamageTimer += deltaTime;

        // Update rotation to face player
        updateRotation(player);

        // Move towards player
        float dx = player.getX() - x;
        float dy = player.getY() - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            x += (dx / distance) * moveSpeed;
            y += (dy / distance) * moveSpeed;
            bounds.setPosition(x, y);
        }

        // Check for contact damage
        if (distance <= BOSS_SIZE/2 && contactDamageTimer >= CONTACT_DAMAGE_COOLDOWN) {
            attack(player);
            contactDamageTimer = 0f;
        }

        // Check for acid attack
        if (acidTimer >= ACID_COOLDOWN) {
            spitAcid(player);
            acidTimer = 0f;
        }

        // Update acid projectiles and check for collisions
        Iterator<AcidProjectile> iterator = acidProjectiles.iterator();
        while (iterator.hasNext()) {
            AcidProjectile projectile = iterator.next();
            projectile.update(deltaTime);
            
            // Check if projectile hits player
            if (projectile.isActive() && projectile.getBounds().overlaps(player.getBounds())) {
                player.takeDamage(ACID_DAMAGE);
                projectile.setActive(false);
            }
            
            if (!projectile.isActive()) {
                iterator.remove();
            }
        }
    }

    @Override
    public void attack(Player player) {
        player.takeDamage(CONTACT_DAMAGE);
        SoundManager.getInstance().playZombieBite(); // Play normal zombie bite sound
    }

    private void explode(Player player) {
        // Check if player is in explosion range
        float dx = player.getX() - x;
        float dy = player.getY() - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance <= BOSS_SIZE * 2) { // Explosion radius is twice the boss size
            player.takeDamage(EXPLOSION_DAMAGE);
        }
        
        SoundManager.getInstance().playExplosion(); // Play explosion sound
    }

    private void spitAcid(Player player) {
        // Create an 8-directional pattern of acid projectiles
        float centerX = x + BOSS_SIZE/2;
        float centerY = y + BOSS_SIZE/2;
        float diagonalDistance = 1000 * (float)Math.sqrt(2); // Diagonal distance to maintain consistent speed
        
        // Cardinal directions (right, up, left, down)
        acidProjectiles.add(new AcidProjectile(centerX, centerY, centerX + 1000, centerY));      // Right
        acidProjectiles.add(new AcidProjectile(centerX, centerY, centerX, centerY + 1000));      // Up
        acidProjectiles.add(new AcidProjectile(centerX, centerY, centerX - 1000, centerY));      // Left
        acidProjectiles.add(new AcidProjectile(centerX, centerY, centerX, centerY - 1000));      // Down
        
        // Diagonal directions
        acidProjectiles.add(new AcidProjectile(centerX, centerY, centerX + diagonalDistance, centerY + diagonalDistance));    // Top-right
        acidProjectiles.add(new AcidProjectile(centerX, centerY, centerX - diagonalDistance, centerY + diagonalDistance));    // Top-left
        acidProjectiles.add(new AcidProjectile(centerX, centerY, centerX + diagonalDistance, centerY - diagonalDistance));    // Bottom-right
        acidProjectiles.add(new AcidProjectile(centerX, centerY, centerX - diagonalDistance, centerY - diagonalDistance));    // Bottom-left
        
        SoundManager.getInstance().playAcidSpit();
    }

    @Override
    public void render(SpriteBatch batch) {
        if (isDead) return;

        // Draw boss with the correct size
        batch.draw(bossTexture, x, y, BOSS_SIZE/2, BOSS_SIZE/2,
                  BOSS_SIZE, BOSS_SIZE, 1, 1, rotationAngle,
                  0, 0, bossTexture.getWidth(), bossTexture.getHeight(), false, false);

        // Draw acid projectiles
        for (AcidProjectile projectile : acidProjectiles) {
            projectile.render(batch);
        }
    }

    @Override
    public void dispose() {
        if (bossTexture != null) {
            bossTexture.dispose();
        }
        for (AcidProjectile projectile : acidProjectiles) {
            projectile.dispose();
        }
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            isDead = true;
            // Stop all music and show win screen
            SoundManager.getInstance().stopAllMusic();
            ZombieGame.getInstance().setScreen(new WinScreen());
        }
    }

    public int getHealth() {
        return health;
    }

    public boolean isDead() {
        return isDead;
    }
} 