package ai.catheu.tech.pong;

import com.badlogic.gdx.Gdx;

public class Ball {

    private final Main game;
    private float x;
    private float y;
    private final int width;
    private final int height;
    private float dx;
    private float dy;

    public Ball(final Main game, final int width, final int height) {
        this.game = game;
        this.width = width;
        this.height = height;

        reset();
    }

    public void reset() {
        x = (game.WORLD_WIDTH - width) / 2;
        y = (game.WORLD_HEIGHT - height) / 2;
        dx = game.randomGen.nextBoolean() ? 100 : -100;
        dy = game.randomGen.nextInt(0, 101) - 50;
    }

    public void update() {
        x += dx * Gdx.graphics.getDeltaTime();
        y += dy * Gdx.graphics.getDeltaTime();
    }

    public void draw() {
        game.shape.rect(x, y, 4, 4);
    }
}
