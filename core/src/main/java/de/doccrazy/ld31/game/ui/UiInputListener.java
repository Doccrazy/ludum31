package de.doccrazy.ld31.game.ui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import de.doccrazy.shared.core.Debug;
import de.doccrazy.shared.game.world.GameState;

public class UiInputListener extends InputListener {
	private UiRoot root;

    public UiInputListener(UiRoot root) {
        this.root = root;
	}

	@Override
    public boolean keyDown(InputEvent event, int keycode) {
		if (keycode == Keys.ENTER
				&& (root.getWorld().getGameState() == GameState.INIT || root.getWorld().isWaitingForRound())) {
			root.getWorld().transition(GameState.INIT);
			root.getWorld().transition(GameState.PRE_GAME);
			root.getAnnounce().skip();
			root.getAnnounce().add("Round " + root.getWorld().getRound(), 0.2f);
			root.getAnnounce().add("Fight!", 0.2f);
		}
		if (Debug.ON) {
			/*if (keycode == Keys.Z) {
				root.getRenderer().setZoomDelta(1f);
			}*/
		}
		return false;
	}

	@Override
	public boolean keyUp(InputEvent event, int keycode) {
        if (Debug.ON) {
            /*if (keycode == Keys.Z) {
                root.getRenderer().setZoomDelta(-2f);
            }*/
        }
        return false;
	}
}
