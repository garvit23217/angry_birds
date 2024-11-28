package com.baglogic.angrybirds;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
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
    public Screen level1screen;
    public Screen level2screen;
    public Screen level3screen;
    public Screen failScreen;
    public Screen pauseScreen;
    public Screen finishedScreen;
    private Music backgroundMusic;
    private ScreenAdapter currentScreen;
    private ScreenAdapter currentLevel;
    private boolean completelevel1 = false;
    private boolean completelevel2 = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(1920, 1200);

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("theme.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.75f);
        backgroundMusic.play();
        backgroundMusic.pause();

        loadingScreen = new loadingScreen(this);
        levelChooseScreen = new levelChooseScreen(this);
        startScreen = new startScreen(this);
        //level1screen = new level1screen(this);
        //level2screen = new level2screen(this);
        //level3screen = new level3screen(this);
        setScreen(loadingScreen);
    }

    @Override
    public void render() {
        super.render();
    }

    public Screen newlevel1screen() {
        level1screen = new level1screen(this);
        return level1screen;
    }

    public Screen newlevel2screen() {
        level2screen = new level2screen(this);
        return level2screen;
    }

    public Screen newlevel3screen() {
        level3screen = new level3screen(this);
        return level3screen;
    }

    public Screen newfailscreen(int level) {
        failScreen = new FailScreen(this, level);
        return failScreen;
    }

    public Screen newpausescreen() {
        pauseScreen = new PauseScreen(this);
        return pauseScreen;
    }

    public Screen newfinishedscreen(int level, int score) {
        finishedScreen = new FinishedScreen(this, level, score);
        return finishedScreen;
    }

    public void setCurrentLevel(int level, ScreenAdapter levelScreen) {
        if (level == 1) {
            level1screen = levelScreen;
        } else if (level == 2) {
            level2screen = levelScreen;
        } else if (level == 3) {
            level3screen = levelScreen;
        }
        currentLevel = levelScreen;
    }

    public ScreenAdapter getCurrentLevel() {
        return currentLevel;
    }


    public ScreenAdapter getCurrentScreen() {
        return currentScreen;
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

    public boolean isBackgroundMusicPlaying() {
        return backgroundMusic.isPlaying();
    }

    public void pauseBackgroundMusic() {
        backgroundMusic.pause();
    }

    public void setCompletelevel1() {
        completelevel1 = true;
    }

    public void setCompletelevel2() {
        completelevel2 = true;
    }

    public boolean getCompleteLevel1() {
        return completelevel1;
    }

    public boolean getCompleteLevel2() {
        return completelevel2;
    }

    public void playBackgroundMusic() {
        backgroundMusic.play();
    }
}
