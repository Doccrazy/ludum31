package de.doccrazy.ld31.game.world;

import com.badlogic.gdx.math.Vector2;
import de.doccrazy.ld31.data.GameRules;
import de.doccrazy.ld31.game.actor.TestActor;
import de.doccrazy.shared.game.world.Box2dWorld;
import de.doccrazy.shared.game.world.GameState;

public class GameWorld extends Box2dWorld {

    public GameWorld() {
        super(GameRules.GRAVITY);
    }

    @Override
    protected void doTransition(GameState newState) {
        switch (newState) {
            case INIT:
                addActor(new TestActor(this, new Vector2(10, 10)));
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
