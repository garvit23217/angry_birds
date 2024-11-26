package com.baglogic.angrybirds;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Blue extends Actor {
    private static final float WORLD_TO_BOX = 0.01f;
    private static final float BOX_TO_WORLD = 100f;

    private float hitpoints;
    private Texture blueBird;
    private Body physicsBody;
    private int width;
    private int height;

    public Blue(World world, float x, float y, float radius) {
        this.width = (int) (radius * 2 * BOX_TO_WORLD);
        this.height = (int) (radius * 2 * BOX_TO_WORLD);
        this.hitpoints = 50; // Blue bird's specific hit points

        blueBird = new Texture("blue.png");

        // Create physics body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x * WORLD_TO_BOX, y * WORLD_TO_BOX);

        physicsBody = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius * WORLD_TO_BOX);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.3f;

        physicsBody.createFixture(fixtureDef);
        shape.dispose();

        setSize(width, height);
        setPosition(x - width / 2, y - height / 2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Vector2 position = physicsBody.getPosition();
        batch.draw(blueBird,
                position.x * BOX_TO_WORLD - width / 2,
                position.y * BOX_TO_WORLD - height / 2,
                width, height);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Update actor position to match physics body
        Vector2 position = physicsBody.getPosition();
        setPosition(position.x * BOX_TO_WORLD - width / 2, position.y * BOX_TO_WORLD - height / 2);
    }

    // Method to apply launch force
    public void launch(float forceX, float forceY) {
        physicsBody.applyLinearImpulse(
                new Vector2(forceX * WORLD_TO_BOX, forceY * WORLD_TO_BOX),
                physicsBody.getWorldCenter(),
                true
        );
    }

    public void reduceHitpoints(float damage) {
        hitpoints -= damage;
        if (hitpoints <= 0) {
            // Bird is destroyed
            markForRemoval();
        }
    }

    private void markForRemoval() {
        // Remove from stage
        if (getStage() != null) {
            getStage().getRoot().removeActor(this);
        }
    }

    public float getHitpoints() {
        return hitpoints;
    }

    //@Override
    public void dispose() {
        blueBird.dispose();
    }
}