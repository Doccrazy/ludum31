package de.doccrazy.ld31.resources;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.brashmonkey.spriter.Entity;
import de.doccrazy.shared.spriter.ResourcesBaseSpriter;

public class SpriterResources extends ResourcesBaseSpriter {
    public Entity guy = entity("guy");

    public SpriterResources(TextureAtlas atlas) {
        super("Game.scml", atlas);
    }
}
