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

public class level1screen extends ScreenAdapter {
    private Core game;
    private Texture background;
    private Texture slingShot;
    private Texture black;
    private Texture blue;
    private Texture red;
    private Texture yellow;
    private Stage stage;

    public level1screen(Core game) {
        this.game = game;
    }

    @Override
    public void show() {
        background = new Texture("bg8.png");
        stage = new Stage(game.getViewport());

        slingShot = new Texture("slingshot.png");
        black = new Texture("black.png");
        blue = new Texture("blue.png");
        red = new Texture("red.png");
        yellow = new Texture("yellow.png");
        Texture pause = new Texture("pause.png");

        ImageButton pauseButton = new ImageButton(new TextureRegionDrawable(pause));
        pauseButton.setPosition(50, 1000);
        pauseButton.setSize(150, 150);
        pauseButton.getImageCell().minSize(150, 150);

        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //to be completed
            }
        });
        //to be completed
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getBatch().begin();
        game.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.getBatch().draw(slingShot,200, 100, 150, 150);
        game.getBatch().draw(black, 175, 100, 50, 50);
        game.getBatch().draw(blue, 125, 100, 50, 50);
        game.getBatch().draw(red, 75, 100, 50 , 50);
        game.getBatch().draw(yellow, 25, 100, 50, 50);
        game.getBatch().end();

        stage.act(delta);
        stage.draw();
    }
}
