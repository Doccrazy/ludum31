package de.doccrazy.ld31.game.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import de.doccrazy.ld31.core.Resource;
import de.doccrazy.shared.game.actor.ShapeActor;
import de.doccrazy.shared.game.base.CollisionListener;
import de.doccrazy.shared.game.world.BodyBuilder;
import de.doccrazy.shared.game.world.Box2dWorld;
import de.doccrazy.shared.game.world.ShapeBuilder;

public class BulletActor extends ShapeActor implements CollisionListener {
	private Vector2 speed;
	private float damage;

	public BulletActor(Box2dWorld world, Vector2 spawn, Vector2 speed, float damage) {
		super(world, spawn, false);
		this.speed = speed;
		this.damage = damage;
	}

	@Override
	protected BodyBuilder createBody(Vector2 spawn) {
		return BodyBuilder.forDynamic(spawn)
				.velocity(speed, 50f).zeroGrav()
				.damping(0, 0)
				.fixShape(ShapeBuilder.circle(0.1f)).fixProps(3f, 0.1f, 100f);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(Resource.GFX.bullet, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
	}

	@Override
	public boolean beginContact(Body me, Body other, Vector2 normal, Vector2 contactPoint) {
		kill();
		if (other.getUserData() instanceof HitListener) {
			Vector2 force = me.getLinearVelocity().cpy().nor().scl(damage);
			((HitListener)other.getUserData()).onHit(force, contactPoint);
		}
		return false;
	}

	@Override
	public void endContact(Body other) {
	}

	@Override
	public void hit(float force) {
	}

}
