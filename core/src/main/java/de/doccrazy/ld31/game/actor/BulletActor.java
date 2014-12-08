package de.doccrazy.ld31.game.actor;

import box2dLight.PointLight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import de.doccrazy.ld31.core.Resource;
import de.doccrazy.ld31.data.AttackType;
import de.doccrazy.ld31.data.CollCategory;
import de.doccrazy.shared.game.actor.ShapeActor;
import de.doccrazy.shared.game.base.CollisionListener;
import de.doccrazy.shared.game.world.BodyBuilder;
import de.doccrazy.shared.game.world.Box2dWorld;
import de.doccrazy.shared.game.world.ShapeBuilder;

public class BulletActor extends ShapeActor implements CollisionListener {
	private Vector2 speed;
	private float damage;
	private int playerIndex;

	public BulletActor(Box2dWorld world, Vector2 spawn, Vector2 speed, float damage, int playerIndex) {
		super(world, spawn, false);
		this.speed = speed;
		this.damage = damage;
		this.playerIndex = playerIndex;
	}

	@Override
	protected void init() {
		super.init();
		PointLight light = new PointLight(world.rayHandler, 10, new Color(1f, 1f, 0f, 1f), 1f, -10, -10);
		light.setXray(true);
		lights.add(light);
	}

	@Override
	protected BodyBuilder createBody(Vector2 spawn) {
		return BodyBuilder.forDynamic(spawn)
				.velocity(speed, 50f).zeroGrav()
				.damping(0, 0)
				.fixShape(ShapeBuilder.circle(0.1f)).fixProps(3f, 0.1f, 100f)
				.fixGroup((short)(-playerIndex - 42))
				.fixFilter(CollCategory.BULLET, ((short)(-1 ^ CollCategory.PLAYER_FEET)));
	}

	@Override
	protected void doAct(float delta) {
		super.doAct(delta);
		lights.get(0).setPosition(body.getPosition());
		float flicker = (float) Math.sin(Math.PI * 4 * (stateTime % 0.5f))/2f;
		lights.get(0).setDistance(damage/AttackType.SHOOT_HOLD.getDamage()/2 + flicker/2);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(Resource.GFX.bullet, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
	}

	@Override
	public boolean beginContact(Body me, Body other, Vector2 normal, Vector2 contactPoint) {
		if (isDead()) {
			return false;
		}
		kill();
		if (other.getUserData() instanceof HitListener) {
			Vector2 force = me.getLinearVelocity().cpy().nor().scl(damage);
			((HitListener)other.getUserData()).onHit(this, AttackType.SHOOT_HOLD, force, contactPoint);
		}
		return false;
	}

	@Override
	public void endContact(Body other) {
	}

	@Override
	public void hit(float force) {
	}

	public int getPlayerIndex() {
		return playerIndex;
	}

}
