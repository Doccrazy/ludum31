package de.doccrazy.ld31.data;

public enum GamepadActions {
	PUNCH("Punch"),
	STRONG_PUNCH("Strong punch"),
	CHARGED_SHOT("Charged shot"),
	JUMP("Jump"),
	BLOCK("Block");

	private String name;

	private GamepadActions(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
