package de.doccrazy.ld31.game.actor;

import java.util.Map;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;

import de.doccrazy.ld31.data.AttackType;
import de.doccrazy.ld31.data.GamepadActions;
import de.doccrazy.ld31.game.actor.AttackInputListener.Consumer;

public class AttackControllerListener extends ControllerAdapter {
	private Map<Integer, GamepadActions> actionMap;
	private Consumer listener;

	public AttackControllerListener(Map<Integer, GamepadActions> actionMap, AttackInputListener.Consumer listener) {
		this.actionMap = actionMap;
		this.listener = listener;
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonIndex) {
		GamepadActions action = actionMap.get(buttonIndex);
    	if (GamepadActions.PUNCH == action) {
    		listener.startAttack(AttackType.PUNCH);
    		return true;
    	}
    	if (GamepadActions.STRONG_PUNCH == action) {
    		listener.startAttack(AttackType.CHARGE);
    		return true;
    	}
    	if (GamepadActions.CHARGED_SHOT == action) {
    		listener.startAttack(AttackType.SHOOT_HOLD);
    		return true;
    	}
    	if (GamepadActions.BLOCK == action) {
    		listener.startBlock();
    		return true;
    	}
    	return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonIndex) {
		GamepadActions action = actionMap.get(buttonIndex);
    	if (GamepadActions.PUNCH == action) {
    		listener.stopAttack(AttackType.PUNCH);
    		return true;
    	}
    	if (GamepadActions.STRONG_PUNCH == action) {
    		listener.stopAttack(AttackType.CHARGE);
    		return true;
    	}
    	if (GamepadActions.CHARGED_SHOT == action) {
    		listener.stopAttack(AttackType.SHOOT_HOLD);
    		return true;
    	}
    	if (GamepadActions.BLOCK == action) {
    		listener.stopBlock();
    		return true;
    	}
    	return false;
	}
}
