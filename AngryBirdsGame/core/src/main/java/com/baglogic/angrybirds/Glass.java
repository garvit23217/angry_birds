package com.baglogic.angrybirds;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Glass extends Actor {
    private static final float WORLD_TO_BOX = 0.01f;
    private static final float BOX_TO_WORLD = 100f;

    private Texture texture;
    private Body physicsBody;
    private float width, height;

    public Glass(World world, float x, float y) {
        // Load square texture
        texture = new Texture("materials/glass/glass_square.png");

        // Calculate dimensions from texture size
        width = texture.getWidth() * WORLD_TO_BOX;
        height = texture.getHeight() * WORLD_TO_BOX;

        // Create physics body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x * WORLD_TO_BOX, y * WORLD_TO_BOX);

        physicsBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 2.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f;

        physicsBody.createFixture(fixtureDef);
        shape.dispose();

        setSize(width * BOX_TO_WORLD, height * BOX_TO_WORLD);
        setPosition(x - width * BOX_TO_WORLD / 2, y - height * BOX_TO_WORLD / 2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Vector2 position = physicsBody.getPosition();
        batch.draw(texture,
                position.x * BOX_TO_WORLD - width * BOX_TO_WORLD / 2,
                position.y * BOX_TO_WORLD - height * BOX_TO_WORLD / 2,
                width * BOX_TO_WORLD,
                height * BOX_TO_WORLD);
    }

    public void dispose() {
        texture.dispose();
    }
}