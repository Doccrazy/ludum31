package de.doccrazy.ld31.game.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import de.doccrazy.ld31.core.Resource;
import de.doccrazy.ld31.data.AttackType;
import de.doccrazy.ld31.data.CollCategory;
import de.doccrazy.shared.game.actor.ShapeActor;
import de.doccrazy.shared.game.actor.WorldActor;
import de.doccrazy.shared.game.world.BodyBuilder;
import de.doccrazy.shared.game.world.Box2dWorld;
import de.doccrazy.shared.game.world.ShapeBuilder;

public class DummyActor extends ShapeActor implements HitListener {
	private static final float WIDTH = 0.3f;
	private static final float HEIGHT = 1.9f;

	public DummyActor(Box2dWorld world, Vector2 spawn) {
		super(world, spawn, true);
	}

	@Override
	protected void init() {
		super.init();
		Body fix = BodyBuilder.forStatic(new Vector2(spawn.x + WIDTH/2, spawn.y + HEIGHT*0.1f))
				.fixShape(ShapeBuilder.box(0.01f, 0.01f)).build(world);

		RevoluteJointDef jointDef = new RevoluteJointDef();
		jointDef.initialize(body, fix, fix.getPosition());
		jointDef.enableMotor = true;
		jointDef.maxMotorTorque = 1;
		Joint joint = world.box2dWorld.createJoint(jointDef);

		fix = BodyBuilder.forStatic(new Vector2(spawn.x + WIDTH/2/* + WIDTH*3f*/, spawn.y + HEIGHT*1.1f))
				.fixShape(ShapeBuilder.box(0.01f, 0.01f)).build(world);

		DistanceJointDef dist = new DistanceJointDef();
		dist.initialize(fix, body, fix.getPosition(), new Vector2(spawn.x + WIDTH/2, spawn.y + HEIGHT));
		dist.frequencyHz = 3;
		dist.dampingRatio = 0.2f;
		joint = world.box2dWorld.createJoint(dist);
}

	@Override
	protected BodyBuilder createBody(Vector2 spawn) {
		return BodyBuilder.forDynamic(spawn)
				.fixShape(ShapeBuilder.box(WIDTH/2, HEIGHT/2)).fixProps(3f, 0.1f, 100f)
				.fixFilter(CollCategory.DUMMY, (short)-1);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(Resource.GFX.dummy, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
	}

	@Override
	public void onHit(WorldActor cause, AttackType type, Vector2 force, Vector2 contactPoint) {
		body.applyLinearImpulse(force, contactPoint, true);
		System.out.println("Damage " + force.len());
	}

}
