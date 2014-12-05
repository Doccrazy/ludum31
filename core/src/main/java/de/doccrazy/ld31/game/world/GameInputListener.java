package de.doccrazy.ld31.game.world;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import de.doccrazy.ld31.game.ui.UiRoot;
import de.doccrazy.shared.game.world.GameState;

public class GameInputListener extends InputListener {
    private GameWorld world;
    private UiRoot root;

    public GameInputListener(UiRoot root) {
        this.root = root;
        this.world = root.getWorld();
        reset();
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (world.getGameState() != GameState.GAME) {
            return false;
        }
        if (button == 0) {
        }
        if (button == 1) {
        }
        return false;
    }

    public void reset() {

    }
}
