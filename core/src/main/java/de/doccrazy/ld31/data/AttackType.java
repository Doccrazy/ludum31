package de.doccrazy.ld31.data;

public enum AttackType {
	PUNCH(100f),
	CHARGE(150f, "charge_punch", 0.5f, null),
	SHOOT_HOLD(500f, "shoot_release", 0f, "shoot_charge");

	private float damage;
	private String followup;
	private float chargeMove;
	private String intro;

	AttackType(float damage) {
		this.damage = damage;
	}

	AttackType(float damage, String followup, float chargeMove, String intro) {
		this.damage = damage;
		this.followup = followup;
		this.chargeMove = chargeMove;
		this.intro = intro;
	}

	public float getDamage() {
		return damage;
	}

	public String getIntro() {
		return intro;
	}

	public String getFollowup() {
		return followup;
	}

	public float getChargeMove() {
		return chargeMove;
	}

	public boolean isCharge() {
		return followup != null;
	}

	public boolean hasIntro() {
		return intro != null;
	}
}
