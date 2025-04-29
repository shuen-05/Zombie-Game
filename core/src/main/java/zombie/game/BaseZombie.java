package zombie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class BaseZombie {
    protected float x, y;
    protected int health;
    protected boolean isDead = false;
    protected float moveSpeed;
    protected float attackCooldown = 0;
    protected float attackCooldownTime;
    protected Texture idleTexture;
    protected Texture attackTexture;
    protected boolean isAttacking = false;
    protected Rectangle bounds;
    protected static final float SIZE = 30f;
    protected float rotationAngle = 0f;

    public BaseZombie(float x, float y, int health, float moveSpeed, float attackCooldownTime, String idleTexturePath, String attackTexturePath) {
        this.x = x;
        this.y = y;
        this.health = health;
        this.moveSpeed = moveSpeed;
        this.attackCooldownTime = attackCooldownTime;
        this.bounds = new Rectangle(x, y, SIZE, SIZE);
        
        try {
            this.idleTexture = new Texture(Gdx.files.internal(idleTexturePath));
            this.attackTexture = new Texture(Gdx.files.internal(attackTexturePath));
            Gdx.app.log("Zombie", "Zombie textures loaded successfully");
        } catch (Exception e) {
            Gdx.app.error("Zombie", "Failed to load zombie textures", e);
        }
    }

    public abstract void update(Player player);
    public abstract void attack(Player player);

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            isDead = true;
        }
    }

    public boolean canAttack() {
        return attackCooldown <= 0;
    }

    public void resetAttackCooldown() {
        attackCooldown = attackCooldownTime;
    }

    public boolean isDead() {
        return isDead;
    }

    public void render(SpriteBatch batch) {
        if (!isDead) {
            try {
                if (isAttacking) {
                    batch.draw(attackTexture, x, y, SIZE/2, SIZE/2, SIZE, SIZE, 1, 1, rotationAngle, 0, 0, attackTexture.getWidth(), attackTexture.getHeight(), false, false);
                } else {
                    batch.draw(idleTexture, x, y, SIZE/2, SIZE/2, SIZE, SIZE, 1, 1, rotationAngle, 0, 0, idleTexture.getWidth(), idleTexture.getHeight(), false, false);
                }
            } catch (Exception e) {
                Gdx.app.error("Zombie", "Failed to render zombie", e);
            }
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

    protected void updateRotation(Player player) {
        float dx = player.getX() + player.getBounds().width/2 - (x + SIZE/2);
        float dy = player.getY() + player.getBounds().height/2 - (y + SIZE/2);
        rotationAngle = (float) Math.toDegrees(Math.atan2(dy, dx));
    }
} 