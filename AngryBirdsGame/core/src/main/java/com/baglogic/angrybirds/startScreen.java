package com.baglogic.angrybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class startScreen extends ScreenAdapter {
    private final Core game;
    private Texture backgroundImage;
    private Stage stage;
    private Texture playTexture;
    private Texture quitTexture;

    public startScreen(Core game) {
        this.game = game;
    }

    @Override
    public void show() {
        backgroundImage = new Texture("bg7.png");
        stage = new Stage(game.getViewport());

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        Gdx.input.setInputProcessor(stage);

        playTexture = new Texture("play.png");
        quitTexture = new Texture("quit.png");

        ImageButton playButton = new ImageButton(new TextureRegionDrawable(playTexture));
        ImageButton quitButton = new ImageButton(new TextureRegionDrawable(quitTexture));

        playButton.setPosition(7f * screenWidth / 8f, 7f * screenHeight / 8f);
        playButton.setSize(screenWidth / 4f, screenHeight / 4f);

        quitButton.setPosition(7f * screenWidth / 8f, 7f * screenHeight / 8f - screenHeight / 2f);
        quitButton.setSize(screenWidth / 4f, screenHeight / 4f);

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.levelChooseScreen);
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        stage.addActor(playButton);
        stage.addActor(quitButton);
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

    @Override
    public void dispose() {
        backgroundImage.dispose();
        playTexture.dispose();
        quitTexture.dispose();
        stage.dispose();
    }
}
