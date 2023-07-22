package com.stemist.simulation.Menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
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
    private Texture backgroundTexture;

    // Buttons
    public ImageButton startBtn;
    public ImageButton configBtn;

    // Constructor
    public MenuHud(SpriteBatch sb) {

        // For prespective
        cam = new OrthographicCamera();
        viewport = new FitViewport(MainWindow.V_WIDTH, MainWindow.V_HEIGHT, cam);

        // Stage
        stage = new Stage(viewport, sb);

        // Start button
        Drawable startBtnTexture = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("menu/startBtn.png"))));
        startBtn = new ImageButton(startBtnTexture);
        
        // Config button
        Drawable configBtnTexture = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("menu/configBtn.png"))));
        configBtn = new ImageButton(configBtnTexture);

        // Table to add buttons to
        Table table = new Table();
        table.setFillParent(true);
        table.bottom();
        table.padBottom(200);

        table.add(startBtn).align(Align.center).padBottom(50);
        table.row();
        table.add(configBtn).align(Align.center);
        

        // Add table to stage
        stage.addActor(table);

        // So that button clicks work
        Gdx.input.setInputProcessor(stage);

        // Background image
        backgroundTexture = new Texture(Gdx.files.internal("menu/background.png"));

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
        backgroundTexture.dispose();
        stage.dispose();
    }
    
}
