package ai.catheu.tech.pong;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    public static final int WORLD_WIDTH = 432;
    public static final int WORLD_HEIGHT = 243;
    private SpriteBatch batch;
    private BitmapFont smallFont;
    private FitViewport viewport;
    private ShapeRenderer shape;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shape = new ShapeRenderer();

        // init font
        final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        final FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 8;
        smallFont = generator.generateFont(parameter);
        smallFont.getRegion().getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        smallFont.setColor(1, 1,1,1);

        generator.dispose(); // don't forget to dispose to avoid memory leaks!
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
        // glyph layout is used to center - we can get its width
        final GlyphLayout layout = new GlyphLayout(smallFont, "Hello Pong!");
        smallFont.draw(batch, layout, (WORLD_WIDTH - layout.width) / 2, WORLD_HEIGHT - 20);
        batch.end();

        // the rectangles
        shape.setProjectionMatrix(viewport.getCamera().combined);
        shape.begin(ShapeType.Filled);
        shape.setColor(Color.WHITE);
        shape.rect(10, WORLD_HEIGHT - 30 - 20, 5, 20);
        shape.rect(WORLD_WIDTH - 10 - 5, 30, 5, 20);
        shape.rect(WORLD_WIDTH / 2 -2 , WORLD_HEIGHT / 2 - 2, 4, 4);

        shape.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        smallFont.dispose();
        shape.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
