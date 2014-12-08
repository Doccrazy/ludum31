package de.doccrazy.ld31.resources;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.Sprite;

import de.doccrazy.shared.core.ResourcesBase;

public class GfxResources extends ResourcesBase {
	public Sprite dummy = atlas.createSprite("dummy");
	public Texture[] texBrick = new Texture[] {brick(0), brick(1), brick(2), brick(3)};
    public Sprite bullet = atlas.createSprite("kamehameha");
    public ParticleEffectPool blood = particle("blood.p", 0.01f);
    public ParticleEffectPool dust = particle("dust.p", 0.01f);
    public ParticleEffectPool snow = particle("snow.p", 0.01f);
    public Texture healthbarFull = texture("healthbar-full.png");
    public Texture healthbarEmpty = texture("healthbar-empty.png");
    public Texture healthbarEmptyRed = texture("healthbar-empty-red.png");
    public Texture backgroundHigh = texture("background.png");
    public Texture backgroundLow = texture("background-low.png");

    public GfxResources() {
        super("game.atlas");
    }

    private Texture brick(int lvl) {
    	Texture tex = texture("brick-dmg" + lvl + ".png");
    	tex.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
    	tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    	return tex;
    }
}
