package de.doccrazy.ld31.game.actor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;

import de.doccrazy.ld31.data.AttackType;
import de.doccrazy.ld31.data.GameRules;
import de.doccrazy.ld31.game.world.GameWorld;
import de.doccrazy.shared.game.base.MovementInputListener;

public class AiController extends Action implements MovementInputListener {
	private GameWorld world;
	private PlayerActor playerActor;
	private PlayerActor enemy;
	private Vector2 move = new Vector2(0, 0);
	private boolean jump;
	private float remainingCharge;

	public AiController(PlayerActor playerActor) {
		this.playerActor = playerActor;
		world = (GameWorld) playerActor.getWorld();
		enemy = world.getPlayer(0);
		if (enemy == playerActor) {
			enemy = world.getPlayer(1);
		}
	}

	@Override
	public Vector2 getMovement() {
		return move;
	}

	@Override
	public boolean isJump() {
		return jump;
	}

	@Override
	public boolean pollJump() {
		if (jump) {
			jump = false;
			return true;
		}
		return false;
	}

	@Override
	public boolean act(float delta) {
		if (remainingCharge > 0) {
			remainingCharge -= delta;
		}
		float dist = enemy.getX() - playerActor.getX();
		boolean chargingPunch = enemy.isCharging() && enemy.getCurrentAttack() == AttackType.CHARGE;
		boolean chargingShot = enemy.isCharging() && enemy.getCurrentAttack() == AttackType.SHOOT_HOLD;

		if (enemy.isDead()) {
			dist = GameRules.LEVEL_WIDTH/2 - playerActor.getX();
			if (Math.abs(dist) > 1) {
				move.x = Math.signum(dist);
			} else {
				move.x = 0;
			}
			return false;
		}

		if (playerActor.isCharging() && remainingCharge <= 0) {
			playerActor.stopAttack(playerActor.getCurrentAttack());
		} else if (Math.signum(dist) != Math.signum(playerActor.getOrientation())) {
			move.x = Math.signum(dist);
		} else if (Math.abs(dist) > 2 && Math.random() < 0.001 && !playerActor.isCharging()) {
			playerActor.startAttack(AttackType.SHOOT_HOLD);
			remainingCharge = (float) (Math.random()*1f + 1f);
		} else if (Math.abs(dist) > 0.6) {
			move.x = Math.signum(dist);
		} else {
			move.x = 0;
			if (!playerActor.isCharging()) {
				if (Math.random() < 0.5) {
					playerActor.startAttack(AttackType.CHARGE);
					remainingCharge = (float) (Math.random()*1.5f + 0.25f);
				} else {
					playerActor.startAttack(AttackType.PUNCH);
				}
			}
		}
		if (Math.abs(dist) < 1.5 && chargingPunch) {
			move.x = -Math.signum(dist);
		}
		return false;
	}

}
