package com.baglogic.angrybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class level1screen extends ScreenAdapter {
    private static final float scaling = 0.01f;
    private static final float BOX_TO_WORLD = 100f;

    // Ground reference point
    private static final float ground = 90f;
    //private static final float BIRD_START_HEIGHT = 1000f; // Higher starting point for birds

    private Core game;
    private Texture background;
    private Texture slingShot;
    private Stage stage;

    // Box2D physics elements
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera box2DCamera;

    // Birds, pigs, and materials
    private Blue blueBird;
    private Red redBird;
    private Yellow yellowBird;
    private Black blackBird;
    private Pig pig1, pig2, pig3;

    // Materials
    private Wood woodStick1, woodStick2, woodStick3;
    private Rock rockSquare1, rockSquare2, rockSquare3;
    private Glass glassSlab1, glassSlab2, glassSlab3;

    // Ground body to prevent falling through
    private Body groundBody;

    public level1screen(Core game) {
        this.game = game;

        // Initialize Box2D world with gravity
        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();

        // Create ground body to stop objects from falling
        createGroundBody();

        // Setup Box2D camera
        box2DCamera = new OrthographicCamera(Gdx.graphics.getWidth() * scaling,
                Gdx.graphics.getHeight() * scaling);
    }

    private void createGroundBody() {
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;

        // Center the ground based on screen width
        groundBodyDef.position.set(Gdx.graphics.getWidth() / 2f * scaling, ground * scaling);

        groundBody = world.createBody(groundBodyDef);

        PolygonShape groundShape = new PolygonShape();

        // Extend the ground beyond the screen
        float extendedWidth = Gdx.graphics.getWidth() * scaling * 6f; // 50% wider than the screen
        groundShape.setAsBox(extendedWidth / 2, 1f); // Half-width and thickness

        FixtureDef groundFixtureDef = new FixtureDef();
        groundFixtureDef.shape = groundShape;
        groundFixtureDef.friction = 0.5f;
        groundFixtureDef.restitution = 0.0f; // Prevent bounce

        groundBody.createFixture(groundFixtureDef);
        groundShape.dispose();
    }


    @Override
    public void show() {
        background = new Texture("bg8.png");
        slingShot = new Texture("slingshot.png");

        // Create stage
        stage = new Stage(game.getViewport());
        Gdx.input.setInputProcessor(stage);

        // Create birds (positioned much higher)
        // Create birds (positioned relative to ground, with specific radii)
        blueBird = new Blue(world, 125, ground + 1000, 0.375f);
        redBird = new Red(world, 175, ground + 1000, 0.375f);
        yellowBird = new Yellow(world, 225, ground + 1000, 0.375f);
        blackBird = new Black(world, 275, ground + 1000, 0.375f);

        // Create pigs (positioned above ground)
        pig1 = new Pig(world, 600, ground + 1000, 0.375f);
        pig2 = new Pig(world, 700, ground + 1000, 0.625f);
        pig3 = new Pig(world, 800, ground + 1000, 0.75f);

        rockSquare1 = new Rock(world, 1400, ground + 100);
        rockSquare2 = new Rock(world, 1400, ground + 500);

        // Create rock structures
        /*rockSlab1 = new Rock(world, 1400, ground + 100, true);  // Left vertical slab
        rockSlab2 = new Rock(world, 1600, ground + 100, true);
        rockSlab3 = new Rock(world, 1500, ground + 250, false);  // Horizontal slab

        // Add rock slabs to stage
        stage.addActor(rockSlab1);
        stage.addActor(rockSlab2);
        stage.addActor(rockSlab3);*/

        stage.addActor(rockSquare1);
        stage.addActor(rockSquare2);

        // Create glass structures
        //glassSlab1 = new Glass(world, 750, ground + 1000, false);
        //glassSlab2 = new Glass(world, 1050, ground + 10, true);
        //glassSlab3 = new Glass(world, 1200, ground + 500, false);

        // Add birds to stage
        stage.addActor(blueBird);
        stage.addActor(redBird);
        stage.addActor(yellowBird);
        stage.addActor(blackBird);

        // Add pigs to stage
        stage.addActor(pig1);
        stage.addActor(pig2);
        stage.addActor(pig3);

        // Add rock to stage
        /*stage.addActor(rockSlab1);
        stage.addActor(rockSlab2);
        stage.addActor(rockSlab3);*/

        // Add glass to stage
        //stage.addActor(glassSlab1);
        //stage.addActor(glassSlab2);
        //stage.addActor(glassSlab3);

        // Create pause button
        Texture pause = new Texture("pause.png");
        ImageButton pauseButton = new ImageButton(new TextureRegionDrawable(pause));
        pauseButton.setPosition(50, 1000);
        pauseButton.setSize(150, 150);
        pauseButton.getImageCell().minSize(150, 150);

        pauseButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(game.levelChooseScreen);
            }
        });

        stage.addActor(pauseButton);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Step physics world
        world.step(delta, 6, 2);

        // Render background
        game.getBatch().begin();
        game.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.getBatch().draw(slingShot, 150, 100, 150, 150);
        game.getBatch().end();

        // Render stage
        stage.act(delta);
        stage.draw();

        // Optional: Render Box2D debug lines
        // debugRenderer.render(world, box2DCamera.combined);
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