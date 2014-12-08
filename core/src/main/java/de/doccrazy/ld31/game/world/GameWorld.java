package de.doccrazy.ld31.game.world;

import java.util.Map;
import java.util.Map.Entry;

import box2dLight.RayHandler;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.Vector2;

import de.doccrazy.ld31.core.Resource;
import de.doccrazy.ld31.data.GameRules;
import de.doccrazy.ld31.data.GamepadActions;
import de.doccrazy.ld31.game.actor.AttackControllerListener;
import de.doccrazy.ld31.game.actor.FloorActor;
import de.doccrazy.ld31.game.actor.PlayerActor;
import de.doccrazy.ld31.game.actor.WallActor;
import de.doccrazy.shared.game.actor.ParticleActor;
import de.doccrazy.shared.game.actor.ParticleEvent;
import de.doccrazy.shared.game.base.GamepadMovementListener;
import de.doccrazy.shared.game.world.Box2dWorld;
import de.doccrazy.shared.game.world.GameState;

public class GameWorld extends Box2dWorld {

    private PlayerActor[] players;
    private int[] scores = new int[2];
	private boolean multiplayer, waitingForRound, gameOver;
	private int round;
	private Map<Integer, GamepadActions> actionMap;
	private boolean partInit;

	public GameWorld() {
        super(GameRules.GRAVITY);
        RayHandler.useDiffuseLight(true);
    }

    @Override
    protected void doTransition(GameState newState) {
        switch (newState) {
            case INIT:
            	Resource.MUSIC.intro.play();
            	waitingForRound = false;
            	players = new PlayerActor[2];
            	addActor(players[0] = new PlayerActor(this, new Vector2(2, 0.25f), 0));
                addActor(players[1] = new PlayerActor(this, new Vector2(GameRules.LEVEL_WIDTH-2, 0.25f), 1).flip());
                addActor(new WallActor(this, new Vector2(0, -0.5f),
                		new Vector2(GameRules.WALL_WIDTH, GameRules.LEVEL_HEIGHT+0.5f)));
                addActor(new WallActor(this, new Vector2(GameRules.LEVEL_WIDTH-GameRules.WALL_WIDTH, -0.5f),
                		new Vector2(GameRules.WALL_WIDTH, GameRules.LEVEL_HEIGHT+0.5f)));
                addActor(new FloorActor(this, new Vector2(0, -1), new Vector2(12, 1)));
                //addActor(new PunchingBagActor(this, new Vector2(8, 2.5f)));
                //addActor(new DummyActor(this, new Vector2(8, 0.0f)));

                if (!partInit) {
                	partInit = true;
                	addActor(new ParticleActor(this));
                	postEvent(new ParticleEvent(GameRules.LEVEL_WIDTH/2, GameRules.LEVEL_HEIGHT, Resource.GFX.snow));
                }
                break;
            case PRE_GAME:
            	round++;
                break;
            case GAME:
            	Resource.MUSIC.intro.stop();
            	Resource.MUSIC.victory.stop();
            	Resource.MUSIC.fight[(int)(Math.random()*Resource.MUSIC.fight.length)].play();
                players[0].setupKeyboardControl();
                stage.setKeyboardFocus(players[0]);
                if (multiplayer) {
                	activateController(players[1]);
                } else {
                	players[1].setupAiControl();
                }
                break;
            case VICTORY:
            case DEFEAT:
            	for (Music m : Resource.MUSIC.fight) {
            		m.stop();
            	}
            	Resource.MUSIC.victory.play();
            	players[0].setupController(null);
            	if (multiplayer) {
            		players[1].setupController(null);
            	}
        }
    }

    @Override
    protected void doUpdate(float delta) {
    	switch (getGameState()) {
    	case GAME:
	    	if (players[1].isDead()) {
	    		scores[0]++;
	    		transition(GameState.VICTORY);
	    	} else if (players[0].isDead()) {
	    		scores[1]++;
	    		transition(GameState.DEFEAT);
	    	}
	    	if (scores[0] >= GameRules.ROUNDS_TO_WIN || scores[1] >= GameRules.ROUNDS_TO_WIN) {
	    		gameOver = true;
	    	}
    		break;
    	case PRE_GAME:
    		if (getStateTime() > 0.5f) {
    			transition(GameState.GAME);
    		}
    		break;
		default:
    	}
    }

    public void controllerConfigured(Map<Integer, GamepadActions> actionMap) {
		this.actionMap = actionMap;
		multiplayer = true;
    }

    public void activateController(PlayerActor player) {
		int jumpAction = -1;
		for (Entry<Integer, GamepadActions> entry : actionMap.entrySet()) {
			if (entry.getValue() == GamepadActions.JUMP) {
				jumpAction = entry.getKey();
			}
		}
		GamepadMovementListener moveListener = new GamepadMovementListener(jumpAction);
		Controllers.addListener(moveListener);
		AttackControllerListener attackListener = new AttackControllerListener(actionMap, player);
		Controllers.addListener(attackListener);
		player.setupController(moveListener);
    }

    public PlayerActor getPlayer(int index) {
		return players[index];
	}

    public String getPlayerName(int index) {
    	return index > 0 ? (isMultiplayer() ? "P2" : "AI") : "P1";
    }

    public int getPlayerScore(int index) {
    	return scores[index];
    }

    public boolean isMultiplayer() {
    	return multiplayer;
    }

    public int getRound() {
    	return round;
    }

    public boolean isGameOver() {
    	return gameOver;
    }

    public void waitingForRound() {
    	waitingForRound = true;
    }

    public boolean isWaitingForRound() {
    	return waitingForRound;
    }
}
