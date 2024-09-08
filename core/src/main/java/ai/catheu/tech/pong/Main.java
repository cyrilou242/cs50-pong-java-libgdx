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

    private SpriteBatch batch;
    private BitmapFont smallFont;
    private BitmapFont scoreFont;
    private FitViewport viewport;
    public ShapeRenderer shape;
    private int player1Score;
    private int player2Score;

    public Random randomGen;
    private GameState gameState;
    private Ball ball;
    private Paddle player1;
    private Paddle player2;
    private int servingPlayer;

    private enum GameState {
        START,
        PLAY,
        SERVE
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
        player1 = new Paddle(this,10,  WORLD_HEIGHT - 30 - 20);
        player2 = new Paddle(this,WORLD_WIDTH - 10 - Paddle.WIDTH,  30);

        ball = new Ball(this, 4, 4);

        gameState  = GameState.START;
    }

    @Override
    public void render() {
        input();
        draw();
    }

    private void input() {
        if (gameState == GameState.SERVE) {
            ball.dy = randomGen.nextInt(0, 101) - 50;
            if (servingPlayer == 1) {
                ball.dx = randomGen.nextInt(140, 200) ;
            } else {
                ball.dx = -randomGen.nextInt(140, 200);
            }
        } else if (gameState == GameState.PLAY) {
            // detect ball collision with paddles, reversing dx if true and
            // slightly increasing it, then altering the dy based on the position of collision
            if (ball.collides(player1)) {
                ball.dx = -ball.dx * 1.03f;
                ball.x = player1.x + Paddle.WIDTH;
                if (ball.dy < 0) {
                    ball.dy = -randomGen.nextInt(10, 151);
                } else {
                    ball.dy = randomGen.nextInt(10, 151);
                }
            }
            if (ball.collides(player2)) {
                ball.dx = -ball.dx * 1.03f;
                ball.x = player2.x - ball.width;
                if (ball.dy < 0) {
                    ball.dy = -randomGen.nextInt(10, 151);
                } else {
                    ball.dy = randomGen.nextInt(10, 151);
                }
            }

            if (ball.y <= 0) {
                ball.y = 0;
                ball.dy = -ball.dy;
            }
            if (ball.y >= WORLD_HEIGHT - ball.height) {
                ball.y = WORLD_HEIGHT - ball.height;
                ball.dy = -ball.dy;
            }
        }

        if (ball.x < 0) {
            servingPlayer = 1;
            player2Score++;
            ball.reset();
            gameState = GameState.SERVE;
        } else if (ball.x >= WORLD_WIDTH - ball.width) {
            servingPlayer = 2;
            player1Score++;
            ball.reset();
            gameState = GameState.SERVE;
        }


        if (gameState == GameState.PLAY) {
            ball.update();
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            player2.dy = -PADDLE_SPEED;
        } else if (Gdx.input.isKeyPressed(Keys.UP)) {
            player2.dy = PADDLE_SPEED;
        } else {
            player2.dy = 0;
        }

        if (Gdx.input.isKeyPressed(Keys.S)) {
            player1.dy = -PADDLE_SPEED;
        } else if (Gdx.input.isKeyPressed(Keys.Z) || Gdx.input.isKeyPressed(Keys.W)) {
            // Z or W to be compatible with both AZERTY and QWERTY in a simple way
            player1.dy = PADDLE_SPEED;
        } else {
          player1.dy = 0;
        }
        player1.update(Gdx.graphics.getDeltaTime());
        player2.update(Gdx.graphics.getDeltaTime());

        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
        } else if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.BACKSPACE)) {
            if (gameState == GameState.START) {
                gameState = GameState.SERVE;
            } else if (gameState == GameState.SERVE) {
                gameState = GameState.PLAY;
            }
        }
    }

    private void draw() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        viewport.apply();

        // the rectangles
        shape.setProjectionMatrix(viewport.getCamera().combined);
        shape.begin(ShapeType.Filled);
        shape.setColor(Color.WHITE);
        player1.draw();
        player2.draw();
        ball.draw();
        shape.end();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        // glyph layout is used to center - we can get its width
        smallFont.setColor(Color.WHITE);
        if (gameState == GameState.START) {
            final GlyphLayout welcomeLayout = new GlyphLayout(smallFont, "Welcome to Pong!");
            smallFont.draw(batch, welcomeLayout, (WORLD_WIDTH - welcomeLayout.width) / 2, WORLD_HEIGHT - 10);
            final GlyphLayout pressEnterLayout = new GlyphLayout(smallFont, "Press enter to begin!");
            smallFont.draw(batch, pressEnterLayout, (WORLD_WIDTH - pressEnterLayout.width) / 2, WORLD_HEIGHT - 20);
        } else if (gameState == GameState.SERVE) {
            final GlyphLayout playerTurnLayout = new GlyphLayout(smallFont, "Player " + servingPlayer + "'s serve!");
            smallFont.draw(batch, playerTurnLayout, (WORLD_WIDTH - playerTurnLayout.width) / 2, WORLD_HEIGHT - 10);
            final GlyphLayout pressEnterLayout = new GlyphLayout(smallFont, "Press enter to serve!");
            smallFont.draw(batch, pressEnterLayout, (WORLD_WIDTH - pressEnterLayout.width) / 2, WORLD_HEIGHT - 20);
        } else if (gameState == GameState.PLAY) {
            // no UI messages to display
        }

        smallFont.setColor(Color.GREEN);
        final GlyphLayout fpsLayout = new GlyphLayout(smallFont, "FPS: " + Gdx.graphics.getFramesPerSecond());
        smallFont.draw(batch, fpsLayout, 10, WORLD_HEIGHT - 10);
        scoreFont.draw(batch, String.valueOf(player1Score), WORLD_WIDTH / 2 - 50, WORLD_HEIGHT - (WORLD_HEIGHT / 3));
        scoreFont.draw(batch, String.valueOf(player2Score), WORLD_WIDTH / 2 + 30, WORLD_HEIGHT - (WORLD_HEIGHT / 3));
        batch.end();
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
