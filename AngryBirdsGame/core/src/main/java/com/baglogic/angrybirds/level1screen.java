package com.baglogic.angrybirds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class level1screen extends ScreenAdapter {
    private Core game;
    private Texture background;
    private Stage stage;

    public level1screen(Core game) {
        this.game = game;
    }

    @Override
    public void show() {
        background = new Texture("bg8.png");
        stage = new Stage(game.getViewport());

        //to be completed
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getBatch().begin();
        game.getBatch().draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.getBatch().end();
    }
}
