package com.baglogic.angrybirds;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class Material extends Actor {
    protected static final float WORLD_TO_BOX = 0.01f;
    protected static final float BOX_TO_WORLD = 100f;

    protected Texture texture;
    protected Body physicsBody;
    protected float width, height;
    protected World world;
    protected float hitpoints;

    public Material(World world, float x, float y, String texturePath, float density, float friction, float restitution, float initialHitpoints) {
        this.world = world;
        this.hitpoints = initialHitpoints;
        this.texture = new Texture(texturePath);

        // Calculate dimensions from texture size
        this.width = texture.getWidth() * WORLD_TO_BOX;
        this.height = texture.getHeight() * WORLD_TO_BOX;

        // Create physics body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x * WORLD_TO_BOX, y * WORLD_TO_BOX);

        this.physicsBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;

        physicsBody.createFixture(fixtureDef);
        shape.dispose();
        physicsBody.setUserData(this);

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

    public void destroy() {
        getStage().getRoot().removeActor(this);
        world.destroyBody(physicsBody);
    }

    public Body getPhysicsBody() {
        return physicsBody;
    }

    public boolean isReadyToDestroy() {
        return hitpoints <= 0;
    }

    public void reduceHitpoints(float damage) {
        hitpoints -= damage;
    }

    public float getHitpoints() {
        return hitpoints;
    }

    public void dispose() {
        texture.dispose();
    }
}
