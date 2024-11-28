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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class level3screen extends ScreenAdapter {
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
    private Rock rockSquare1, rockSquare2, rockSquare3, rockSquare4, rockSquare5, rockSquare6;
    private Wood woodSquare4, woodSquare5;
    private Glass glassSquare6;

    private Body groundBody;
    private boolean isAtLaunchPosition = false;
    private float destructionTimer = 0f;
    private final Map<Actor, Integer> groundCollisionCounts = new HashMap<>();
    private final Set<Bird> launchedBirds = new HashSet<>();

    int pigs = 3;
    int unrealeasedBirds = 4;
    int rockDestroyed = 0;
    int score = 0;

    private boolean isLevelComplete = false;
    private boolean isLevelFailed = false;
    private float failTimer = 0f;

    private Label scoreLabel;
    private Skin skin;

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

    public level3screen(Core game) {
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

                if (userDataA instanceof Actor && "ground".equals(userDataB)) {
                    handleGroundCollision((Actor) userDataA);
                } else if (userDataB instanceof Actor && "ground".equals(userDataA)) {
                    handleGroundCollision((Actor) userDataB);
                } else {
                    resetGroundCollisionCount(userDataA);
                    resetGroundCollisionCount(userDataB);
                }

                if (userDataA instanceof Bird && userDataB instanceof Material) {
                    //System.out.println("PostSolve: Bird collided with Material");
                    handleBirdMaterialCollision((Bird) userDataA, (Material) userDataB, maxImpulse);
                } else if (userDataA instanceof Material && userDataB instanceof Bird) {
                    //System.out.println("PostSolve: Material collided with Bird");
                    handleBirdMaterialCollision((Bird) userDataB, (Material) userDataA, maxImpulse);
                } else if (userDataA instanceof Bird && userDataB instanceof Pig) {
                    //System.out.println("PostSolve: Bird collided with Pig");
                    handleBirdPigCollision((Bird) userDataA, (Pig) userDataB, maxImpulse);
                } else if (userDataA instanceof Pig && userDataB instanceof Bird) {
                    //System.out.println("PostSolve: Pig collided with Bird");
                    handleBirdPigCollision((Bird) userDataB, (Pig) userDataA, maxImpulse);
                }
            }
        });
    }

    private void handleBirdMaterialCollision(Bird bird, Material material, float impulse) {
        float birdDamageMultiplier = 20f;
        float materialDamageMultiplier = 400f;

        bird.reduceHitpoints(impulse * birdDamageMultiplier);
        material.reduceHitpoints(impulse * materialDamageMultiplier);

        if (bird.getHitpoints() <= 0) {
            score += 1000; // Add score for bird destruction
            actorsToRemove.add(bird);
            queueBodyForDestruction(bird.getPhysicsBody());
        }

        if (material.isReadyToDestroy()) {
            score += 10000; // Add score for material destruction
            actorsToRemove.add(material);
            queueBodyForDestruction(material.getPhysicsBody());
        }
    }

    private void queueCollisionHandling(Actor actorA, Actor actorB, float impulse) {
        //System.out.println("Queuing collision: " + actorA + " with " + actorB + ", impulse: " + impulse);
        collisionEvents.add(new level3screen.CollisionEvent(actorA, actorB, impulse));
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
        groundBody.setUserData("ground");
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

        pig1 = new Pig(world, 1300, GROUND_HEIGHT + 300, 0.375f);
        pig2 = new Pig(world, 1500, GROUND_HEIGHT + 300, 0.375f);
        pig3 = new Pig(world, 1400, GROUND_HEIGHT + 600, 0.750f);

        stage.addActor(pig1);
        stage.addActor(pig2);
        stage.addActor(pig3);

        rockSquare1 = new Rock(world, 1260, GROUND_HEIGHT + 100);
        rockSquare2 = new Rock(world, 1400, GROUND_HEIGHT + 100);
        rockSquare3 = new Rock(world, 1530, GROUND_HEIGHT + 100);
        woodSquare4 = new Wood(world, 1330, GROUND_HEIGHT + 200);
        woodSquare5 = new Wood(world, 1490, GROUND_HEIGHT + 200);
        glassSquare6 = new Glass(world, 1400, GROUND_HEIGHT + 400);

        stage.addActor(rockSquare1);
        stage.addActor(rockSquare2);
        stage.addActor(rockSquare3);
        stage.addActor(woodSquare4);
        stage.addActor(woodSquare5);
        stage.addActor(glassSquare6);

        skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        scoreLabel = new Label("Score: 0", skin);
        scoreLabel.setPosition(800, Gdx.graphics.getHeight() - 10);
        scoreLabel.setFontScale(4);

        stage.addActor(scoreLabel);

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
        for (level3screen.CollisionEvent event : collisionEvents) {
            handleCollision(event.actorA, event.actorB, event.impulse);
        }
        collisionEvents.clear();

        if (!world.isLocked()) {
            destroyQueuedBodiesAndActors();
        }
    }

    @Override
    public void render(float delta) {
        if (game.getScreen() != this) {
            return; // Pause updates when not active
        }

        if (!isLevelComplete && !isLevelFailed) {
            // Regular game logic
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
            scoreLabel.setText("Score: " + score);
            stage.act(delta);
            stage.draw();

            checkGameState(delta);
        } else if (isLevelComplete) {
            game.setScreen(game.newfinishedscreen(3, score));
        } else if (isLevelFailed) {
            game.setScreen(game.newfailscreen(3));
        }
    }


    private void checkGameState(float delta) {
        if (pigs <= 0) {
            isLevelComplete = true;
            //System.out.println("All pigs destroyed! Level passed.");
            return;
        }

        if (unrealeasedBirds == 0 && pigs > 0) {
            failTimer += delta;
            if (failTimer >= 10f) {
                if (pigs > 0) {
                    isLevelFailed = true;
                    //System.out.println("Level failed after waiting 10 seconds.");
                }
            }
        } else {
            failTimer = 0f; // Reset the timer if birds are still available
        }
    }


    private void handleGroundCollision(Actor actor) {
        if (actor instanceof Bird && !launchedBirds.contains(actor)) {
            //System.out.println("Skipping unlaunched bird: " + actor);
            return;
        }
        if (actor instanceof Rock) {
            return;
        }

        int count = groundCollisionCounts.getOrDefault(actor, 0) + 1;
        groundCollisionCounts.put(actor, count);

        //System.out.println("Ground collision count for " + actor + ": " + count);

        if (count >= 3) {
            //System.out.println(actor + " has collided with the ground 3 times. Queuing for removal.");
            actorsToRemove.add(actor);
            if (actor instanceof Bird) {
                queueBodyForDestruction(((Bird) actor).getPhysicsBody());
            } else if (actor instanceof Pig) {
                queueBodyForDestruction(((Pig) actor).getPhysicsBody());
            }
            groundCollisionCounts.remove(actor);
        }
    }

    private void resetGroundCollisionCount(Object userData) {
        if (userData instanceof Bird) {
            Bird bird = (Bird) userData;
            if (!launchedBirds.contains(bird)) {
                return;
            }
        }
        if (userData instanceof Rock) {
            return;
        }
        if (userData instanceof Actor) {
            Actor actor = (Actor) userData;
            if (groundCollisionCounts.containsKey(actor)) {
                groundCollisionCounts.remove(actor);
                //System.out.println("Reset ground collision count for " + actor);
            }
        }
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

            launchedBirds.add(currentBird);
            unrealeasedBirds--;
            //System.out.println("Bird added to launchedBirds: " + currentBird);

            isDragging = false;
            resetLaunchState();
        } else {
            //System.out.println("Bird not launched: " + currentBird);
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
                if (actor instanceof Pig) {
                    pigs--;
                    score += 1000;
                }
                if (actor instanceof Bird) {
                    score += 350;
                }
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
            //System.out.println("All birds are launched!");
        }
    }

    private void handleCollision(Actor actorA, Actor actorB) {
        handleCollision(actorA, actorB, 0);
    }

    private void handleCollision(Actor actorA, Actor actorB, float impulse) {
        float destructionThreshold = 7.5f;
        float impulseScale = 2.0f;

        impulse *= impulseScale;

        if (actorA instanceof Bird && actorB == groundBody.getUserData()) {
            //System.out.println("Actor A instance of: " + actorA.getClass().getSimpleName());
            //System.out.println("Actor B instance of: " + actorB.getClass().getSimpleName());
            handleBirdGroundCollision((Bird) actorA);
        } else if (actorB instanceof Bird && "ground".equals(groundBody.getUserData())) {
            //System.out.println("Actor A instance of: " + actorA.getClass().getSimpleName());
            //System.out.println("Actor B instance of: " + actorB.getClass().getSimpleName());
            handleBirdGroundCollision((Bird) actorB);
        } else if (actorA instanceof Bird && actorB instanceof Rock) {
            //System.out.println("Actor A instance of: " + actorA.getClass().getSimpleName());
            //System.out.println("Actor B instance of: " + actorB.getClass().getSimpleName());
            //System.out.println("Bird-Rock collision detected.");
            handleBirdRockCollision((Bird) actorA, (Rock) actorB, impulse, destructionThreshold);
        } else if (actorB instanceof Bird && actorA instanceof Rock) {
            //System.out.println("Actor A instance of: " + actorA.getClass().getSimpleName());
            //System.out.println("Actor B instance of: " + actorB.getClass().getSimpleName());
            //System.out.println("Bird-Rock collision detected.");
            handleBirdRockCollision((Bird) actorB, (Rock) actorA, impulse, destructionThreshold);
        } else if (actorA instanceof Pig && actorB instanceof Rock) {
            //System.out.println("Pig-Rock collision detected.");
            //handlePigRockCollision((Pig) actorA, (Rock) actorB, impulse, destructionThreshold);
            return;
        } else if (actorB instanceof Pig && actorA instanceof Rock) {
            //System.out.println("Pig-Rock collision detected.");
            //handlePigRockCollision((Pig) actorB, (Rock) actorA, impulse, destructionThreshold);
            return;
        } else if (actorA instanceof Bird && actorB instanceof Pig) {
            //System.out.println("Actor A instance of: " + actorA.getClass().getSimpleName());
            //System.out.println("Actor B instance of: " + actorB.getClass().getSimpleName());
            //System.out.println("Bird-Pig collision detected.");
            handleBirdPigCollision((Bird) actorA, (Pig) actorB, impulse);
        } else if (actorB instanceof Bird && actorA instanceof Pig) {
            //System.out.println("Actor A instance of: " + actorA.getClass().getSimpleName());
            //System.out.println("Actor B instance of: " + actorB.getClass().getSimpleName());
            //System.out.println("Bird-Pig collision detected.");
            handleBirdPigCollision((Bird) actorB, (Pig) actorA, impulse);
        }
    }

    private void handleBirdPigCollision(Bird bird, Pig pig, float impulse) {
        float birdDamageMultiplier = 30f;
        float pigDamageMultiplier = 1000f;

        bird.reduceHitpoints(impulse * birdDamageMultiplier);
        pig.reduceHitpoints(impulse * pigDamageMultiplier);

        if (bird.getHitpoints() <= 0) {
            //score += 1000;
            actorsToRemove.add(bird);
            queueBodyForDestruction(bird.getPhysicsBody());
        }

        if (pig.getHitpoints() <= 0) {
            //score += 1000;
            actorsToRemove.add(pig);
            queueBodyForDestruction(pig.getPhysicsBody());
        }
    }

    private void handleBirdGroundCollision(Bird bird) {
        Vector2 velocity = bird.getPhysicsBody().getLinearVelocity();
        float negligibleVelocityThreshold = 0.5f;

        if (Math.abs(velocity.y) < negligibleVelocityThreshold) {
            bird.remove();
            queueBodyForDestruction(bird.getPhysicsBody());
        }
    }

    private void handleBirdRockCollision(Bird bird, Rock rock, float impulse, float threshold) {
        //System.out.println("Handling Bird-Rock collision...");
        //System.out.println("Initial Rock hitpoints: " + rock.getHitpoints());
        //System.out.println("Initial Bird hitpoints: " + bird.getHitpoints());

        bird.reduceHitpoints(impulse * 20f);
        rock.reduceHitpoints(impulse * 300f);

        //System.out.println("Updated Rock hitpoints: " + rock.getHitpoints());
        //System.out.println("Updated Bird hitpoints: " + bird.getHitpoints());

        if (rock.isReadyToDestroy()) {
            //System.out.println("Rock destroyed.");
            actorsToRemove.add(rock);
            queueBodyForDestruction(rock.getPhysicsBody());
        }

        if (bird.getHitpoints() <= 0) {
            //System.out.println("Bird destroyed.");
            actorsToRemove.add(bird);
            queueBodyForDestruction(bird.getPhysicsBody());
        }
    }

    private void handlePigRockCollision(Pig pig, Rock rock, float impulse, float threshold) {
        //System.out.println("Handling Pig-Rock collision...");
        //System.out.println("Initial Rock hitpoints: " + rock.getHitpoints());
        //System.out.println("Initial Pig hitpoints: " + pig.getHitpoints());

        rock.reduceHitpoints(impulse * 0.1f);
        pig.reduceHitpoints(impulse * 0.1f);

        //System.out.println("Updated Rock hitpoints: " + rock.getHitpoints());
        //System.out.println("Updated Pig hitpoints: " + pig.getHitpoints());

        if (rock.isReadyToDestroy()) {
            //System.out.println("Rock destroyed.");
            actorsToRemove.add(rock);
            queueBodyForDestruction(rock.getPhysicsBody());
        }

        if (pig.getHitpoints() <= 0) {
            //System.out.println("Pig destroyed.");
            actorsToRemove.add(pig);
            queueBodyForDestruction(pig.getPhysicsBody());
        }
    }

    public void applyDamage(Bird bird, Pig pig, float impulse) {
        float damageMultiplier = 0.8f;

        if (bird != null) {
            bird.reduceHitpoints(impulse * damageMultiplier);
            if (bird.getHitpoints() <= 0) {
                actorsToRemove.add(bird);
                queueBodyForDestruction(bird.getPhysicsBody());
            }
        }

        if (pig != null) {
            pig.reduceHitpoints(impulse * damageMultiplier);
            if (pig.getHitpoints() <= 0) {
                actorsToRemove.add(pig);
                queueBodyForDestruction(pig.getPhysicsBody());
            }
        }
    }

    public void queueBodyForDestruction(Body body) {
        bodiesToDestroy.add(body);
        //System.out.println("Body queued for destruction: " + body);
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
