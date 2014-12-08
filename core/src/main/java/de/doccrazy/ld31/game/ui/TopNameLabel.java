package de.doccrazy.ld31.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import de.doccrazy.ld31.core.Resource;

public class TopNameLabel extends Label {
	private UiRoot root;
	private int playerIndex;

	public TopNameLabel(UiRoot root, int playerIndex) {
		super("P1", new LabelStyle(Resource.FONT.retro, new Color(1, 1, 1, 1)));
		this.root = root;
		this.playerIndex = playerIndex;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		setText(root.getWorld().getPlayerName(playerIndex) + " " + root.getWorld().getPlayerScore(playerIndex));
		setVisible(root.getWorld().isGameInProgress() || root.getWorld().isGameFinished());
	}
}
