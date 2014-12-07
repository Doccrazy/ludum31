package de.doccrazy.ld31.resources;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;

import de.doccrazy.shared.core.ResourcesBase;

public class GfxResources extends ResourcesBase {
	public Sprite dummy = atlas.createSprite("dummy");
    public Texture texBrick = texture("brick.png");
    public Sprite bullet = atlas.createSprite("kamehameha");

    public GfxResources() {
        super("game.atlas");
        texBrick.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        texBrick.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    }
}
