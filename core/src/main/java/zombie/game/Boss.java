package zombie.game;

public class Boss {
    float x, y;
    int health = 300;

    public Boss(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(Player player) {
        // Simple follow logic
        if (player.x > x) x += 0.3;
        else x -= 0.3;
    }

    public void render() {
        Utils.drawRect(x, y, 60, 60, 1f, 0f, 0f); // red boss
    }

    public boolean isDefeated() {
        return health <= 0;
    }

    public void specialAttack() {
        if (health < 25) {
            // Spawn minions or shoot projectiles
        }
    }
}
