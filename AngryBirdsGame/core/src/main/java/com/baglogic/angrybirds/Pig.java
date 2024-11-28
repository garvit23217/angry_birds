package com.baglogic.angrybirds;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Pig extends Actor {
    private static final float WORLD_TO_BOX = 0.01f;
    private static final float BOX_TO_WORLD = 100f;

    protected World world;
    protected Body body;
    private Texture texture;
    private boolean isDestroyed = false;
    private float hitpoints;
    private final int hitpointsFactor = 3;

    public Pig(World world, float x, float y, float scale) {
        this.world = world;
        this.hitpoints = 200 * scale;
        texture = new Texture("pig.png");

        // Create body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x * WORLD_TO_BOX, y * WORLD_TO_BOX);

        // Create body in the world
        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius((texture.getWidth() * scale / 2) * WORLD_TO_BOX);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.8f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.4f;

        body.createFixture(fixtureDef);
        shape.dispose();
        body.setUserData(this);

        setSize(texture.getWidth() * scale, texture.getHeight() * scale);
    }

    public void destroy() {
        isDestroyed = true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!isDestroyed) {
            float worldX = body.getPosition().x * BOX_TO_WORLD - getWidth() / 2;
            float worldY = body.getPosition().y * BOX_TO_WORLD - getHeight() / 2;
            batch.draw(texture, worldX, worldY, getWidth(), getHeight());
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!isDestroyed) {
            setPosition(
                    body.getPosition().x * BOX_TO_WORLD - getWidth() / 2,
                    body.getPosition().y * BOX_TO_WORLD - getHeight() / 2
            );
        }
    }

    public void reduceHitpoints(float damage) {
        hitpoints -= damage;
        if (hitpoints <= 0) {
            remove();
            world.destroyBody(body);
        }
    }


    public float getHitpoints() {
        return hitpoints;
    }

    public Body getPhysicsBody() {
        return body;
    }

    public void dispose() {
        texture.dispose();
    }
}