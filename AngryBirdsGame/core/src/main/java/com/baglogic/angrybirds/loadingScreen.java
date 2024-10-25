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
        logoImage = new Texture("logo2.png");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float x = Gdx.graphics.getWidth();
        float y = Gdx.graphics.getHeight();

        game.getBatch().begin();
        game.getBatch().draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.getBatch().draw(logoImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.getBatch().end();

        time += delta;

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
