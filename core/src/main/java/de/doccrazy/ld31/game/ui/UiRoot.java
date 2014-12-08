package de.doccrazy.ld31.game.ui;

import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

import de.doccrazy.ld31.game.GameRenderer;
import de.doccrazy.ld31.game.world.GameInputListener;
import de.doccrazy.ld31.game.world.GameWorld;
import de.doccrazy.shared.game.ui.UiBase;
import de.doccrazy.shared.game.world.GameState;

public class UiRoot extends UiBase<GameWorld, GameRenderer, GameInputListener> {
	private UiGamepadListener padInput;
	private AnnouncerLabel announce;

	public UiRoot(Stage stage, GameWorld world, GameRenderer renderer) {
		super(stage, world, renderer);
		padInput = new UiGamepadListener(this);
		Controllers.addListener(padInput);

		add(new TopNameLabel(this, 0)).left().pad(5);
		add(new Healthbar(this, 0)).expandX().left().pad(5);
		add(new KOLabel(this)).expandX().center().pad(5);
		add(new Healthbar(this, 1)).expandX().right().pad(5);
		add(new TopNameLabel(this, 1)).right().pad(5);

		stage.addActor(new ControllerLabel(this));
		stage.addActor(new PlayerLabel(this, 0));
		stage.addActor(new PlayerLabel(this, 1));
		stage.addActor(new HelpLabel(this));
		stage.addActor(announce = new AnnouncerLabel(this));

		announce.add("Extreme\nStick Fighter\nUltimate 3i", 1.5f);
		announce.add("Press Enter", 99999f);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (getWorld().isGameFinished()) {
			String name = getWorld().getPlayerName(0);
			if (getWorld().getGameState() == GameState.DEFEAT) {
				name = getWorld().getPlayerName(1);
			}
			if (!getWorld().isGameOver() && !getWorld().isWaitingForRound()) {
				announce.add(name + " won round " + getWorld().getRound() + "!", 2f);
				announce.add("Press Enter", 99999f);
				getWorld().waitingForRound();
			} else if (getWorld().isGameOver()) {
				announce.add(name + " Victory!", 9999999f);
			}
		}
	}

	public UiGamepadListener getPadInput() {
		return padInput;
	}

	@Override
	protected InputListener createUiInput() {
		return new UiInputListener(this);
	}

	public AnnouncerLabel getAnnounce() {
		return announce;
	}
}
