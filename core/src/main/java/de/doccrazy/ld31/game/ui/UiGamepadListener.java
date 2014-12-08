package de.doccrazy.ld31.game.ui;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;

import de.doccrazy.ld31.data.GamepadActions;

public class UiGamepadListener extends ControllerAdapter {
	private final UiRoot root;
	private boolean init = false;
	private int configure = 0;
	private Map<Integer, GamepadActions> actionMap = new HashMap<>();

	public UiGamepadListener(UiRoot root) {
		this.root = root;
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonIndex) {
		if (!init) {
			//controller detected
			init = true;
			return true;
		}
		if (!isConfigured() && !root.getWorld().isGameInProgress()) {
			actionMap.put(buttonIndex, GamepadActions.values()[configure]);
			configure++;
			if (isConfigured()) {
				root.getWorld().controllerConfigured(actionMap);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonIndex) {
		return false;
	}

	public boolean isSupported() {
		return Controllers.getControllers().size > 0;
	}

	public boolean isInit() {
		return init;
	}

	public boolean isConfigured() {
		return configure >= GamepadActions.values().length;
	}

	public GamepadActions getNextAction() {
		return GamepadActions.values()[configure];
	}

}
