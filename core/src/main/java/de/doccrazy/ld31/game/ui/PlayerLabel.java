package de.doccrazy.ld31.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import de.doccrazy.ld31.core.Resource;
import de.doccrazy.ld31.game.actor.PlayerActor;

public class PlayerLabel extends Label {
	private UiRoot root;
	private int playerIndex;

	public PlayerLabel(UiRoot root, int playerIndex) {
		super("P1", new LabelStyle(Resource.FONT.retro, new Color(1, 1, 1, 1)));
		setWidth(0);
		this.root = root;
		this.playerIndex = playerIndex;
        setAlignment(Align.center);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		setVisible(!getPlayer().isDead()
				&& (root.getWorld().isGameInProgress() || root.getWorld().isGameFinished()));
		setText(root.getWorld().getPlayerName(playerIndex));
		Vector2 pos = new Vector2(getPlayer().getX(), getPlayer().getY());
        pos.add(getPlayer().getOriginX(), 2.3f);
        pos = root.getWorld().stage.stageToScreenCoordinates(pos);
        pos.y += getHeight();
        pos = getStage().screenToStageCoordinates(pos);
        setPosition(pos.x, pos.y);
	}

	private PlayerActor getPlayer() {
		return root.getWorld().getPlayer(playerIndex);
	}
}
