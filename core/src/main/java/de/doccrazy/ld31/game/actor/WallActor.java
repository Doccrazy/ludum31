package de.doccrazy.ld31.game.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import de.doccrazy.ld31.core.Resource;
import de.doccrazy.shared.game.actor.ShapeActor;
import de.doccrazy.shared.game.world.BodyBuilder;
import de.doccrazy.shared.game.world.Box2dWorld;
import de.doccrazy.shared.game.world.ShapeBuilder;

public class WallActor extends ShapeActor {
	private Vector2 size;
	private ShapeRenderer shapeRenderer = new ShapeRenderer(100);

	public WallActor(Box2dWorld world, Vector2 position, Vector2 size) {
		super(world, position, true);
		this.size = size;
	}

	@Override
	protected void doAct(float delta) {
		super.doAct(delta);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(Resource.GFX.texBrick, getX(), getY(), getWidth(), getHeight(), 0, 0, (int)(getWidth()*300f), (int)(getHeight()*300f), false, false);
		batch.end();
		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(0, 0, 0, 1);
		shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
		Gdx.gl.glLineWidth(3);
		shapeRenderer.flush();
		shapeRenderer.end();
		Gdx.gl.glLineWidth(1);
		batch.begin();
	}

	@Override
	protected BodyBuilder createBody(Vector2 spawn) {
		return BodyBuilder.forStatic(spawn)
				.fixShape(ShapeBuilder.box(size.x/2, size.y/2)).fixProps(0, 0, 0);
	}

}
