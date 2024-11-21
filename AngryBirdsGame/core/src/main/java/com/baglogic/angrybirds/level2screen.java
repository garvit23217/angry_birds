package com.baglogic.angrybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class level2screen extends ScreenAdapter {
    private Core game;
    private Texture background;
    private Stage stage;
    private Texture slingShot;

    public level2screen(Core game) {
        this.game = game;
    }

    @Override
    public void show()
    {
        background = new Texture("bg8.png");
        stage = new Stage(game.getViewport());

        Gdx.input.setInputProcessor(stage);

        slingShot = new Texture("slingshot.png");
        Texture pause = new Texture("pause.png");
        Texture backTexture = new Texture("back.png");

        ImageButton pauseButton = new ImageButton(new TextureRegionDrawable(pause));
        pauseButton.setPosition(50, 1000);
        pauseButton.setSize(150, 150);
        pauseButton.getImageCell().minSize(150, 150);

        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.levelChooseScreen);
            }
        });

        Blue blueBird = new Blue();
        Red redBird = new Red();
        Yellow yellowBird = new Yellow();
        Black blackBird = new Black();
        Pig pig1 = new Pig(75,75,1500,200);
        Pig pig2 = new Pig(125,125,1200,200);
        Pig pig3 = new Pig(150,150,1350,200);

        stage.addActor(blueBird);
        stage.addActor(redBird);
        stage.addActor(yellowBird);
        stage.addActor(blackBird);
        stage.addActor(pauseButton);
        stage.addActor(pig1);
        stage.addActor(pig2);
        stage.addActor(pig3);

    }
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getBatch().begin();
        game.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.getBatch().draw(slingShot,200, 100, 150, 150);
        //game.getBatch().draw(black, 175, 100, 50, 50);
        //game.getBatch().draw(blue, 125, 100, 50, 50);
        //game.getBatch().draw(red, 75, 100, 50 , 50);
        //game.getBatch().draw(yellow, 25, 100, 50, 50);
        game.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        background.dispose();
        stage.dispose();
    }
}

