package de.doccrazy.ld31.game.actor;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.brashmonkey.spriter.Player;

import de.doccrazy.ld31.core.Resource;
import de.doccrazy.shared.game.actor.SpriterActor;
import de.doccrazy.shared.game.world.BodyBuilder;
import de.doccrazy.shared.game.world.Box2dWorld;
import de.doccrazy.shared.game.world.ShapeBuilder;

public class TestActor extends SpriterActor {
    private Body circle;

    public TestActor(Box2dWorld world, Vector2 spawn) {
        super(world, spawn, true, Resource.SPRITER.guy, Resource.SPRITER::getDrawer);
        setHeight(getHeight() * 2);
        setUseRotation(false);

        player.setScale(0.02f);
        player.setAnimation("work");

        circle = BodyBuilder.forDynamic(Vector2.Zero)
                .fixShape(ShapeBuilder.circle(1)).fixSensor()
                .build(world);
        Player.Attachment a = new Player.Attachment(player.getObject("p_tip")) {
            @Override
            protected void setPosition(float x, float y) {
                float rot = circle.getAngle();
                x += getX() + getOriginX();
                y += getY() + getOriginY();
                circle.setTransform(x, y, rot);
            }

            @Override
            protected void setScale(float xscale, float yscale) {

            }

            @Override
            protected void setAngle(float angle) {
                Vector2 pos = circle.getPosition();
                circle.setTransform(pos, MathUtils.degreesToRadians * angle);
            }
        };
        player.attachments.add(a);

        task.every(0.05f, Void -> {
            BodyBuilder.forDynamic(circle.getPosition())
                    .velocity(new Vector2(10f, 0).rotateRad(circle.getAngle()))
                    .fixShape(ShapeBuilder.circle(0.1f))
                    .build(world);
        });
        task.in(2f, Void -> {
            BodyBuilder.forDynamic(circle.getPosition())
                    .velocity(new Vector2(15f, 0).rotateRad(circle.getAngle()))
                    .fixShape(ShapeBuilder.circle(0.3f))
                    .build(world);
        }).thenEvery(0.2f, Void -> {
            BodyBuilder.forDynamic(circle.getPosition())
            .velocity(new Vector2(15f, 0).rotateRad(circle.getAngle()))
            .fixShape(ShapeBuilder.circle(0.3f))
            .build(world);
        });
        task.during(2f, t -> {
            System.out.println(t);
        }).thenWait(1f).then(0f, Void -> {
            System.out.println("done");
        });
    }

    @Override
    protected BodyBuilder createBody(Vector2 spawn) {
        return BodyBuilder.forDynamic(spawn).velocity(Vector2.Zero, 2f)
                .fixShape(ShapeBuilder.box(1, 1));
    }
}
