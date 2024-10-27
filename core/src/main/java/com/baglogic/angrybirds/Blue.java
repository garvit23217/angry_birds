package com.baglogic.angrybirds;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Blue extends Actor {
    private float hitpoints;
    private Texture blueBird;
    private Vector2 position;
    private int height = 75;
    private int width = 75;

    public Blue(){
        hitpoints = 50;
        blueBird = new Texture("blue.png");
        position = new Vector2(125, 125);
        setSize(width, height);
        setPosition(position.x, position.y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(blueBird, getX(), getY(), width, height);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public float getHitpoints() {
        return hitpoints;
    }

    public void updateHitpoints(float hitpoints) {
        this.hitpoints = hitpoints;
    }
}
