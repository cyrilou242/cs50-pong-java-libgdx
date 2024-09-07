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
import java.util.Random;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    public static final int WORLD_WIDTH = 432;
    public static final int WORLD_HEIGHT = 243;
    public static final int PADDLE_SPEED = 200;
    private static final int PADDLE_HEIGHT = 20;

    private SpriteBatch batch;
    private BitmapFont smallFont;
    private BitmapFont scoreFont;
    private FitViewport viewport;
    private ShapeRenderer shape;
    private int player1Score;
    private int player2Score;
    private float player1Y;
    private float player2Y;
    private float ballX;
    private float ballY;
    private int ballDx;
    private int ballDy;

    private Random randomGen;
    private GameState gameState;

    private enum GameState {
        START,
        PLAY
    }

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
        smallFont.setColor(Color.WHITE);

        parameter.size = 32;
        scoreFont = generator.generateFont(parameter);
        scoreFont.getRegion().getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        scoreFont.setColor(Color.WHITE);

        generator.dispose(); // don't forget to dispose to avoid memory leaks!

        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT));
        viewport.getCamera().position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        viewport.getCamera().update();

        randomGen = new Random();

        player1Score = 0;
        player2Score = 0;
        player1Y = WORLD_HEIGHT - 30 - 20;
        player2Y = 30;

        initBall();

        gameState  = GameState.START;
    }

    private void initBall() {
        ballX = WORLD_WIDTH / 2 - 2;
        ballY = WORLD_HEIGHT / 2 - 2;
        ballDx = randomGen.nextBoolean() ? 100 : -100;
        ballDy = randomGen.nextInt(0, 101) - 50;
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    private void input() {
        if (gameState == GameState.PLAY) {
            ballX += ballDx * Gdx.graphics.getDeltaTime();
            ballY += ballDy * Gdx.graphics.getDeltaTime();
        }

        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            player2Y -= PADDLE_SPEED * Gdx.graphics.getDeltaTime();
            player2Y = Math.max(0, player2Y);
        } else if (Gdx.input.isKeyPressed(Keys.UP)) {
            player2Y += PADDLE_SPEED * Gdx.graphics.getDeltaTime();
            player2Y = Math.min( WORLD_HEIGHT - PADDLE_HEIGHT, player2Y);
        }

        if (Gdx.input.isKeyPressed(Keys.S)) {
            player1Y -= PADDLE_SPEED * Gdx.graphics.getDeltaTime();
            player1Y = Math.max(0, player1Y);
        } else if (Gdx.input.isKeyPressed(Keys.Z) || Gdx.input.isKeyPressed(Keys.W)) {
            // Z or W to be compatible with both AZERTY and QWERTY in a simple way
            player1Y += PADDLE_SPEED * Gdx.graphics.getDeltaTime();
            player1Y = Math.min( WORLD_HEIGHT - PADDLE_HEIGHT, player1Y);
        }

        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
        } else if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.BACKSPACE)) {
            if (gameState == GameState.START) {
                gameState = GameState.PLAY;
            } else {
                gameState = GameState.START;
                initBall();
            }
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
        final GlyphLayout layout = new GlyphLayout(smallFont, "Hello " + gameState + " state!");
        smallFont.draw(batch, layout, (WORLD_WIDTH - layout.width) / 2, WORLD_HEIGHT - 20);
        scoreFont.draw(batch, String.valueOf(player1Score), WORLD_WIDTH / 2 - 50, WORLD_HEIGHT - (WORLD_HEIGHT / 3));
        scoreFont.draw(batch, String.valueOf(player2Score), WORLD_WIDTH / 2 + 30, WORLD_HEIGHT - (WORLD_HEIGHT / 3));
        batch.end();

        // the rectangles
        shape.setProjectionMatrix(viewport.getCamera().combined);
        shape.begin(ShapeType.Filled);
        shape.setColor(Color.WHITE);
        shape.rect(10, player1Y, 5, PADDLE_HEIGHT);
        shape.rect(WORLD_WIDTH - 10 - 5, player2Y, 5, PADDLE_HEIGHT);
        shape.rect(ballX, ballY, 4, 4);

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
