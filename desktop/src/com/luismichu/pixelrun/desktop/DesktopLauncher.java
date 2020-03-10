package com.luismichu.pixelrun.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.luismichu.pixelrun.DatabaseDesktop;
import com.luismichu.pixelrun.PixelRun;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.vSyncEnabled = true; // Setting to false disables vertical sync
		//config.foregroundFPS = 120; // Setting to 0 disables foreground fps throttling
		config.width = 365;
		config.height = 650;
		//config.fullscreen = true;
		new LwjglApplication(new PixelRun(new DatabaseDesktop()), config);
	}
}
