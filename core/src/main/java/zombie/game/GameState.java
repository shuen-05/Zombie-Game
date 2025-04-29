package zombie.game;

import com.badlogic.gdx.Screen;

public class GameState {
    private static Screen currentScreen;
    private static boolean isPaused = false;

    public static void init() {
        currentScreen = null;
        isPaused = false;
    }

    public static void setCurrentScreen(Screen screen) {
        if (currentScreen != null) {
            currentScreen.hide();
        }
        currentScreen = screen;
        if (currentScreen != null) {
            currentScreen.show();
        }
    }

    public static void update() {
        if (currentScreen != null && !isPaused) {
            if (currentScreen instanceof GameScreen) {
                ((GameScreen) currentScreen).update();
            }
        }
    }

    public static void render(float delta) {
        if (currentScreen != null) {
            currentScreen.render(delta);
        }
    }

    public static void pause() {
        isPaused = true;
    }

    public static void resume() {
        isPaused = false;
    }

    public static boolean isPaused() {
        return isPaused;
    }
}
