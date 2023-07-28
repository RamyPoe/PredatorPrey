package com.stemist.simulation.Configuration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.stemist.simulation.MainWindow;


public class ConfigScreen implements Screen {

    public int reproductiveRate = 1; 

    // Detect button press
    private enum BUTTON {
        BTN_NONE,
        BTN_REPRODUCTIVE_INCREASE,
        BTN_REPRODUCTIVE_DECREASE,
        BTN_SAVE,
        PENDING
    }
    private BUTTON btnPressed = BUTTON.BTN_NONE;

    // To change screens
    MainWindow main;

    // For button interaction
    ConfigHud hud;

    // Constructor
    public ConfigScreen(MainWindow main) {
        // For changing screens
        this.main = main;

        // Add hud
        hud = new ConfigHud(main.batch);

    }

    @Override
    public void show() {
    }

    // For processing
    private void update(float delta) {
        // Skip button check if transitioning
        if (main.transition.active) {
            return;
        }
        // Leave page by right clicking. 
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            main.transition.fadeOut(MainWindow.SCREEN.MENU);
        }


        // Increase/decrease reproductive rate. 
        if (btnPressed == BUTTON.BTN_REPRODUCTIVE_INCREASE) {
            btnPressed = BUTTON.BTN_NONE;
        }
        if (btnPressed == BUTTON.BTN_REPRODUCTIVE_DECREASE) {
            btnPressed = BUTTON.BTN_NONE; 
        }

    }

    @Override
    public void render(float delta) {

        // Seperate logic from rendering
        update(delta);

        // Draw hud
        hud.draw();

        // Transition screen fade in
        if (!main.transition.haveFadedIn && !main.transition.active)
            main.transition.fadeIn();
        
        // Draw transition
        main.transition.draw(delta);


    }

    @Override
    public void resize(int width, int height) {
        hud.viewport.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        hud.dispose();
    }
    
}
