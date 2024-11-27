package com.baglogic.angrybirds;

import com.badlogic.gdx.physics.box2d.World;

public class Red extends Bird {
    public Red(World world, float x, float y, float radius) {
        super(world, x, y, radius, "red.png", 100);
    }
}
