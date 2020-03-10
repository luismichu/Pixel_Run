package com.luismichu.pixelrun;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class PixelRun extends Game {
    public static Database db;

    public PixelRun(Database db){
        PixelRun.db = db;
    }

    @Override
    public void create() {
        setScreen(new MainMenu(this, null, true));
    }
}