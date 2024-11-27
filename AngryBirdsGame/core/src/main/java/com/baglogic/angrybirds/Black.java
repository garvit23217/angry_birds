package com.baglogic.angrybirds;

import com.badlogic.gdx.physics.box2d.World;

public class Black extends Bird {
    public Black(World world, float x, float y, float radius) {
        super(world, x, y, radius, "black.png", 150);
    }
}
