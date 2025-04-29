package zombie.game;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;

public class Utils {
    private static ShapeRenderer shapeRenderer;
    private static BitmapFont font;
    private static SpriteBatch batch;

    public static void init(SpriteBatch gameBatch) {
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f); // Make text larger
        batch = gameBatch;
    }

    public static BitmapFont getFont() {
        return font;
    }

    public static void drawRect(float x, float y, float width, float height, float r, float g, float b) {
        shapeRenderer.setColor(r, g, b, 1);
        shapeRenderer.rect(x, y, width, height);
    }

    public static void drawText(String text, float x, float y) {
        font.draw(batch, text, x, y);
    }

    public static void begin() {
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    }

    public static void end() {
        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
    }

    public static void drawTexture(Texture texture, float x, float y, int width, int height) {
        batch.draw(texture, x, y, width, height);
    }

    public static void drawCircle(float x, float y, float radius, float r, float g, float b, float a) {
        shapeRenderer.setColor(r, g, b, a);
        shapeRenderer.circle(x, y, radius);
    }

    public static void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }
}
