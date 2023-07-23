package com.stemist.simulation.Configuration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
 


public class ConfigHud implements Disposable {

    // Stage setup
    public Stage stage;
    public Viewport viewport;
    private OrthographicCamera cam;

    // Assets & Fonts
    private Texture backgroundTexture;
    private BitmapFont font = new BitmapFont();

    // Buttons
    public ImageButton reproductiveSpeedIncreaseBtn; 
    public ImageButton reproductiveSpeedDecreaseBtn; 
    public ImageButton saveBtn; 

    private int reproductiveRate; 

    // Constructor
    public ConfigHud(SpriteBatch sb, int reproductiveRate) {
        this.reproductiveRate = reproductiveRate; 
        // For prespective
        cam = new OrthographicCamera();
        viewport = new FitViewport(MainWindow.V_WIDTH, MainWindow.V_HEIGHT, cam);

        // Stage
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.setFillParent(true);
        table.bottom();
        table.padBottom(200);
 
        
        // Draw buttons 
        Drawable reproductionBtnTexture = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("menu/reproductiveBtn.png"))));
        reproductiveSpeedIncreaseBtn = new ImageButton(reproductionBtnTexture);

        Drawable deproductionBtnTexture = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("menu/deproductiveBtn.png"))));
        reproductiveSpeedDecreaseBtn = new ImageButton(deproductionBtnTexture);

        Drawable saveBtnTexture = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("menu/saveReproductive.png"))));
        saveBtn = new ImageButton(saveBtnTexture); 

        // Configure table to add buttons. 
        table.add(reproductiveSpeedIncreaseBtn).align(Align.right).padBottom(50);
        table.row();
        table.add(reproductiveSpeedDecreaseBtn).align(Align.right).padBottom(50);
        table.row(); 
        table.add(saveBtn).align(Align.right); 
                    

        // Add table to stage
        stage.addActor(table);
        

        // So that button clicks work
        Gdx.input.setInputProcessor(stage);

        // Background image
        backgroundTexture = new Texture(Gdx.files.internal("menu/background.jpg"));

    }

    public void updateReproductiveRateLabel(int reproductiveRate) {

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

        // Create font and display reproductive rate. 
        stage.getBatch().begin(); 
        font.draw(stage.getBatch(), String.valueOf(reproductiveRate), 500, 125); 

        // Set scale and color. 
        font.getData().setScale(5f);
        font.setColor(Color.WHITE);

        stage.getBatch().end(); 
        // Draw GUI
        stage.draw();

    }

    // Update reproductive rate text from ConfigScreen.java
    public void updateReproductiveRate(int newReproductiveRate) {
        reproductiveRate = newReproductiveRate; 
    }

    
    @Override
    public void dispose() {
        backgroundTexture.dispose();
        stage.dispose();
    }
    
}
