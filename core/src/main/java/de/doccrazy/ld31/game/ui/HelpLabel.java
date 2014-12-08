package de.doccrazy.ld31.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import de.doccrazy.ld31.core.Resource;
import de.doccrazy.shared.game.world.GameState;

public class HelpLabel extends Label {
	private UiRoot root;

	public HelpLabel(UiRoot root) {
		super("Controls:\n\n"
				+ "Arrows:  Move        Space:  Jump         \n"
				+ "A:       Punch       S:      Charged punch\n"
				+ "D:       Hamekameka  Shift:  Block        \n\n", new LabelStyle(Resource.FONT.retroSmall, new Color(1, 1, 1, 1)));
		this.root = root;
		setAlignment(Align.bottom);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		setWidth(getStage().getWidth());
		setVisible(root.getWorld().getGameState() == GameState.INIT);
	}
}
