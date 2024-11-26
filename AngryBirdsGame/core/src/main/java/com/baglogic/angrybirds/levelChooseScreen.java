package com.baglogic.angrybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class levelChooseScreen extends ScreenAdapter {
    private final Core game;
    private Texture backgroundImage;
    private Stage stage;

    public levelChooseScreen(Core game) {
        this.game = game;
    }

    @Override
    public void show() {
        backgroundImage = new Texture("bg6.png");
        stage = new Stage(game.getViewport());

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        Gdx.input.setInputProcessor(stage);

        //System.out.println("Width: " + screenWidth);
        //System.out.println("Height : " + screenHeight);

        Texture level1Texture = new Texture("lvl1.png");
        Texture level2Texture = new Texture("lvl2.png");
        Texture level3Texture = new Texture("lvl3.png");
        Texture backTexture = new Texture("back.png");

        ImageButton level1Button = new ImageButton(new TextureRegionDrawable(level1Texture));
        ImageButton level2Button = new ImageButton(new TextureRegionDrawable(level2Texture));
        ImageButton level3Button = new ImageButton(new TextureRegionDrawable(level3Texture));
        ImageButton backButton = new ImageButton(new TextureRegionDrawable(backTexture));

        level1Button.setPosition(400, 500);
        level1Button.setSize(200, 200);
        level1Button.getImageCell().minSize(200, 200);

        level2Button.setPosition(850, 500);
        level2Button.setSize(200, 200);
        level2Button.getImageCell().minSize(200, 200);

        level3Button.setPosition(1300, 500);
        level3Button.setSize(200, 200);
        level3Button.getImageCell().minSize(200, 200);

        backButton.setPosition(50, 50);
        backButton.setSize(200, 200);
        backButton.getImageCell().minSize(200, 200);

        level1Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.level1screen);
            }
        });

        level2Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //game.setScreen((game.level2screen));
            }
        });

        level3Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //game.setScreen((game.level3screen));
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.startScreen);
            }
        });

        stage.addActor(level1Button);
        stage.addActor(level2Button);
        stage.addActor(level3Button);
        stage.addActor(backButton);
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
        stage.dispose();
    }
}
