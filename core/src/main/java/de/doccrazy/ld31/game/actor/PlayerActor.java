package de.doccrazy.ld31.game.actor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import box2dLight.PointLight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Mainline.Key;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.Player.PlayerListener;
import com.brashmonkey.spriter.PlayerTweener;

import de.doccrazy.ld31.core.Resource;
import de.doccrazy.ld31.data.AttackType;
import de.doccrazy.ld31.data.CollCategory;
import de.doccrazy.ld31.data.GameRules;
import de.doccrazy.shared.game.actor.ParticleEvent;
import de.doccrazy.shared.game.actor.SpriterActor;
import de.doccrazy.shared.game.actor.Tasker.OnceTaskDef;
import de.doccrazy.shared.game.actor.WorldActor;
import de.doccrazy.shared.game.base.CollisionListener;
import de.doccrazy.shared.game.base.KeyboardMovementListener;
import de.doccrazy.shared.game.base.MovementInputListener;
import de.doccrazy.shared.game.world.BodyBuilder;
import de.doccrazy.shared.game.world.Box2dWorld;
import de.doccrazy.shared.game.world.ShapeBuilder;

public class PlayerActor extends SpriterActor implements CollisionListener, AttackInputListener.Consumer,
		HitListener {
	private static final int CONTACT_TTL = 50;
	private static final float RADIUS = 0.2f;
	private static final float VELOCITY = 10f;
	private static final float JUMP_IMPULSE = 50f;
	private static final float HIT_IMPULSE = 30f;
	private static final float HITBOX_HEIGHT = 1.5f;
	private static final float HITBOX_WIDTH = 0.3f;
	private static final float AIR_CONTROL = 30f;
	private static final float SCALE = 0.0027f;

	private final PlayerTweener rootTweener;
	private final PlayerTweener walkTweener = new PlayerTweener(Resource.SPRITER.char1);
	private final Player punchPlayer = new Player(Resource.SPRITER.char1);
	private MovementInputListener movement;
	private Map<Body, ContactInfo> floorContacts = new HashMap<Body, ContactInfo>();
	private boolean moving;
	private float orientation = 1;
	private int tweenTo = -1;
	private int tweenToRoot = -1;
	private AttackType currentAttack;
	private Body fistBody, hitbox;
	private Joint hitboxJoint;
	private float chargeTime, staggerTime;
	private boolean playingIntro;
	private int index;
	private boolean blocking;
	private float health;
	private OnceTaskDef regenerate;
	private boolean chargeReleased;
	private PointLight shotLight;

	public PlayerActor(Box2dWorld world, Vector2 spawn, int index) {
        super(world, spawn, true, Resource.SPRITER.char1, Resource.SPRITER::getDrawer);
		this.index = index;
		health = GameRules.PLAYER_HEATH;
        setHeight(getHeight() * 2);
        setUseRotation(false);

        rootTweener = new PlayerTweener(walkTweener, new Player(Resource.SPRITER.char1));
        rootTweener.getSecondPlayer().setAnimation("block");
        rootTweener.setWeight(0);
        player = rootTweener;
        walkTweener.getFirstPlayer().setAnimation("idle");
        walkTweener.getSecondPlayer().setAnimation("idle");
        punchPlayer.addListener(new PlayerListener() {

			@Override
			public void animationFinished(Animation animation) {
				if (currentAttack != null) {
					if (!currentAttack.isCharge() || animation.name.equals(currentAttack.getFollowup())) {
						currentAttack = null;
						fistBody.setActive(false);
						player = rootTweener;
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
        walkTweener.setWeight(0f);

        fistBody = BodyBuilder.forDynamic(Vector2.Zero)
                .fixShape(ShapeBuilder.circle(0.15f))//.fixSensor();
                .fixGroup((short)(-index - 42))
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
                shotLight.setPosition(x, y);
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

        scalePlayer(player, SCALE);
        scalePlayer(punchPlayer, SCALE);

		shotLight = new PointLight(world.rayHandler, 10, new Color(1f, 1f, 0f, 1f), 1f, -10, -10);
		shotLight.setXray(true);
		shotLight.setActive(false);
		lights.add(shotLight);
        //rootTweener.setWeight(0.3f);
    }

	@Override
	protected void init() {
		super.init();
		hitbox = BodyBuilder.forDynamic(new Vector2(body.getPosition().x, body.getPosition().y+HITBOX_HEIGHT/2+RADIUS)).noRotate()
				.fixShape(ShapeBuilder.box(HITBOX_WIDTH/2, HITBOX_HEIGHT/2)).fixGroup((short)(-index - 42))
				.build(world);
		hitbox.setUserData(this);

		RevoluteJointDef def = new RevoluteJointDef();
		def.initialize(hitbox, body, body.getPosition());
		hitboxJoint = world.box2dWorld.createJoint(def);
	}

    @Override
    protected BodyBuilder createBody(Vector2 spawn) {
        return BodyBuilder.forDynamic(spawn).velocity(Vector2.Zero, 2f)
                .fixShape(ShapeBuilder.circle(RADIUS)).fixProps(3f, 0.1f, 100f)
                .fixGroup((short)(-index - 42)).fixFilter(CollCategory.PLAYER_FEET, (short)-1);
    }

	public void setupKeyboardControl() {
        movement = new KeyboardMovementListener();
        addListener((InputListener)movement);
        addListener(new AttackInputListener(this));
	}

	public void setupAiControl() {
		AiController ai = new AiController(this);
		addAction(ai);
		movement = ai;
	}

	public void setupController(MovementInputListener movement) {
		this.movement = movement;
	}

	@Override
	public void startAttack(AttackType type) {
		if (currentAttack == null && staggerTime <= 0) {
			currentAttack = type;
			punchPlayer.setTime(0);
			playingIntro = type.hasIntro();
			punchPlayer.setAnimation(playingIntro ? type.getIntro() : type.toString().toLowerCase());
			player = punchPlayer;
			fistBody.setActive(!type.isCharge());
			chargeTime = type.isCharge() ? 0f : 1f;
			if (type.isCharge()) {
				chargeReleased = false;
			}
			if (type == AttackType.SHOOT_HOLD) {
				Resource.SOUND.shotCharge.play();
			}
		}
	}

	@Override
	public void stopAttack(AttackType type) {
		if (playingIntro) {
			player = rootTweener;
			playingIntro = false;
			currentAttack = null;
		} else if (isCharging()) {
			chargeReleased = true;
			punchPlayer.setAnimation(currentAttack.getFollowup());
			punchPlayer.setTime(0);
			if (currentAttack == AttackType.SHOOT_HOLD) {
				Vector2 speed = Vector2.X.cpy().scl(orientation).rotateRad(fistBody.getAngle()).scl(10f);
				world.addActor(new BulletActor(world, fistBody.getPosition(), speed, currentAttack.getDamage() * (chargeTime + 1f), index));
				Resource.SOUND.fire.play();
			} else {
				fistBody.setActive(true);
			}
		}
	}

	@Override
	public void startBlock() {
		blocking = true;
	}

	@Override
	public void stopBlock() {
		blocking = false;
	}

	public boolean isCharging() {
		return currentAttack != null && currentAttack.isCharge() && !chargeReleased;
	}

	@Override
	protected void doAct(float delta) {
		processContacts();
		if (isCharging()) {
			chargeTime += delta;
		}
		if (movement != null) {
			move(delta);
		}
		animate(delta);
		if (getX() > GameRules.LEVEL_WIDTH || getX() + getWidth() < 0) {
			health = 0;
			kill();
		}
		if (staggerTime > 0) {
			staggerTime -= delta;
		}
		super.doAct(delta);
	}

	private void animate(float delta) {
		if (staggerTime > 0) {
     		walkTweener.getSecondPlayer().setAnimation("stagger");
		} else if (touchingFloor()) {
     		walkTweener.getSecondPlayer().setAnimation("walk");
     	} else {
     		walkTweener.getSecondPlayer().setAnimation("fly");
     	}

		if (moving || !touchingFloor() || staggerTime > 0) {
     		tweenTo = 1;
     	} else {
     		tweenTo = -1;
     	}
		tweenToRoot = blocking ? 1 : -1;
     	if (orientation != player.flippedX()) {
     		player.flipX();
     	}
     	walkTweener.setWeight(MathUtils.clamp(walkTweener.getWeight() + tweenTo*4*delta, 0, 1));
		rootTweener.setWeight(MathUtils.clamp(rootTweener.getWeight() + tweenToRoot*1000*delta, 0, 1));

        shotLight.setActive(currentAttack == AttackType.SHOOT_HOLD && isCharging());
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
	     		if (blocking) {
	     			mult = 0.5f;
	     		}
	     		if (staggerTime > 0) {
	     			mult = 0.2f;
	     		}
	     		body.setAngularVelocity(-mv.x*VELOCITY*mult);
	     	} else {
	     		body.applyForceToCenter(mv.x * AIR_CONTROL, 0f, true);
	     	}
     	}

     	if (movement.pollJump() && touchingFloor() && !isCharging()) {
     		addImpulse(0f, JUMP_IMPULSE);
     		//Resource.jump.play();
     	}
    }

    private void addImpulse(float impulseX, float impulseY) {
 		body.applyLinearImpulse(impulseX, impulseY, body.getPosition().x, body.getPosition().y, true);
 		floorContacts.clear();
    }

    public PlayerActor flip() {
    	orientation = -orientation;
    	return this;
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
			AttackType at = currentAttack;
			Timer.post(new Task() {
				@Override
				public void run() {
					fistBody.setActive(false);  //only one hit
					((HitListener)other.getUserData()).onHit(PlayerActor.this, at, new Vector2(currentAttack.getDamage() * chargeTime * orientation, 0), ct);
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

	@Override
	public void onHit(WorldActor cause, AttackType type, Vector2 force, Vector2 contactPoint) {
		float height = hitbox.getLocalPoint(contactPoint).y / HITBOX_HEIGHT;

		if (blocking && Math.signum(orientation) != Math.signum(force.x)) {
			boolean bullet = cause instanceof BulletActor;
			addImpulse(force.x * getHitScale() * (bullet ? 0.4f : 0.1f), HIT_IMPULSE);
			health -= force.len() * (bullet ? 0.75f : 0.1f);
			if (cause instanceof PlayerActor) {
				((PlayerActor) cause).stagger();
				Resource.SOUND.block.play();
			} else {
				Resource.SOUND.shotHitWall.play();
			}
		} else {
			if (cause instanceof BulletActor) {
				Resource.SOUND.shotHit.play();
			} else {
				Resource.SOUND.hit[(int)(Math.random()*Resource.SOUND.hit.length)].play();
			}
			if (isCharging()) {
				currentAttack = null;
				player = rootTweener;
				playingIntro = false;
				stagger();
			}
			addImpulse(force.x * getHitScale() * (type == AttackType.CHARGE ? 3 : 1), HIT_IMPULSE);
			health -= force.len();
			world.postEvent(new ParticleEvent(contactPoint.x, contactPoint.y, Resource.GFX.blood));
			if (regenerate != null) {
				regenerate.done();
			}
			regenerate = task.wait(GameRules.REGEN_DELAY);
			regenerate.thenEvery(1f/GameRules.REGEN_PER_SEC, Void -> {
				if (health < 0) {
					health = 0;
				} else if (health < GameRules.PLAYER_HEATH) {
					health += 1;
				}
			});
		}
	}

	public void stagger() {
		staggerTime = GameRules.STAGGER_TIME;
	}

	private float getHitScale() {
		return Interpolation.linear.apply(0.4f, 0.05f, MathUtils.clamp(health/GameRules.PLAYER_HEATH, 0, 1));
	}

	public float getHealth() {
		return health;
	}

	public float getOrientation() {
		return orientation;
	}

	public AttackType getCurrentAttack() {
		return currentAttack;
	}

	@Override
	protected void doRemove() {
		world.box2dWorld.destroyJoint(hitboxJoint);
		world.box2dWorld.destroyBody(hitbox); hitbox = null;
		world.box2dWorld.destroyBody(fistBody); fistBody = null;
		super.doRemove();
	}
}
