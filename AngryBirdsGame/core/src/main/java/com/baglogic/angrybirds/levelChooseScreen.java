package com.baglogic.angrybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

public class levelChooseScreen extends ScreenAdapter {
    private final Core game;
    private Texture backgroundImage;
    private Texture boxTest;

    public levelChooseScreen(Core game) {
        this.game = game;
    }

    @Override
    public void show() {
        backgroundImage = new Texture("bg4.png");
        boxTest = new Texture("box.png");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Get screen dimensions
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        game.getBatch().begin();
        game.getBatch().draw(backgroundImage, 0, 0, screenWidth, screenHeight); // Draw the background image to cover the entire screen
        game.getBatch().draw(boxTest, 100, 100, 50, 50);
        game.getBatch().end();
    }

    @Override
    public void dispose() {
        backgroundImage.dispose();
    }
}
