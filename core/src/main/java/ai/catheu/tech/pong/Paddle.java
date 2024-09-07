package ai.catheu.tech.pong;

import com.badlogic.gdx.math.MathUtils;

public class Paddle {

    public static final float WIDTH = 5;
    private static final float HEIGHT = 20;

    private final Main game;
    private final float x;
    private float y;
    public float dy = 0;

    public Paddle(final Main game, final float x, final float y) {
        this.game = game;
        this.x = x;
        this.y = y;
    }

    public void update(final float dt) {
        y +=  dy  * dt;
        y = MathUtils.clamp(y, 0, Main.WORLD_HEIGHT - HEIGHT);
    }

    public void draw() {
        game.shape.rect(x, y, WIDTH, HEIGHT);
    }
}
