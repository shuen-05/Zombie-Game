package zombie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AcidZombie extends BaseZombie {
    private static final float ATTACK_RANGE = 600f;
    private List<AcidProjectile> projectiles;

    public AcidZombie(float x, float y) {
        super(x, y, 15, 0.8f, 1.5f,
              "images/zombies/acid_zombie.png",
              "images/zombies/acid_zombie_attack.png");
        projectiles = new ArrayList<>();
    }

    @Override
    public void update(Player player) {
        if (isDead) return;

        // Update rotation to face player
        updateRotation(player);

        // Update attack cooldown
        if (attackCooldown > 0) {
            attackCooldown -= Gdx.graphics.getDeltaTime();
        }
        
        // Set attacking state for a short duration when shooting
        isAttacking = attackCooldown > attackCooldownTime - 0.3f;  // Show attack animation for 0.3 seconds

        // Calculate distance to player
        float dx = player.getX() - x;
        float dy = player.getY() - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Try to maintain ideal range (70% of attack range)
        float idealRange = ATTACK_RANGE * 0.8f;
        
        // Back away if too close (within 80% of ideal range)
        if (distance < idealRange * 0.8f) {
            // Move away from player
            if (distance > 0) {
                x -= (dx / distance) * moveSpeed;
                y -= (dy / distance) * moveSpeed;
                bounds.setPosition(x, y);
            }
        } 
        // Move closer if too far (beyond 120% of ideal range)
        else if (distance > idealRange * 1.2f) {
            // Move towards player
            if (distance > 0) {
                x += (dx / distance) * moveSpeed;
                y += (dy / distance) * moveSpeed;
                bounds.setPosition(x, y);
            }
        }

        // Attack if within range and can attack
        if (distance <= ATTACK_RANGE && canAttack()) {
            attack(player);
        }

        // Update projectiles
        Iterator<AcidProjectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            AcidProjectile projectile = iterator.next();
            projectile.update(Gdx.graphics.getDeltaTime());
            
            // Check if projectile hits player
            if (projectile.isActive() && projectile.getBounds().overlaps(player.getBounds())) {
                player.takeDamage(projectile.getDamage());
                projectile.setActive(false);
            }
            
            if (!projectile.isActive()) {
                iterator.remove();
            }
        }
    }

    @Override
    public void attack(Player player) {
        if (!canAttack()) return;

        // Create new acid projectile aimed at player
        projectiles.add(new AcidProjectile(
            x + SIZE/2,
            y + SIZE/2,
            player.getX() + 15,
            player.getY() + 15
        ));

        // Play acid spit sound
        SoundManager.getInstance().playAcidSpit();

        resetAttackCooldown();
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        // Render projectiles
        for (AcidProjectile projectile : projectiles) {
            projectile.render(batch);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        AcidProjectile.dispose();
    }
} 