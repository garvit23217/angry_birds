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

public class FinishedScreen extends ScreenAdapter {
    private final Core game;
    private Texture backgroundImage;
    private Stage stage;

    public FinishedScreen(Core game) {
        this.game = game;
    }

    public void show() {
        backgroundImage = new Texture("FinishedScreen.png");
        stage = new Stage(game.getViewport());

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        Gdx.input.setInputProcessor(stage);

        Texture nextlevel = new Texture("NextLevel.png");
        Texture menu = new Texture("menu2.png");

        TextureRegionDrawable nextlevelDrawable = new TextureRegionDrawable(new TextureRegion(nextlevel));
        nextlevelDrawable.setMinWidth(600);
        nextlevelDrawable.setMinHeight(600);

        TextureRegionDrawable menuDrawable = new TextureRegionDrawable(new TextureRegion(menu));
        menuDrawable.setMinWidth(600);
        menuDrawable.setMinHeight(600);

        ImageButton nextlevelButton = new ImageButton(nextlevelDrawable);
        ImageButton menuButton = new ImageButton(menuDrawable);

        nextlevelButton.setPosition(820, 140);
        nextlevelButton.setSize(300, 300);
        nextlevelButton.getImageCell().minSize(200, 200);

        menuButton.setPosition(475, 140);
        menuButton.setSize(300, 300);
        menuButton.getImageCell().minSize(200, 200);


        nextlevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                game.setScreen(game.newlevel1screen());
            }
        });

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.levelChooseScreen);
            }
        });

        stage.addActor(nextlevelButton);
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