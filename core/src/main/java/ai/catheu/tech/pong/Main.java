package ai.catheu.tech.pong;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    public static final int WORLD_WIDTH = 432;
    public static final int WORLD_HEIGHT = 243;
    private SpriteBatch batch;
    private BitmapFont font;
    private FitViewport viewport;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(1, 1,1,1);
        font.getRegion().getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT));
        viewport.getCamera().position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        viewport.getCamera().update();
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    private void input() {
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void logic() {

    }

    private void draw() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        font.draw(batch, "Hello pong", WORLD_WIDTH / 2 -40, WORLD_HEIGHT/2);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
