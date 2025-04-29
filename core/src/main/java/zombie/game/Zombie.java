package zombie.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Gdx;

public class Zombie extends BaseZombie {
    private static final float ATTACK_DISTANCE = 30f;
    private float damageTimer = 0f;
    private boolean isInContact = false;

    public Zombie(float x, float y) {
        super(x, y, 15, 1.0f, 1.0f,
              "images/zombies/basic_zombie.png",
              "images/zombies/basic_zombie.png");
    }

    @Override
    public void update(Player player) {
        if (isDead) return;

        // Update rotation to face player
        updateRotation(player);

        // Update attack cooldown
        if (attackCooldown > 0) {
            attackCooldown -= Gdx.graphics.getDeltaTime();
            isAttacking = true;
        } else {
            isAttacking = false;
        }

        float dx = player.getX() - x;
        float dy = player.getY() - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance > 0) {
            x += (dx / distance) * moveSpeed;
            y += (dy / distance) * moveSpeed;
            bounds.setPosition(x, y);
        }

        // Check if in contact with player
        isInContact = distance <= ATTACK_DISTANCE;
        
        // Apply continuous damage if in contact
        if (isInContact) {
            damageTimer += Gdx.graphics.getDeltaTime();
            if (damageTimer >= 1.0f) {  // Every second
                player.takeDamage(5);
                SoundManager.getInstance().playZombieBite();  // Play bite sound for continuous damage
                damageTimer = 0f;
            }
        } else {
            damageTimer = 0f;
        }

        // Initial attack if close enough
        if (distance <= ATTACK_DISTANCE && canAttack()) {
            attack(player);
        }
    }

    @Override
    public void attack(Player player) {
        if (canAttack()) {
            player.takeDamage(5);  // Initial contact damage
            SoundManager.getInstance().playZombieBite();  // Play bite sound
            resetAttackCooldown();
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

    public void dispose() {
        if (idleTexture != null) {
            idleTexture.dispose();
        }
        if (attackTexture != null) {
            attackTexture.dispose();
        }
    }
}
