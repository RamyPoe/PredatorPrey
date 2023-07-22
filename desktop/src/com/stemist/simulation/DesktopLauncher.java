package com.stemist.simulation;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.stemist.simulation.MainWindow;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		
		// Config object
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		// Set fps
		config.setForegroundFPS(60);

		// Set window title
		config.setTitle("Simulation");

		// Start program starting with main class using config
		new Lwjgl3Application(new MainWindow(), config);
	}
}
