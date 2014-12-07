package de.doccrazy.ld31.game.actor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Mainline.Key;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.Player.PlayerListener;
import com.brashmonkey.spriter.PlayerTweener;

import de.doccrazy.ld31.core.Resource;
import de.doccrazy.shared.game.actor.SpriterActor;
import de.doccrazy.shared.game.base.CollisionListener;
import de.doccrazy.shared.game.base.MovementInputListener;
import de.doccrazy.shared.game.world.BodyBuilder;
import de.doccrazy.shared.game.world.Box2dWorld;
import de.doccrazy.shared.game.world.ShapeBuilder;

public class PlayerActor extends SpriterActor implements CollisionListener, AttackInputListener.Consumer {
	private static final int CONTACT_TTL = 50;
	private static final float VELOCITY = 10.f;
	private static final float JUMP_IMPULSE = 50f;
	private static final float AIR_CONTROL = 30f;
	private static final float SCALE = 0.0027f;

	private final PlayerTweener tweener = new PlayerTweener(Resource.SPRITER.char1);
	private final Player punchPlayer = new Player(Resource.SPRITER.char1);
	private MovementInputListener movement;
	private Map<Body, ContactInfo> floorContacts = new HashMap<Body, ContactInfo>();
	private boolean moving;
	private float orientation = 1;
	private int tweenTo = -1;
	private AttackType currentAttack;
	private Body fistBody;
	private float chargeTime;
	private boolean playingIntro;

	public PlayerActor(Box2dWorld world, Vector2 spawn) {
        super(world, spawn, true, Resource.SPRITER.char1, Resource.SPRITER::getDrawer);
        setHeight(getHeight() * 2);
        setUseRotation(false);

        player = tweener;
        tweener.getFirstPlayer().setAnimation("idle");
        tweener.getSecondPlayer().setAnimation("walk");
        punchPlayer.setScale(SCALE);
        punchPlayer.addListener(new PlayerListener() {

			@Override
			public void animationFinished(Animation animation) {
				if (currentAttack != null) {
					if (!currentAttack.isCharge() || animation.name.equals(currentAttack.getFollowup())) {
						currentAttack = null;
						fistBody.setActive(false);
						player = tweener;
					} else if (playingIntro && animation.name.equals(currentAttack.getIntro())) {
						punchPlayer.setAnimation(currentAttack.toString().toLowerCase());
						playingIntro = false;
						chargeTime = 0f;
					}
				}
			}

			@Override
			public void animationChanged(Animation oldAnim, Animation newAnim) {
			}

			@Override
			public void preProcess(Player player) {
			}

			@Override
			public void postProcess(Player player) {
			}

			@Override
			public void mainlineKeyChanged(Key prevKey, Key newKey) {
			}

        });
        tweener.setWeight(0f);
        player.setScale(SCALE);
        player.setAnimation("idle");

        fistBody = BodyBuilder.forDynamic(Vector2.Zero)
                .fixShape(ShapeBuilder.circle(0.15f))//.fixSensor();
                .build(world);
        fistBody.setUserData(this);
		fistBody.setActive(false);
        Player.Attachment a = new Player.Attachment(punchPlayer.getObject("point_fist")) {
            @Override
            protected void setPosition(float x, float y) {
                float rot = fistBody.getAngle();
                x += getX() + getOriginX();
                y += getY() + getOriginY();
                fistBody.setTransform(x, y, rot);
            }

            @Override
            protected void setScale(float xscale, float yscale) {

            }

            @Override
            protected void setAngle(float angle) {
                Vector2 pos = fistBody.getPosition();
                fistBody.setTransform(pos, MathUtils.degreesToRadians * angle);
            }
        };
        punchPlayer.attachments.add(a);

        movement = new MovementInputListener();
        addListener(movement);
        addListener(new AttackInputListener(this));
    }

	@Override
	public void startAttack(AttackType type) {
		if (currentAttack == null) {
			currentAttack = type;
			punchPlayer.setTime(0);
			playingIntro = type.hasIntro();
			punchPlayer.setAnimation(playingIntro ? type.getIntro() : type.toString().toLowerCase());
			player = punchPlayer;
			fistBody.setActive(!type.isCharge());
			chargeTime = type.isCharge() ? 0f : 1f;
		}
	}

	@Override
	public void stopAttack(AttackType type) {
		if (playingIntro) {
			player = tweener;
			playingIntro = false;
			currentAttack = null;
		} else if (isCharging()) {
			punchPlayer.setAnimation(currentAttack.getFollowup());
			punchPlayer.setTime(0);
			if (currentAttack == AttackType.SHOOT_HOLD) {
				Vector2 speed = Vector2.X.cpy().rotateRad(fistBody.getAngle()).scl(10f);
				world.addActor(new BulletActor(world, fistBody.getPosition(), speed, currentAttack.getDamage() * (chargeTime + 1f)));
			} else {
				fistBody.setActive(true);
			}
		}
	}

	private boolean isCharging() {
		return currentAttack != null && currentAttack.isCharge() && !fistBody.isActive();
	}

    @Override
    protected BodyBuilder createBody(Vector2 spawn) {
        return BodyBuilder.forDynamic(spawn).velocity(Vector2.Zero, 2f)
                .fixShape(ShapeBuilder.circle(0.2f)).fixProps(3f, 0.1f, 100f);
    }

	@Override
	protected void doAct(float delta) {
		processContacts();
		if (isCharging()) {
			chargeTime += delta;
		}
		move(delta);
		animate(delta);
		super.doAct(delta);
	}

	private void animate(float delta) {
		if (touchingFloor()) {
     		tweener.getSecondPlayer().setAnimation("walk");
     	} else {
     		tweener.getSecondPlayer().setAnimation("fly");
     	}

		if (moving || !touchingFloor()) {
     		tweenTo = 1;
     	} else {
     		tweenTo = -1;
     	}
     	if (orientation != player.flippedX()) {
     		player.flipX();
     	}
		tweener.setWeight(MathUtils.clamp(tweener.getWeight() + tweenTo*2*delta, 0, 1));
	}

	private void processContacts() {
		for (Iterator<Entry<Body, ContactInfo>> it = floorContacts.entrySet().iterator(); it.hasNext(); ) {
			Entry<Body, ContactInfo> entry = it.next();
			entry.getValue().ttl -= 1;
			if (entry.getValue().ttl <= 0) {
				it.remove();
			}
		}
	}

    private void move(float delta) {
     	Vector2 mv = movement.getMovement();
     	moving = Math.abs(mv.x) > 0;
     	if (moving && currentAttack == null) {
     		orientation = Math.signum(mv.x);
     	}

     	if (mv.x == 0 || Math.signum(mv.x) == Math.signum(orientation)) {
	     	if (touchingFloor()) {
	     		float mult = isCharging() ? currentAttack.getChargeMove() : 1f;
	     		body.setAngularVelocity(-mv.x*VELOCITY*mult);
	     	} else {
	     		body.applyForceToCenter(mv.x * AIR_CONTROL, 0f, true);
	     	}
     	}

     	if (movement.pollJump() && touchingFloor() && !isCharging()) {
     		body.applyLinearImpulse(0f, JUMP_IMPULSE, body.getPosition().x, body.getPosition().y, true);
     		floorContacts.clear();
     		//Resource.jump.play();
     	}
    }

    private boolean touchingFloor() {
    	return floorContacts.size() > 0;
    }

	public void addFloorContact(Body body, Vector2 point) {
		floorContacts.put(body, new ContactInfo(Integer.MAX_VALUE, point));
	}

	public void removeFloorContact(Body body) {
		ContactInfo info = floorContacts.get(body);
		if (info != null) {
			info.ttl = CONTACT_TTL;
		}
	}

	@Override
	public boolean beginContact(Body me, Body other, Vector2 normal, Vector2 contactPoint) {
		boolean enabled = true;
		if (normal.y > 0.707f && !other.getFixtureList().get(0).isSensor()) {   //45 deg
			addFloorContact(other, contactPoint);
		}
		if (me.equals(fistBody)) {
			enabled = false;
		}
		if (me.equals(fistBody) && other.getUserData() instanceof HitListener) {
			Vector2 ct = contactPoint.cpy();
			Timer.post(new Task() {
				@Override
				public void run() {
					fistBody.setActive(false);  //only one hit
					((HitListener)other.getUserData()).onHit(new Vector2(currentAttack.getDamage() * chargeTime * orientation, 0), ct);
				}
			});
		}
		return enabled;
	}

	@Override
	public void endContact(Body other) {
		removeFloorContact(other);
	}

	@Override
	public void hit(float force) {
	}

	private static class ContactInfo {
		private int ttl;
		private Vector2 p;
		ContactInfo(int ttl, Vector2 p) {
			this.ttl = ttl;
			this.p = p;
		}
	}
}
