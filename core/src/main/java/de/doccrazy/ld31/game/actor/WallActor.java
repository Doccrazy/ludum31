package de.doccrazy.ld31.game.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import de.doccrazy.ld31.core.Resource;
import de.doccrazy.shared.game.actor.ParticleEvent;
import de.doccrazy.shared.game.actor.ShapeActor;
import de.doccrazy.shared.game.base.CollisionListener;
import de.doccrazy.shared.game.world.BodyBuilder;
import de.doccrazy.shared.game.world.Box2dWorld;
import de.doccrazy.shared.game.world.ShapeBuilder;

public class WallActor extends ShapeActor implements CollisionListener {
	private Vector2 size;
	private ShapeRenderer shapeRenderer = new ShapeRenderer(100);
	private int health = 400;
	private Texture texture = Resource.GFX.texBrick[0];

	public WallActor(Box2dWorld world, Vector2 position, Vector2 size) {
		super(world, position, true);
		this.size = size;
	}

	@Override
	protected void doAct(float delta) {
		super.doAct(delta);
		if (health <= 0) {
			body.setActive(false);
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (texture != null) {
			batch.draw(texture, getX(), getY(), getWidth(), getHeight(), 0, 0, (int)(getWidth()*500f), (int)(getHeight()*500f), false, false);
		}
		/*batch.end();
		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(0, 0, 0, 1);
		shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
		Gdx.gl.glLineWidth(3);
		shapeRenderer.flush();
		shapeRenderer.end();
		Gdx.gl.glLineWidth(1);
		batch.begin();*/
	}

	private int level() {
		int lvl = 4-(health+99)/100;
		return lvl;
	}

	@Override
	protected BodyBuilder createBody(Vector2 spawn) {
		return BodyBuilder.forStatic(spawn)
				.fixShape(ShapeBuilder.box(size.x/2, size.y/2)).fixProps(0, 0, 0);
	}

	@Override
	public boolean beginContact(Body me, Body other, Vector2 normal, Vector2 contactPoint) {
		return true;
	}

	@Override
	public void endContact(Body other) {
	}

	@Override
	public void hit(float force) {
		if (force > 50) {
			int lvlOld = level();
			health -= Math.min(force, 100);
			if (lvlOld != level()) {
				world.postEvent(new ParticleEvent(getX()+getOriginX(), getY()+getOriginY(), Resource.GFX.dust));
				task.in(0.25f, Void -> {
					texture = level() < Resource.GFX.texBrick.length ? Resource.GFX.texBrick[level()] : null;
				});
			}
		}
	}

}
