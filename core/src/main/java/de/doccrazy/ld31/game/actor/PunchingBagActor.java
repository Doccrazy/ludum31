package de.doccrazy.ld31.game.actor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;

import de.doccrazy.shared.game.actor.ShapeActor;
import de.doccrazy.shared.game.world.BodyBuilder;
import de.doccrazy.shared.game.world.Box2dWorld;
import de.doccrazy.shared.game.world.ShapeBuilder;

public class PunchingBagActor extends ShapeActor {
	private static final float RADIUS = 0.4f;
	private static final float RADIUS_LINK = 0.04f;

	public PunchingBagActor(Box2dWorld world, Vector2 spawn) {
		super(world, spawn, false);
	}

	@Override
	protected void init() {
		super.init();
		Body prevBody = body;
		Vector2 prevPos = new Vector2(spawn.x, spawn.y + RADIUS - RADIUS_LINK);
		for (int i = 0; i < 40; i++) {
			Vector2 linkPos = new Vector2(spawn.x, spawn.y + RADIUS + RADIUS_LINK + i*RADIUS_LINK*2);
			Body link = BodyBuilder.forDynamic(linkPos)
					.fixShape(ShapeBuilder.circle(RADIUS_LINK)).fixProps(3f, 0.1f, 10f)
					.fixFilter((short)1, (short)0)
					.build(world);
			Vector2 contactPoint = prevPos.cpy().add(linkPos).scl(0.5f);
			RevoluteJointDef jointDef = new RevoluteJointDef();
			jointDef.initialize(prevBody, link, contactPoint);
			jointDef.enableMotor = true;
			jointDef.maxMotorTorque = 1;
			//Joint joint = world.box2dWorld.createJoint(jointDef);

			RopeJointDef dist = new RopeJointDef();
			dist.bodyA = prevBody;
			dist.bodyB = link;
			dist.localAnchorA.set(dist.bodyA.getLocalPoint(contactPoint));
			dist.localAnchorB.set(dist.bodyB.getLocalPoint(contactPoint));
			dist.maxLength = 0.01f;
			Joint joint = world.box2dWorld.createJoint(dist);

			prevBody = link;
			prevPos = linkPos;
		}

		Body hook = BodyBuilder.forStatic(prevPos).fixShape(ShapeBuilder.circle(0.001f)).build(world);
		RevoluteJointDef jointDef = new RevoluteJointDef();
		jointDef.initialize(prevBody, hook, prevPos);
		jointDef.enableMotor = true;
		jointDef.maxMotorTorque = 1;
		world.box2dWorld.createJoint(jointDef);
	}

	@Override
	protected BodyBuilder createBody(Vector2 spawn) {
		return BodyBuilder.forDynamic(spawn)
				.fixShape(ShapeBuilder.circle(RADIUS)).fixProps(3f, 0.1f, 10f);
	}

}
