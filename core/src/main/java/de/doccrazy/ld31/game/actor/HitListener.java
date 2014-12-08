package de.doccrazy.ld31.game.actor;

import com.badlogic.gdx.math.Vector2;

import de.doccrazy.ld31.data.AttackType;
import de.doccrazy.shared.game.actor.WorldActor;

public interface HitListener {
	void onHit(WorldActor cause, AttackType type, Vector2 force, Vector2 contactPoint);
}
