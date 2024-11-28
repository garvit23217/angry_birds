package com.baglogic.angrybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class level1screen extends ScreenAdapter {
    private static final float SCALING = 0.01f;
    private static final float BOX_TO_WORLD = 100f;

    private static final float GROUND_HEIGHT = 90f;

    private Core game;
    private Texture background;
    private Texture slingShot;
    private Stage stage;

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera box2DCamera;
    private InputMultiplexer inputMultiplexer;

    private Bird[] birds;
    private int currentBirdIndex = 0;
    private Bird currentBird;
    private boolean isDragging = false;

    private Pig pig1, pig2, pig3;
    private Rock rockSquare1, rockSquare2, rockSquare3, rockSquare4;

    private Body groundBody;

    public level1screen(Core game) {
        this.game = game;

        // Initialize Box2D world
        world = new World(new Vector2(0, -4.9f), true);
        debugRenderer = new Box2DDebugRenderer();

        // Create ground body
        createGroundBody();

        // Setup Box2D camera
        box2DCamera = new OrthographicCamera(Gdx.graphics.getWidth() * SCALING,
                Gdx.graphics.getHeight() * SCALING);

        // Update the world contact listener in the constructor
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();

                Object userDataA = bodyA.getUserData();
                Object userDataB = bodyB.getUserData();

                if (userDataA instanceof Actor && userDataB instanceof Actor) {
                    Gdx.app.log("Collision Begin", userDataA + " collided with " + userDataB);
                }
            }

            @Override
            public void endContact(Contact contact) {
                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();

                Object userDataA = bodyA.getUserData();
                Object userDataB = bodyB.getUserData();

                if (userDataA instanceof Actor && userDataB instanceof Actor) {
                    Gdx.app.log("Collision End", userDataA + " stopped colliding with " + userDataB);
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
                // Optional: Modify collision response if necessary
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();

                Object userDataA = bodyA.getUserData();
                Object userDataB = bodyB.getUserData();

                float maxImpulse = 0;
                for (float normalImpulse : impulse.getNormalImpulses()) {
                    maxImpulse = Math.max(maxImpulse, normalImpulse);
                }

                if (userDataA instanceof Actor && userDataB instanceof Actor) {
                    Gdx.app.log("Collision Impulse", "Impulse: " + maxImpulse);
                    handleCollision((Actor) userDataA, (Actor) userDataB, maxImpulse);
                }
            }
        });
    }

    private void createGroundBody() {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        groundBodyDef.position.set(Gdx.graphics.getWidth() / 2f * SCALING, GROUND_HEIGHT * SCALING);

        groundBody = world.createBody(groundBodyDef);

        PolygonShape groundShape = new PolygonShape();
        float extendedWidth = Gdx.graphics.getWidth() * SCALING * 6f;
        groundShape.setAsBox(extendedWidth / 2, 1f);

        FixtureDef groundFixtureDef = new FixtureDef();
        groundFixtureDef.shape = groundShape;
        groundFixtureDef.friction = 0.5f;
        groundFixtureDef.restitution = 0.0f;

        groundBody.createFixture(groundFixtureDef);
        groundShape.dispose();
    }

    @Override
    public void show() {
        background = new Texture("bg8.png");
        slingShot = new Texture("slingshot.png");

        stage = new Stage(game.getViewport());

        birds = new Bird[] {
                new Blue(world, 125, GROUND_HEIGHT + 100, 0.375f),
                new Red(world, 175, GROUND_HEIGHT + 100, 0.375f),
                new Yellow(world, 225, GROUND_HEIGHT + 100, 0.375f),
                new Black(world, 275, GROUND_HEIGHT + 100, 0.375f)
        };

        for (Bird bird : birds) {
            stage.addActor(bird);
        }

        // Add pigs and rocks as actors in the stage
        pig1 = new Pig(world, 1400, GROUND_HEIGHT + 100, 0.375f);
        pig2 = new Pig(world, 1500, GROUND_HEIGHT + 1000, 0.625f);
        pig3 = new Pig(world, 1300, GROUND_HEIGHT + 1000, 0.75f);

        stage.addActor(pig1);
        stage.addActor(pig2);
        stage.addActor(pig3);

        rockSquare1 = new Rock(world, 1300, GROUND_HEIGHT + 100);
        rockSquare2 = new Rock(world, 1400, GROUND_HEIGHT + 500);
        rockSquare3 = new Rock(world, 1500, GROUND_HEIGHT + 100);
        rockSquare4 = new Rock(world, 1500, GROUND_HEIGHT + 500);

        stage.addActor(rockSquare1);
        stage.addActor(rockSquare2);
        stage.addActor(rockSquare3);
        stage.addActor(rockSquare4);

        // Add pause button
        Texture pause = new Texture("pause.png");
        ImageButton pauseButton = new ImageButton(new TextureRegionDrawable(pause));
        pauseButton.setPosition(50, 1000);
        pauseButton.setSize(150, 150);

        pauseButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(game.levelChooseScreen);
            }
        });

        stage.addActor(pauseButton);

        // Initialize InputMultiplexer
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector2 touchPos = stage.screenToStageCoordinates(new Vector2(screenX, screenY));
                selectBird(touchPos);
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                Vector2 touchPos = stage.screenToStageCoordinates(new Vector2(screenX, screenY));
                if (currentBird != null && currentBird.isSelected()) {
                    dragAndLaunchBird(touchPos);
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (isDragging && currentBird != null) {
                    releaseBird();
                }
                return true;
            }
        });

        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(delta, 6, 2);

        game.getBatch().begin();
        game.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.getBatch().draw(slingShot, 150, 100, 150, 150);
        game.getBatch().end();

        handleBirdInput();

        stage.act(delta);
        stage.draw();

        // debug lines
        //debugRenderer.render(world, box2DCamera.combined);
    }

    private boolean isAtLaunchPosition = false;
    private void handleBirdInput() {
        Vector2 touchPos = stage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));

        if (Gdx.input.isTouched()) {
            if (!isDragging) {
                selectBird(touchPos);
            }

            if (currentBird != null && currentBird.isSelected()) {
                dragAndLaunchBird(touchPos);
            }
        } else if (isDragging && currentBird != null) {
            releaseBird();
        }
    }

    private void selectBird(Vector2 touchPos) {
        for (Bird bird : birds) {
            if (isNearCurrentBird(touchPos, bird)) {
                setBirdToLaunchPosition(bird);
                return;
            }
        }
    }

    private void dragAndLaunchBird(Vector2 touchPos) {
        if (currentBird != null && currentBird.isSelected() && isNearLaunchPosition(touchPos)) {
            isDragging = true;

            currentBird.setPosition(
                    touchPos.x - currentBird.getWidth() / 2,
                    touchPos.y - currentBird.getHeight() / 2
            );
            updatePhysicsBodyTransform(currentBird);
        }
    }

    private void releaseBird() {
        if (currentBird != null && currentBird.isSelected() && isDragging) {
            float anchorX = 390;
            float anchorY = GROUND_HEIGHT + 170;

            float dragDistance = Vector2.dst(currentBird.getX(), currentBird.getY(), anchorX, anchorY);

            float velocityScale = 10.0f;
            float velocityX = -(currentBird.getX() - anchorX) * velocityScale;
            float velocityY = -(currentBird.getY() - anchorY) * velocityScale;

            currentBird.launch(velocityX, velocityY);

            isDragging = false;
            resetLaunchState();
        }
    }

    private void setBirdToLaunchPosition(Bird bird) {
        if (currentBird != null) {
            currentBird.setSelected(false);

            float replacementX = bird.getX();
            float replacementY = GROUND_HEIGHT + 100;
            currentBird.setPosition(replacementX, replacementY);
            updatePhysicsBodyTransform(currentBird);
        }

        currentBird = bird;
        currentBird.setSelected(true);
        currentBird.setPosition(390, GROUND_HEIGHT + 170);
        updatePhysicsBodyTransform(currentBird);
        isAtLaunchPosition = true;
    }

    private void updatePhysicsBodyTransform(Bird bird) {
        Body body = bird.getPhysicsBody();
        float centerX = (bird.getX() + bird.getWidth() / 2) * Bird.WORLD_TO_BOX;
        float centerY = (bird.getY() + bird.getHeight() / 2) * Bird.WORLD_TO_BOX;
        body.setTransform(centerX, centerY, body.getAngle());
    }


    private boolean isBirdAtLaunchPosition(Bird bird) {
        float launchX = 150;
        float launchY = GROUND_HEIGHT + 100;
        return Vector2.dst(bird.getX(), bird.getY(), launchX, launchY) < 10;
    }

    private boolean isSignificantlyDragged(Bird bird) {
        float launchX = 150;
        float launchY = GROUND_HEIGHT + 100;
        return Vector2.dst(bird.getX(), bird.getY(), launchX, launchY) > 10;
    }

    private void resetLaunchState() {
        currentBird = null;
        isAtLaunchPosition = false;
    }

    private boolean isNearCurrentBird(Vector2 touchPos, Bird bird) {
        Vector2 birdPos = new Vector2(bird.getX() + bird.getWidth() / 2, bird.getY() + bird.getHeight() / 2);
        return touchPos.dst(birdPos) < 50;
    }

    private boolean isNearLaunchPosition(Vector2 touchPos) {
        float launchX = 390;
        float launchY = GROUND_HEIGHT + 170;
        return Vector2.dst(touchPos.x, touchPos.y, launchX, launchY) < 50;
    }

    private void moveToNextBird() {
        if (currentBirdIndex < birds.length - 1) {
            currentBirdIndex++;
            currentBird = birds[currentBirdIndex];
            currentBird.setPosition(150, GROUND_HEIGHT + 20);
        } else {
            System.out.println("All birds are launched!");
        }
    }

    private void handleCollision(Actor actorA, Actor actorB) {
        // Default impulse value for simple collisions
        handleCollision(actorA, actorB, 0);
    }

    private void handleCollision(Actor actorA, Actor actorB, float impulse) {
        float destructionThreshold = 30.0f; // Impulse threshold for destruction

        if (actorA instanceof Bird && actorB instanceof Pig) {
            applyDamage((Bird) actorA, (Pig) actorB, impulse);
        } else if (actorB instanceof Bird && actorA instanceof Pig) {
            applyDamage((Bird) actorB, (Pig) actorA, impulse);
        } else if (actorA instanceof Bird && actorB instanceof Rock) {
            handleBirdRockCollision((Bird) actorA, (Rock) actorB, impulse, destructionThreshold);
        } else if (actorB instanceof Bird && actorA instanceof Rock) {
            handleBirdRockCollision((Bird) actorB, (Rock) actorA, impulse, destructionThreshold);
        } else if (actorA instanceof Pig && actorB instanceof Rock) {
            handlePigRockCollision((Pig) actorA, (Rock) actorB, impulse, destructionThreshold);
        } else if (actorB instanceof Pig && actorA instanceof Rock) {
            handlePigRockCollision((Pig) actorB, (Rock) actorA, impulse, destructionThreshold);
        }
    }

    private void handleBirdRockCollision(Bird bird, Rock rock, float impulse, float threshold) {
        bird.reduceHitpoints(impulse * 0.2f); // Scale bird damage based on impulse
        if (rock != null && impulse > threshold) {
            rock.destroy(); // Destroy rock if the impulse exceeds the threshold
        }

        if (bird.getHitpoints() <= 0) {
            bird.remove();
            world.destroyBody(bird.getPhysicsBody());
        }
    }

    private void handlePigRockCollision(Pig pig, Rock rock, float impulse, float threshold) {
        if (rock != null && impulse > threshold) {
            rock.destroy();
        }
        pig.reduceHitpoints(impulse * 0.1f);

        if (pig.getHitpoints() <= 0) {
            pig.remove();
            world.destroyBody(pig.getPhysicsBody());
        }
    }

    private void applyDamage(Bird bird, Pig pig, float impulse) {
        float damageMultiplier = 0.2f;

        if (bird != null) {
            bird.reduceHitpoints(impulse * damageMultiplier);
            if (bird.getHitpoints() <= 0) {
                bird.remove();
                world.destroyBody(bird.getPhysicsBody());
            }
        }

        if (pig != null) {
            pig.reduceHitpoints(impulse * damageMultiplier);
            if (pig.getHitpoints() <= 0) {
                pig.remove();
                world.destroyBody(pig.getPhysicsBody());
            }
        }
    }


    @Override
    public void dispose() {
        background.dispose();
        slingShot.dispose();
        stage.dispose();
        world.dispose();
        debugRenderer.dispose();
    }
}
