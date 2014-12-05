package de.doccrazy.ld31.game.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import de.doccrazy.ld31.game.GameRenderer;
import de.doccrazy.ld31.game.world.GameInputListener;
import de.doccrazy.ld31.game.world.GameWorld;
import de.doccrazy.shared.game.ui.UiBase;

public class UiRoot extends UiBase<GameWorld, GameRenderer, GameInputListener> {

	public UiRoot(Stage stage, GameWorld world, GameRenderer renderer) {
		super(stage, world, renderer);
	}
}
