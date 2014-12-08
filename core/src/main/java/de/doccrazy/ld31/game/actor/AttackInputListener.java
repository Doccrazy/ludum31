package de.doccrazy.ld31.game.actor;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import de.doccrazy.ld31.data.AttackType;

public class AttackInputListener extends InputListener {
	private Consumer listener;

	public AttackInputListener(Consumer listener) {
		this.listener = listener;
	}

    @Override
	public boolean keyDown(InputEvent event, int keycode) {
    	if (Keys.A == keycode) {
    		listener.startAttack(AttackType.PUNCH);
    		return true;
    	}
    	if (Keys.S == keycode) {
    		listener.startAttack(AttackType.CHARGE);
    		return true;
    	}
    	if (Keys.D == keycode) {
    		listener.startAttack(AttackType.SHOOT_HOLD);
    		return true;
    	}
    	if (Keys.SHIFT_LEFT == keycode) {
    		listener.startBlock();
    		return true;
    	}
    	return false;
    }

    @Override
    public boolean keyUp(InputEvent event, int keycode) {
    	if (Keys.A == keycode) {
    		listener.stopAttack(AttackType.PUNCH);
    		return true;
    	}
    	if (Keys.S == keycode) {
    		listener.stopAttack(AttackType.CHARGE);
    		return true;
    	}
    	if (Keys.D == keycode) {
    		listener.stopAttack(AttackType.SHOOT_HOLD);
    		return true;
    	}
    	if (Keys.SHIFT_LEFT == keycode) {
    		listener.stopBlock();
    		return true;
    	}
    	return false;
    }

    public interface Consumer {
    	void startAttack(AttackType type);

    	void stopAttack(AttackType type);

    	void startBlock();

    	void stopBlock();
    }
}
