package com.baglogic.angrybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

public class loadingScreen extends ScreenAdapter {
    private final Core game;
    private Texture backgroundImage;
    private Texture logoImage;
    private float time;

    public loadingScreen(Core game) {
        this.game = game;
    }

    @Override
    public void show() {
        backgroundImage = new Texture("whitebg.png");
        logoImage = new Texture("logo.png");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float x = Gdx.graphics.getWidth();
        float y = Gdx.graphics.getHeight();

        game.getBatch().begin();
        game.getBatch().draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Draw background image
        game.getBatch().draw(logoImage, x/4, y/4, x/2, y/2); // Draw logo image
        game.getBatch().end();

        // Accumulate the elapsed time
        time += delta; // Add the time since the last frame

        // Check if 3 seconds have passed
        if (time > 3) {
            game.setScreen(game.startScreen);
        }
    }

    @Override
    public void dispose() {
        backgroundImage.dispose();
        logoImage.dispose();
    }
}
