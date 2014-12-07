package de.doccrazy.ld31.game.world;

import com.badlogic.gdx.math.Vector2;

import de.doccrazy.ld31.data.GameRules;
import de.doccrazy.ld31.game.actor.DummyActor;
import de.doccrazy.ld31.game.actor.FloorActor;
import de.doccrazy.ld31.game.actor.PlayerActor;
import de.doccrazy.ld31.game.actor.WallActor;
import de.doccrazy.shared.game.world.Box2dWorld;
import de.doccrazy.shared.game.world.GameState;

public class GameWorld extends Box2dWorld {

    private PlayerActor player;

	public GameWorld() {
        super(GameRules.GRAVITY);
    }

    @Override
    protected void doTransition(GameState newState) {
        switch (newState) {
            case INIT:
                addActor(player = new PlayerActor(this, new Vector2(2, 1)));
                addActor(new WallActor(this, new Vector2(0, -0.5f), new Vector2(0.5f, 7.5f)));
                addActor(new WallActor(this, new Vector2(11.5f, -0.5f), new Vector2(0.5f, 7.5f)));
                addActor(new FloorActor(this, new Vector2(0, -1), new Vector2(12, 1)));
                //addActor(new PunchingBagActor(this, new Vector2(8, 2.5f)));
                addActor(new DummyActor(this, new Vector2(8, 0.0f)));
                stage.setKeyboardFocus(player);
                break;
            case PRE_GAME:
                break;
            case GAME:
                break;
        }
    }

    @Override
    protected void doUpdate(float delta) {

    }
}
