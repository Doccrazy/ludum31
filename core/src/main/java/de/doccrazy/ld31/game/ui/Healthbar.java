package de.doccrazy.ld31.game.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import de.doccrazy.ld31.core.Resource;
import de.doccrazy.ld31.data.GameRules;
import de.doccrazy.ld31.game.actor.PlayerActor;

public class Healthbar extends Widget {
	private UiRoot root;
	private int playerIndex;
	private boolean right;
	private float t;

	public Healthbar(UiRoot root, int playerIndex) {
		this.root = root;
		this.playerIndex = playerIndex;
		right = playerIndex == 1;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		t += delta;
		//setWidth(getStage().getWidth()/2);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		boolean dead = root.getWorld().getPlayer(playerIndex).getHealth() <= 0;
		float fill = MathUtils.clamp(getPlayer().getHealth() / GameRules.PLAYER_HEATH, 0, 1);
		int w = (int) (right ? getWidth()*(1-fill) : getWidth()*fill);
		Texture texEmpty = Resource.GFX.healthbarEmpty;
		if (dead && t % 0.5 < 0.25) {
			texEmpty = Resource.GFX.healthbarEmptyRed;
		}
		batch.draw(right ? Resource.GFX.healthbarFull : texEmpty,
				getX(), getY(), getWidth()-w, getHeight(), 0, 0, (int)(getWidth()-w), (int)getHeight(), false, false);
		batch.draw(right ? texEmpty : Resource.GFX.healthbarFull,
				getX()+(getWidth()-w), getY(), w, getHeight(), 0, 0, w, (int)getHeight(), false, false);
	}

	private PlayerActor getPlayer() {
		return root.getWorld().getPlayer(playerIndex);
	}

	@Override
	public float getPrefWidth () {
		return getStage().getWidth()/2;
	}

	@Override
	public float getPrefHeight () {
		return 40;
	}

	@Override
	public float getMinWidth() {
		return 0;
	}

	@Override
	public float getMinHeight() {
		return 0;
	}
}
