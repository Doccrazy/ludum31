package de.doccrazy.ld31.game.ui;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import de.doccrazy.shared.core.Debug;

public class UiInputListener extends InputListener {
	private UiRoot root;

    public UiInputListener(UiRoot root) {
        this.root = root;
	}

	@Override
    public boolean keyDown(InputEvent event, int keycode) {
		if (keycode == Keys.ENTER/* && world.isGameFinished()*/) {
			root.getWorld().reset();
			root.getInput().reset();
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
