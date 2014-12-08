package de.doccrazy.ld31.game.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import de.doccrazy.ld31.core.Resource;

public class AnnouncerLabel extends Label {
	private UiRoot root;

    private float t = Float.MAX_VALUE;
    private List<Announcement> text = new ArrayList<>();
    private Announcement current;

	private int skip;

	public AnnouncerLabel(UiRoot root) {
		super("", new LabelStyle(Resource.FONT.retroBig, Color.WHITE));
		this.root = root;

		setAlignment(Align.center);
		setY(300);
		setHeight(400);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		setWidth(getStage().getWidth());
		if (current == null && !text.isEmpty()) {
			current = text.remove(0);
			setText(current.text);
			t = 0;
		}
		if (current == null) {
			return;
		}
		t += delta;
		if (t < 1f) {
			setFontScale(Interpolation.bounceOut.apply(6f, 1f, t));
			setColor(new Color(1, 1, 1, Interpolation.exp5Out.apply(0f, 1f, t)));
		} else if (t < current.time + 1f) {
			setFontScale(1f);
			setColor(Color.WHITE);
			if (skip > 0) {
				t = current.time + 1f;
				skip--;
			}
		} else if (t < current.time + 1.25f) {
			float d = MathUtils.clamp(4*(t-current.time-1), 0, 1);
			setFontScale(Interpolation.exp5.apply(1f, 6f, d));
			setColor(new Color(1, 1, 1, Interpolation.exp5Out.apply(1f, 0f, d)));
		} else {
			current = null;
			setColor(new Color(0,0,0,0));
		}
	}

	public void skip() {
		skip = text.size() + (current != null ? 1 : 0);
	}

	public void add(String str, float t) {
		text.add(new Announcement(str, t));
	}
}

class Announcement {
	String text;
	float time;

	public Announcement(String text, float time) {
		this.text = text;
		this.time = time;
	}
}