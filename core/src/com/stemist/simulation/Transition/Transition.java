package com.stemist.simulation.Transition;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.stemist.simulation.MainWindow;

public class Transition {
    
    // For displaying
    OrthographicCamera cam;
    FitViewport vp;

    // For drawing
    public Texture texture;
    MainWindow game;

    // Position in percent
    public boolean active = false;
    public boolean fadeIn = true;
    float pos;

    // For the frame of entering/leaving
    public boolean haveFadedIn = false;
    public boolean haveFadedOut = false;

    // Speed of transition
    float speed;

    // For drawing
    MainWindow.SCREEN screen;

    public Transition(float speed) {

        // Speed of transition
        this.speed = speed/1000.0f;

        // Camera for drawing
        cam = new OrthographicCamera();
        cam.position.x = MainWindow.V_WIDTH/2;
        cam.position.y = 1.0f / 2;
        vp = new FitViewport(MainWindow.V_WIDTH, 1, cam);
        vp.update(MainWindow.V_WIDTH, 1);  
        cam.update();


        // Percentage
        pos = 0.0f;

        // Create texture
        texture = MainWindow.createTexture(MainWindow.V_WIDTH, 1, Color.BLACK);

    }

    // Set the game
    public void setGame(MainWindow game) {
        this.game = game;
    }

    // To fade out
    public void fadeOut(MainWindow.SCREEN s) {
        haveFadedIn = false;
        
        this.screen = s;
        fadeIn = false;
        pos = 0;
        active = true;
    }
    
    // To fade in
    public void fadeIn() {
        haveFadedIn = true;

        this.screen = null;
        fadeIn = true;
        pos = 0;
        active = true;
    }

    // Draw at correct position
    public void draw() {

        // Don't draw if we're not in use
        if (!active) { return; }

        // Increment till done
        pos += speed;
        if (pos > 1) { pos = 1; }

        // Move black screen out if fading out
        if (fadeIn) { pos += 1; }
        
        // Draw at pos
        game.batch.setProjectionMatrix(cam.combined);
        game.batch.begin();
        game.batch.draw(texture, pos*MainWindow.V_WIDTH - MainWindow.V_WIDTH, 0);
        game.batch.end();

        // Get rid of the last effect
        if (fadeIn) { pos -= 1; }
        
        // We drew the last frame, change screens
        if (pos == 1) {
            active = false;

            if (!fadeIn) {
                game.setGameScreen(screen);
            }

        }

    }

}