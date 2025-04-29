package zombie.game.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import zombie.game.ZombieGame;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        // Set the working directory to the assets folder
        System.setProperty("org.lwjgl.system.librarypath", System.getProperty("user.dir") + "/assets");
        
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Zombie Apocalypse");
        config.setWindowedMode(1280, 720);
        config.useVsync(true);
        config.setForegroundFPS(60);
        
        new Lwjgl3Application(new ZombieGame(), config);
    }
}

