package com.stemist.simulation.Menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stemist.simulation.MainWindow;


public class MenuHud implements Disposable {

    // Stage setup
    public Stage stage;
    public Viewport viewport;
    private OrthographicCamera cam;

    // Assets
    public Texture backgroundTexture;


    // Constructor
    public MenuHud(SpriteBatch sb) {

        // For prespective
        cam = new OrthographicCamera();
        viewport = new FitViewport(MainWindow.V_WIDTH, MainWindow.V_HEIGHT, cam);

        // Stage
        stage = new Stage(viewport, sb);

        // Background image
        backgroundTexture = new Texture(Gdx.files.internal("menu/background.jpg"));

    }

    // Called every loop
    public void draw() {

        // For animations
        stage.act();

        // Get batch ready
        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);
        
        // Draw background image
        stage.getBatch().begin();

        stage.getBatch().draw(backgroundTexture, 0, 0, MainWindow.V_WIDTH, MainWindow.V_HEIGHT);

        stage.getBatch().end();
        
        // Draw GUI
        stage.draw();

    }

    
    @Override
    public void dispose() {
    }
    
}
