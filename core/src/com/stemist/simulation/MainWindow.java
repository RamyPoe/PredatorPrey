package com.stemist.simulation;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.stemist.simulation.Game.GameScreen;
import com.stemist.simulation.Menu.MenuScreen;
import com.stemist.simulation.Transition.Transition;

public class MainWindow extends Game {
	
	// Determining which screen to draw
	public static enum SCREEN {
		MENU,
		GAME
	}
	private static SCREEN cur_screen = SCREEN.MENU;
	private static SCREEN new_screen = SCREEN.MENU;

	// For drawing
	public SpriteBatch batch;

	// For transition elements
	public Transition transition;

	// Screen Constants
	public static final int V_WIDTH = 1200;
	public static final int V_HEIGHT = 800;

	// Game Constraints
	public static final int GAME_MAX_RIGHT 	=  2000;
	public static final int GAME_MAX_LEFT 	= -2000;
	public static final int GAME_MAX_TOP 	=  1400;
	public static final int GAME_MAX_BOTTOM = -1400;

	// Game constants
	public static final int ENTITY_RADIUS = 30;
	public static final int ENTITY_MAX_VEL = 700;
	public static final int ENTITY_MAX_ANGLE_VEL = 500;
	public static final int ENTITY_MAX_ENERGY = 100;
	public static final float VEL_ENERGY_DEPLETION = 20;
	public static final int ENTITY_NUM_RAYS = 20;
	public static final float CHANCE_INITIAL_PREY = 0.7f;

	public static final int MAX_PREDATORS = 25;
	public static final int MAX_PREY = 200;

	// Predator
	public static final float IDLE_ENERGY_DEPLETION = 3;
	public static final float KILL_ENERGY_GAIN = 20;
	public static final float KILL_SPLIT_GAIN = 50;
	public static final float SPLIT_ENERGY_THRESHOLD = 100;
	public static final float SPLIT_ENERGY_DEPLETION = 2;
	public static final int DIGESTION_TIME_MS = 500;

	// Prey
	public static final float IDLE_ENERGY_GAIN = 4;
	public static final int SPLIT_TIME_MS = 3000;

	
	// Called when class is first created
	@Override
	public void create () {
		// Used when drawing anything
		batch = new SpriteBatch();

		// Set window size
		Gdx.graphics.setWindowedMode(1200, 800);

		// Setup transition
		transition = new Transition(60);
		transition.setGame(this);

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

		// See if screen needs to be changed
		if (cur_screen != new_screen) {
			cur_screen = new_screen;
			this.getScreen().dispose();

			switch(cur_screen) {
				case MENU:
					setScreen(new MenuScreen(this));
					break;

				case GAME:
					setScreen(new GameScreen(this));
					break;
			}
		}

		// Draw the current screen
		super.render();
	}
	
	// Setting a new screen
	public void setGameScreen(SCREEN s) {
		MainWindow.new_screen = s;
	}

	// So we can create textures on demand
	public static Texture createTexture(int width, int height, Color color) {
		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fillRectangle(0, 0, width, height);
		Texture texture = new Texture(pixmap);
		pixmap.dispose();
		return texture;
	}

	// Get current time
	public static long getTimeMs() {
		return System.nanoTime() / 1_000_000;
	}

	// When screen is destroyed (used for garbage collection)
	@Override
	public void dispose () {
		batch.dispose();
		super.dispose();
	}
}