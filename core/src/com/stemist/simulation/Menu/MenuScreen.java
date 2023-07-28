package com.stemist.simulation.Menu;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.stemist.simulation.MainWindow;

public class MenuScreen implements Screen {

    // Detect button press
    private enum BUTTON {
        BTN_NONE,
        BTN_START,
        BTN_CONFIG,
        PENDING
    }
    private BUTTON btnPressed = BUTTON.BTN_NONE;

    // To change screens
    MainWindow main;

    // For button interaction
    MenuHud hud;

    // Constructor
    public MenuScreen(MainWindow main) {
        // For changing screens
        this.main = main;

        // Add hud
        hud = new MenuHud(main.batch);

        // When start button is pressed
        hud.startBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Change button pressed
                if (btnPressed != BUTTON.PENDING) { btnPressed = BUTTON.BTN_START; }
            }
        });

        // When config button is pressed
        hud.configBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Change button pressed
                if (btnPressed != BUTTON.PENDING) { btnPressed = BUTTON.BTN_CONFIG; }
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

        // Check buttons
        if (btnPressed == BUTTON.BTN_START) {
            main.transition.fadeOut(MainWindow.SCREEN.GAME);
            btnPressed = BUTTON.BTN_NONE;
        }
        if (btnPressed == BUTTON.BTN_CONFIG) {
            System.out.println("CONFIG BUTTON PRESSED");
            main.transition.fadeOut(MainWindow.SCREEN.CONFIG);
            
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
