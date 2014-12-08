package de.doccrazy.ld31.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class ControllerLabel extends Label {
	private UiRoot root;
	private float t;

	public ControllerLabel(UiRoot root) {
		super("Xg", new LabelStyle(new BitmapFont(), new Color(1f, 0.2f, 0.2f, 1f)));
		this.root = root;

        setAlignment(Align.center);
        setFontScale(1.5f);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		setWidth(getStage().getWidth());
		setY(300);

		t += delta;
		if (t < 8f && root.getPadInput().isSupported() && !root.getPadInput().isInit()) {
			setText("Press any controller button to start P2 setup");
			setVisible(t % 1f < 0.5f);
		} else if (root.getPadInput().isInit() && !root.getPadInput().isConfigured()) {
			setText("Press button for action '" + root.getPadInput().getNextAction() + "'");
			t = -999;
		} else if (root.getPadInput().isConfigured() && t < 1.5f) {
			if (t < 0) {
				t = 0;
			}
			setText("Configuration finished");
			setVisible(t % 0.4f < 0.2f);
		} else {
			setVisible(false);
		}
	}
}
