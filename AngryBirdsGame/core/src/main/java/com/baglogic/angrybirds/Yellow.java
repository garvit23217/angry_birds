package com.baglogic.angrybirds;

import com.badlogic.gdx.physics.box2d.World;

public class Yellow extends Bird {
    public Yellow(World world, float x, float y, float radius) {
        super(world, x, y, radius, "yellow.png", 75);
    }
}
