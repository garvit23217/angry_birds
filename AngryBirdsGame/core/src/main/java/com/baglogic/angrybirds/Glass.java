package com.baglogic.angrybirds;

import com.badlogic.gdx.physics.box2d.World;

public class Glass extends Material {
    public Glass(World world, float x, float y) {
        super(world, x, y, "materials/glass/glass_square.png", 2.0f, 0.5f, 0.1f, 80.0f);
    }
}
