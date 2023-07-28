package com.stemist.simulation.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stemist.simulation.MainWindow;

public class GameHud implements Disposable {

    // Stage setup
    public Stage stage;
    public Viewport viewport;
    private OrthographicCamera cam;

    // Font for text
    private LabelStyle labelStyle;

    // For getting hud info
    private GameScreen gameScreen;

    // All the labels to be shown
    private Label timeElapsedLabel;
    private Label fpsLabel;
    private Label frameTimeLabel;
    private Label numPredLabel;
    private Label numPreyLabel;
    private Label gracePeriodLabel;

    // Constructor
    public GameHud(SpriteBatch sb, GameScreen gameScreen) {
        this.gameScreen = gameScreen;

        // For prespective
        cam = new OrthographicCamera();
        viewport = new ScreenViewport(cam);

        // Stage
        stage = new Stage(viewport, sb);

        // Label font
        FileHandle fontFileHandle = Gdx.files.internal("fonts/font.ttf");
        
        // Load Fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFileHandle);
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        //========================================
        parameter.size = 24;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 0f;
        
        labelStyle = new Label.LabelStyle();
        labelStyle.fontColor = parameter.color;
        labelStyle.font = generator.generateFont(parameter); 
        //========================================

        // Create table
        Table table = new Table();
        table.setFillParent(true);
        table.top().left();
        table.padLeft(30);
        table.padTop(30);
 
        // Add labels to table
        timeElapsedLabel = new Label("", labelStyle);
        table.add(timeElapsedLabel).align(Align.left); table.row();
        
        fpsLabel = new Label("", labelStyle);
        table.add(fpsLabel).align(Align.left); table.row();

        frameTimeLabel = new Label("", labelStyle);
        table.add(frameTimeLabel).align(Align.left); table.row();

        numPredLabel = new Label("", labelStyle);
        table.add(numPredLabel).align(Align.left); table.row();

        numPreyLabel = new Label("", labelStyle);
        table.add(numPreyLabel).align(Align.left); table.row();

        gracePeriodLabel = new Label("", labelStyle);
        table.add(gracePeriodLabel).align(Align.left);

        
        // Add table to stage
        stage.addActor(table);

    }

    // Set the text for the labels
    private void setLabels() {
        timeElapsedLabel.setText("Elapsed: " + gameScreen.getTimeElapsed() + " s");
        fpsLabel.setText("FPS: " + gameScreen.getFps());
        frameTimeLabel.setText("Frame Time: " + gameScreen.getFrameTime()*1000 + " ms");
        numPredLabel.setText("Predators: " + gameScreen.getNumPred() + " / " + MainWindow.MAX_PREDATORS);
        numPreyLabel.setText("Prey: " + gameScreen.getNumPrey() + " / " + MainWindow.MAX_PREY);
        gracePeriodLabel.setText("Grace Period: " + gameScreen.getGracePeriod());
    }

    // Drawing to the screen
    public void draw(float dt) {

        // Sets the text for the labels
        setLabels();

        // For animations
        stage.act();

        // Get batch ready
        viewport.apply(true);
        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);

        // Draw Hud
        stage.draw();

    }


    @Override
    public void dispose() {
        stage.dispose();
    }
}