package com.baglogic.angrybirds;

import com.badlogic.gdx.physics.box2d.World;

public class Rock extends Material {
    public Rock(World world, float x, float y) {
        super(world, x, y, "materials/rock/rock_square.png", 2.5f, 0.6f, 0.1f, 100.0f);
    }
}
