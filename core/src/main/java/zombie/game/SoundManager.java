package zombie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
    private static SoundManager instance;
    private Sound explosionSound;
    private Sound rifleSound;
    private Sound shotgunSound;
    private Sound zombieGrowlSound;
    private Sound acidSpitSound;
    private Sound zombieBiteSound;
    private Sound takeDamageSound;
    private Music levelMusic;
    private Music bossMusic;
    private Music winMusic;
    private boolean isBossMusicPlaying = false;

    private SoundManager() {
        // Load all sound effects
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx/explosion.mp3"));
        rifleSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx/shoot_rifle.mp3"));
        shotgunSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx/shoot_shotgun.mp3"));
        zombieGrowlSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx/zombie_growl.mp3"));
        acidSpitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx/spit_acid.mp3"));
        zombieBiteSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx/zombie_bite.mp3"));
        takeDamageSound = Gdx.audio.newSound(Gdx.files.internal("sounds/sfx/take_damage.mp3"));
        
        // Load background music
        levelMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/music/level_music.mp3"));
        bossMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/music/boss_battle.mp3"));
        winMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/music/win_music.mp3"));
        
        // Set music properties
        levelMusic.setLooping(true);
        levelMusic.setVolume(0.3f);
        bossMusic.setLooping(true);
        bossMusic.setVolume(0.3f);
        winMusic.setLooping(true);
        winMusic.setVolume(0.3f);
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playExplosion() {
        explosionSound.play(0.8f);
    }

    public void playRifleShot() {
        rifleSound.play(0.3f);
    }

    public void playShotgunShot() {
        shotgunSound.play(0.4f);
    }

    public void playZombieGrowl() {
        zombieGrowlSound.play(0.15f);
    }

    public void playAcidSpit() {
        acidSpitSound.play(0.4f);  // Medium volume for acid spit sound
    }

    public void playZombieBite() {
        zombieBiteSound.play(0.5f);  // Medium volume for bite sound
    }

    public void playTakeDamage() {
        takeDamageSound.play(0.6f);  // Medium-high volume for damage sound
    }

    public void playLevelMusic() {
        if (isBossMusicPlaying) {
            bossMusic.stop();
            isBossMusicPlaying = false;
        }
        levelMusic.play();
    }

    public void playBossMusic() {
        levelMusic.stop();
        bossMusic.play();
        isBossMusicPlaying = true;
    }

    public void playWinMusic() {
        stopAllMusic();
        winMusic.play();
    }

    public void stopAllMusic() {
        levelMusic.stop();
        bossMusic.stop();
        winMusic.stop();
        isBossMusicPlaying = false;
    }

    public void dispose() {
        explosionSound.dispose();
        rifleSound.dispose();
        shotgunSound.dispose();
        zombieGrowlSound.dispose();
        acidSpitSound.dispose();
        zombieBiteSound.dispose();
        takeDamageSound.dispose();
        levelMusic.dispose();
        bossMusic.dispose();
        winMusic.dispose();
    }
}
