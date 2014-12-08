package de.doccrazy.ld31.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import de.doccrazy.ld31.core.Resource;

public class KOLabel extends Label {
	private UiRoot root;

	public KOLabel(UiRoot root) {
		super("KO", new LabelStyle(Resource.FONT.retro, new Color(1, 1, 1, 1)));
		this.root = root;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		//setText(player2 ? (root.getWorld().isMultiplayer() ? "P2" : "AI") : "P1");
	}
}
