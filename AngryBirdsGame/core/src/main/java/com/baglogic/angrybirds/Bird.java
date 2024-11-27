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

public abstract class Bird extends Actor {
    public static final float WORLD_TO_BOX = 0.01f;
    public static final float BOX_TO_WORLD = 100f;

    private float hitpoints;
    private Texture birdTexture;
    private Body physicsBody;
    private int width;
    private int height;
    private float radius;
    private boolean isSelected;
    private World world;


    public Bird(World world, float x, float y, float radius, String texturePath, float hitpoints) {
        this.world = world;
        this.radius = radius; // Set the radius
        this.width = (int) (radius * 2 * BOX_TO_WORLD);
        this.height = (int) (radius * 2 * BOX_TO_WORLD);
        this.hitpoints = hitpoints;

        birdTexture = new Texture(texturePath);

        // Create physics body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x * WORLD_TO_BOX, y * WORLD_TO_BOX);

        physicsBody = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius * WORLD_TO_BOX);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.2f;

        physicsBody.createFixture(fixtureDef);
        shape.dispose();

        setSize(width, height);
        setPosition(x - width / 2, y - height / 2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Vector2 position = physicsBody.getPosition();
        batch.draw(birdTexture,
                position.x * BOX_TO_WORLD - width / 2,
                position.y * BOX_TO_WORLD - height / 2,
                width, height);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        Vector2 position = physicsBody.getPosition();
        setPosition(position.x * BOX_TO_WORLD - width / 2, position.y * BOX_TO_WORLD - height / 2);
    }

    public void launch(float velocityX, float velocityY) {
        // Set the linear velocity of the bird's physics body directly
        physicsBody.setLinearVelocity(
                new Vector2(velocityX * WORLD_TO_BOX, velocityY * WORLD_TO_BOX)
        );
    }


    public void reduceHitpoints(float damage) {
        hitpoints -= damage;
        if (hitpoints <= 0) {
            remove();
            world.destroyBody(physicsBody);
        }
    }


    private void markForRemoval() {
        if (getStage() != null) {
            getStage().getRoot().removeActor(this);
        }
    }

    public float getHitpoints() {
        return hitpoints;
    }

    public void dispose() {
        birdTexture.dispose();
    }

    // Circular touch detection
    public boolean isTouched(float touchX, float touchY) {
        // Calculate the center of the bird
        float centerX = getX() + getWidth() / 2;
        float centerY = getY() + getHeight() / 2;

        // Check if the touch point is within the circle
        float distance = Vector2.dst(centerX, centerY, touchX, touchY);
        return distance <= radius * BOX_TO_WORLD;
    }

    public Body getPhysicsBody() {
        return physicsBody;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;

        if (selected) {
            setColor(1, 1, 1, 1);
        }
        else {
            setColor(1, 1, 1, 0.5f);
        }
    }
}
