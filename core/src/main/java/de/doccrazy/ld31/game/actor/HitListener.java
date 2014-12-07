package de.doccrazy.ld31.game.actor;

import com.badlogic.gdx.math.Vector2;

public interface HitListener {
	void onHit(Vector2 force, Vector2 contactPoint);
}
