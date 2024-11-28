package com.baglogic.angrybirds;

import com.badlogic.gdx.physics.box2d.World;

public class Wood extends Material {
    public Wood(World world, float x, float y) {
        super(world, x, y, "materials/wood/wood_square.png", 0.8f, 0.6f, 0.2f, 120.0f);
    }
}