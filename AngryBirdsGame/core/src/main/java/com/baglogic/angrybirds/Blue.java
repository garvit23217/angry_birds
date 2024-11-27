package com.baglogic.angrybirds;

import com.badlogic.gdx.physics.box2d.World;

public class Blue extends Bird {
    public Blue(World world, float x, float y, float radius) {
        super(world, x, y, radius, "blue.png", 50);
    }
}
