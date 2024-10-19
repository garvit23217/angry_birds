package com.baglogic.angrybirds;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Core extends Game {
    private SpriteBatch batch;
    private FitViewport viewport;
    private Texture image;
    public Screen loadingScreen;
    public Screen levelChooseScreen;
    public Screen startScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(1920, 1200);
        loadingScreen = new loadingScreen(this);
        levelChooseScreen = new levelChooseScreen(this);
        startScreen = new startScreen(this);

        setScreen(loadingScreen);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();

        if (loadingScreen != null) {
            loadingScreen.dispose();
        }
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public FitViewport getViewport() {
        return viewport;
    }
}
