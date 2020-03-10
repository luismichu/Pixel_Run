package com.luismichu.pixelrun;

import com.badlogic.gdx.Game;

public class PixelRun extends Game {
    @Override
    public void create() {
        setScreen(new MainMenu(this, null, true));
    }
}