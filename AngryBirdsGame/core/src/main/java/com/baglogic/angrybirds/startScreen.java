package com.baglogic.angrybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class startScreen extends ScreenAdapter {
    private final Core game;
    private Texture backgroundImage;
    private Stage stage;
    private Skin skin;
    private TextButton startButton;
    private TextButton exitButton;

    public startScreen(Core game) {
        this.game = game;
    }

    @Override
    public void show() {
        backgroundImage = new Texture("bg5.png");
        stage = new Stage(game.getViewport());

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));

        startButton = new TextButton("Start", skin);
        startButton.setPosition(7f*screenWidth/8f,7f*screenHeight/8f);
        startButton.setSize(screenWidth/4f, screenHeight/4f);

        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.levelChooseScreen);
            }
        });

        exitButton = new TextButton("Exit", skin);
        exitButton.setPosition(7f*screenWidth/8f, 7f*screenHeight/8f - screenHeight/2f);
        exitButton.setSize(screenWidth/4f, screenHeight/4f);

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        stage.addActor(startButton);
        stage.addActor(exitButton);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getBatch().begin();
        game.getBatch().draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.getBatch().end();

        stage.act(delta);
        stage.draw();
    }
}
