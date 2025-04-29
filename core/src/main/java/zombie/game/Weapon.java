package zombie.game;

public enum Weapon {
    RIFLE(0.15f),
    SHOTGUN(1.5f);

    private final float shootCooldown;

    Weapon(float shootCooldown) {
        this.shootCooldown = shootCooldown;
    }

    public float getShootCooldown() {
        return shootCooldown;
    }
}
