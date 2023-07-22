package com.stemist.simulation;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.stemist.simulation.Menu.MenuScreen;

public class MainWindow extends Game {
	
	// For drawing
	public SpriteBatch batch;

	// Screen Constants
	public static final int V_WIDTH = 1200;
	public static final int V_HEIGHT = 800;

	// Game Constraints
	public static final int GAME_MAX_RIGHT 	=  500;
	public static final int GAME_MAX_LEFT 	= -500;
	public static final int GAME_MAX_TOP 	=  700;
	public static final int GAME_MAX_BOTTOM = -700;

	// Draw position to be calculated
	int x, y;
	
	// Called when class is first created
	@Override
	public void create () {
		// Used when drawing anything
		batch = new SpriteBatch();

		// Set window size
		Gdx.graphics.setWindowedMode(1200, 800);

		// Start with menu
		setScreen(
			new MenuScreen(this)
		);
	}

	// Called at framerate
	@Override
	public void render () {
		// Clear screen
		ScreenUtils.clear(1, 0, 0, 1);

		// Draw the current screen
		super.render();
	}
	
	// When screen is destroyed (used for garbage collection)
	@Override
	public void dispose () {
		batch.dispose();
		super.dispose();
	}
}
