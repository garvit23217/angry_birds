package com.baglogic.angrybirds;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Red extends Actor {
    private float hitpoints;
    private Texture redBird;
    private Vector2 position;
    private int height = 75;
    private int width = 75;

    public Red() {
        hitpoints = 100;
        redBird = new Texture("red.png");
        position = new Vector2(75, 125);
        setSize(width, height);
        setPosition(position.x, position.y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(redBird, getX(), getY(), width, height);
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
