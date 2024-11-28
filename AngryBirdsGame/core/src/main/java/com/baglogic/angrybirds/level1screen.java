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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class level1screen extends ScreenAdapter {
    private static final float SCALING = 0.01f;
    private static final float BOX_TO_WORLD = 100f;

    private static final float GROUND_HEIGHT = 90f;
    private static final float DESTRUCTION_DELAY = 0.1f;

    private Core game;
    private Texture background;
    private Texture slingShot;
    private Stage stage;

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera box2DCamera;
    private InputMultiplexer inputMultiplexer;
    private final Array<Body> bodiesToDestroy = new Array<>();
    private final Array<Actor> actorsToRemove = new Array<>();

    private Bird[] birds;
    private int currentBirdIndex = 0;
    private Bird currentBird;
    private boolean isDragging = false;

    private Pig pig1, pig2, pig3;
    private Rock rockSquare1, rockSquare2, rockSquare3, rockSquare4;

    private Body groundBody;
    private boolean isAtLaunchPosition = false;
    private float destructionTimer = 0f;

    private static class CollisionEvent {
        Actor actorA;
        Actor actorB;
        float impulse;

        CollisionEvent(Actor actorA, Actor actorB, float impulse) {
            this.actorA = actorA;
            this.actorB = actorB;
            this.impulse = impulse;
        }
    }
    private final Array<CollisionEvent> collisionEvents = new Array<>();

    public level1screen(Core game) {
        this.game = game;

        world = new World(new Vector2(0, -4.9f), true);
        debugRenderer = new Box2DDebugRenderer();

        createGroundBody();

        box2DCamera = new OrthographicCamera(Gdx.graphics.getWidth() * SCALING,
                Gdx.graphics.getHeight() * SCALING);

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
                    queueCollisionHandling((Actor) userDataA, (Actor) userDataB, maxImpulse);
                }
            }
        });
    }

    private void queueCollisionHandling(Actor actorA, Actor actorB, float impulse) {
        collisionEvents.add(new CollisionEvent(actorA, actorB, impulse));
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

        pig1 = new Pig(world, 1400, GROUND_HEIGHT + 100, 0.375f);
        pig2 = new Pig(world, 1500, GROUND_HEIGHT + 1000, 0.625f);

        stage.addActor(pig1);
        stage.addActor(pig2);
        //stage.addActor(pig3);

        rockSquare1 = new Rock(world, 1300, GROUND_HEIGHT + 100);
        rockSquare2 = new Rock(world, 1400, GROUND_HEIGHT + 500);
        rockSquare3 = new Rock(world, 1500, GROUND_HEIGHT + 100);
        rockSquare4 = new Rock(world, 1500, GROUND_HEIGHT + 500);

        stage.addActor(rockSquare1);
        stage.addActor(rockSquare2);
        stage.addActor(rockSquare3);
        stage.addActor(rockSquare4);

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

    private void processQueuedCollisions() {
        for (CollisionEvent event : collisionEvents) {
            handleCollision(event.actorA, event.actorB, event.impulse);
        }
        collisionEvents.clear();
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(delta, 6, 2);

        processQueuedCollisions();

        destructionTimer += delta;
        if (destructionTimer >= DESTRUCTION_DELAY) {
            destroyQueuedBodiesAndActors();
            destructionTimer = 0f;
        }

        game.getBatch().begin();
        game.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.getBatch().draw(slingShot, 150, 100, 150, 150);
        game.getBatch().end();

        handleBirdInput();
        stage.act(delta);
        stage.draw();
    }

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

    private void destroyQueuedBodiesAndActors() {
        for (Body body : bodiesToDestroy) {
            if (body != null) {
                world.destroyBody(body);
            }
        }
        bodiesToDestroy.clear();

        for (Actor actor : actorsToRemove) {
            if (actor.getStage() != null) {
                actor.remove();
            }
        }
        actorsToRemove.clear();
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
        handleCollision(actorA, actorB, 0);
    }

    private void handleCollision(Actor actorA, Actor actorB, float impulse) {
        float destructionThreshold = 15.0f;
        float impulseScale = 2.0f;

        impulse *= impulseScale;

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
        bird.reduceHitpoints(impulse * 0.2f);
        if (rock != null && impulse > threshold) {
            rock.destroy();
            queueBodyForDestruction(rock.getPhysicsBody());
        }

        if (bird.getHitpoints() <= 0) {
            bird.remove();
            queueBodyForDestruction(bird.getPhysicsBody());
        }
    }

    private void handlePigRockCollision(Pig pig, Rock rock, float impulse, float threshold) {
        if (rock != null && impulse > threshold) {
            rock.destroy();
            queueBodyForDestruction(rock.getPhysicsBody());
        }
        pig.reduceHitpoints(impulse * 0.1f);

        if (pig.getHitpoints() <= 0) {
            pig.remove();
            queueBodyForDestruction(pig.getPhysicsBody());
        }
    }

    public void applyDamage(Bird bird, Pig pig, float impulse) {
        float damageMultiplier = 0.5f;

        if (bird != null) {
            bird.reduceHitpoints(impulse * damageMultiplier);
            if (bird.getHitpoints() <= 0) {
                actorsToRemove.add(bird); // Queue for removal
                queueBodyForDestruction(bird.getPhysicsBody()); // Queue body destruction
            }
        }

        if (pig != null) {
            pig.reduceHitpoints(impulse * damageMultiplier);
            if (pig.getHitpoints() <= 0) {
                actorsToRemove.add(pig); // Queue for removal
                queueBodyForDestruction(pig.getPhysicsBody()); // Queue body destruction
            }
        }
    }


    public void queueBodyForDestruction(Body body) {
        bodiesToDestroy.add(body);
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
