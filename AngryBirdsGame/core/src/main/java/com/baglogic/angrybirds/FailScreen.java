package com.baglogic.angrybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class FailScreen extends ScreenAdapter {
    private final Core game;
    private Texture backgroundImage;
    private Stage stage;

    public FailScreen(Core game) {
        this.game = game;
    }

    public void show() {
        backgroundImage = new Texture("FailScreen.png");
        stage = new Stage(game.getViewport());

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        Gdx.input.setInputProcessor(stage);

        Texture restart = new Texture("restart.png");
        Texture menu = new Texture("menu2.png");

        TextureRegionDrawable restartDrawable = new TextureRegionDrawable(new TextureRegion(restart));
        restartDrawable.setMinWidth(600);
        restartDrawable.setMinHeight(600);

        TextureRegionDrawable menuDrawable = new TextureRegionDrawable(new TextureRegion(menu));
        menuDrawable.setMinWidth(600);
        menuDrawable.setMinHeight(600);

        ImageButton restartButton = new ImageButton(restartDrawable);
        ImageButton menuButton = new ImageButton(menuDrawable);

        restartButton.setPosition(820, 140);
        restartButton.setSize(300, 300);
        restartButton.getImageCell().minSize(200, 200);

        menuButton.setPosition(475, 140);
        menuButton.setSize(300, 300);
        menuButton.getImageCell().minSize(200, 200);


        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.newlevel1screen());
            }
        });

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.levelChooseScreen);
            }
        });

        stage.addActor(restartButton);
        stage.addActor(menuButton);
    }
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
        stage.dispose();
    }
}