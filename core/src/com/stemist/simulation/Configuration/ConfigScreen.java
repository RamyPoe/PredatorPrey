package com.stemist.simulation.Configuration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.stemist.simulation.MainWindow;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


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
        hud = new ConfigHud(main.batch, reproductiveRate);

        // Add listener for the three buttons on the screen. 
        hud.reproductiveSpeedIncreaseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Change button pressed
                if (btnPressed != BUTTON.PENDING) { btnPressed = BUTTON.BTN_REPRODUCTIVE_INCREASE; }
            }
        });
    
        hud.reproductiveSpeedDecreaseBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Change button pressed
                if (btnPressed != BUTTON.PENDING) { btnPressed = BUTTON.BTN_REPRODUCTIVE_DECREASE; }
            }
        });
    
        hud.saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Change button pressed
                if (btnPressed != BUTTON.PENDING) { btnPressed = BUTTON.BTN_SAVE; }
            }
        });


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
            System.out.println("Increase Reproductive Time");
            reproductiveRate++; 
            hud.updateReproductiveRate(reproductiveRate);
            btnPressed = BUTTON.BTN_NONE;
        }
        if (btnPressed == BUTTON.BTN_REPRODUCTIVE_DECREASE) {
            System.out.println("Decrease Reproductive Time");
            hud.updateReproductiveRate(reproductiveRate);
            if (reproductiveRate > 0) {
                reproductiveRate--;
            } 
            btnPressed = BUTTON.BTN_NONE; 
        }

        // Save information in a file named reproductiveRate.txt and access it later in Prey.java
        if (btnPressed == BUTTON.BTN_SAVE) {
            File file = new File("reproductiveRate.txt"); 
            try { 
                FileWriter writer = new FileWriter(file);
                writer.write(String.valueOf(reproductiveRate));
                writer.close();
                System.out.println("Information has been saved"); 
            }
            catch (IOException e) {
                System.out.println(e);
            }

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
        main.transition.draw();


    }

    public int getReproductiveRate() { 
        return reproductiveRate; 
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
