package zombie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class TextureLoader {
    private static Map<String, Texture> textureCache;
    private static SpriteBatch gameBatch;
    private static String assetsPath;

    public static void init(SpriteBatch batch) {
        gameBatch = batch;
        textureCache = new HashMap<>();
        
        // Try to find the assets directory
        File currentDir = new File(".");
        File assetsDir = new File(currentDir, "assets");
        if (assetsDir.exists()) {
            assetsPath = "";  // Assets are in the root directory
        } else {
            assetsDir = new File(currentDir.getParentFile(), "assets");
            if (assetsDir.exists()) {
                assetsPath = "../";  // Assets are in the parent directory
            } else {
                assetsPath = "";
            }
        }
        Gdx.app.log("TextureLoader", "Assets path set to: " + assetsPath);
    }

    private static Texture createPlaceholderTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public static Texture loadTexture(String path) {
        if (textureCache.containsKey(path)) {
            return textureCache.get(path);
        }
        
        try {
            // Try loading with the detected assets path
            String fullPath = assetsPath + path;
            Gdx.app.log("TextureLoader", "Attempting to load texture from: " + fullPath);
            
            if (Gdx.files.internal(fullPath).exists()) {
                Gdx.app.log("TextureLoader", "File exists at: " + fullPath);
                Texture texture = new Texture(Gdx.files.internal(fullPath));
                textureCache.put(path, texture);
                return texture;
            } else {
                Gdx.app.error("TextureLoader", "File does not exist at: " + fullPath);
                throw new RuntimeException("File not found: " + fullPath);
            }
        } catch (Exception e) {
            Gdx.app.error("TextureLoader", "Failed to load texture: " + path + ", creating placeholder", e);
            // Create placeholder textures based on the type
            Texture placeholder;
            if (path.contains("player")) {
                placeholder = createPlaceholderTexture(30, 30, Color.BLUE);
            } else if (path.contains("zombie")) {
                placeholder = createPlaceholderTexture(30, 30, Color.GREEN);
            } else if (path.contains("background")) {
                placeholder = createPlaceholderTexture(1280, 720, Color.DARK_GRAY);
            } else {
                placeholder = createPlaceholderTexture(30, 30, Color.WHITE);
            }
            textureCache.put(path, placeholder);
            return placeholder;
        }
    }

    public static void drawImage(Texture texture, float x, float y, float width, float height) {
        if (texture == null || gameBatch == null) return;
        gameBatch.draw(texture, x, y, width, height);
    }

    public static void dispose() {
        if (textureCache != null) {
            for (Texture texture : textureCache.values()) {
                texture.dispose();
            }
            textureCache.clear();
        }
    }
}
