package com.stemist.simulation.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stemist.simulation.MainWindow;
import com.stemist.simulation.Physics.Entity;
import com.stemist.simulation.Physics.PhysicsWorld;
import com.stemist.simulation.Physics.PhysicsRenderer;
import com.badlogic.gdx.graphics.Color;
 
 


public class GameScreen implements Screen {

    // Timer 
    private long startTime = MainWindow.getTimeMs();
    private float elapsedTime;

    // Used for text display 
    private SpriteBatch spriteBatch = new SpriteBatch(); 
    private BitmapFont font = new BitmapFont(); 
 
    // To change screens
    MainWindow main;

    // For prespective
    private OrthographicCamera cam;
    private Viewport viewport;
    public static final float CAM_SPEED_FACTOR = 1200f;
    public static final float CAM_ZOOM_FACTOR = 13f;

    // Game world
    PhysicsWorld pWorld;
    PhysicsRenderer pRenderer;

    
    // Constructor
    public GameScreen(MainWindow main) {

        // For changing screens
        this.main = main;

        // For prespective
        cam = new OrthographicCamera();
        cam.zoom = 1.0f;
        viewport = new FitViewport(MainWindow.V_WIDTH, MainWindow.V_HEIGHT, cam);

        // Start in middle of world
        cam.position.x = (MainWindow.GAME_MAX_LEFT+MainWindow.GAME_MAX_RIGHT)/2;
        cam.position.y = (MainWindow.GAME_MAX_BOTTOM+MainWindow.GAME_MAX_TOP)/2;

        // Create game world
        pWorld = new PhysicsWorld();
        pRenderer = new PhysicsRenderer(new ShapeRenderer());

        // Spawn inital
        Vector2 pos = new Vector2(0, 0);
        for (int i = MainWindow.GAME_MAX_LEFT + 50 + MainWindow.ENTITY_RADIUS; i < MainWindow.GAME_MAX_RIGHT; i += MainWindow.ENTITY_RADIUS*10) {
            for (int j = MainWindow.GAME_MAX_BOTTOM + 50 + MainWindow.ENTITY_RADIUS; j < MainWindow.GAME_MAX_TOP; j += MainWindow.ENTITY_RADIUS*10) {
                pos.set(i, j);
                Entity e = Math.random() < MainWindow.CHANCE_INITIAL_PREY ? new Prey(pos) : new Predator(pos);
                pWorld.addEntity(e);
            }
        }

    }

    @Override
    public void show() {
    }

    // Processing
    private void update(float delta) {
        
        // Move camera
        if (Gdx.input.isKeyPressed(Input.Keys.W) && cam.position.y < MainWindow.GAME_MAX_TOP)    { cam.position.y += CAM_SPEED_FACTOR * delta; }
        if (Gdx.input.isKeyPressed(Input.Keys.S) && cam.position.y > MainWindow.GAME_MAX_BOTTOM) { cam.position.y -= CAM_SPEED_FACTOR * delta; }
        if (Gdx.input.isKeyPressed(Input.Keys.D) && cam.position.x < MainWindow.GAME_MAX_RIGHT)  { cam.position.x += CAM_SPEED_FACTOR * delta; }
        if (Gdx.input.isKeyPressed(Input.Keys.A) && cam.position.x > MainWindow.GAME_MAX_LEFT)   { cam.position.x -= CAM_SPEED_FACTOR * delta; }
        
        // Change zoom
        if (Gdx.input.isKeyPressed(Input.Keys.Q) && cam.zoom < 10) { cam.zoom += CAM_ZOOM_FACTOR * delta; }
        if (Gdx.input.isKeyPressed(Input.Keys.E) && cam.zoom > 1) { cam.zoom -= CAM_ZOOM_FACTOR * delta; }

        // Debug
        // System.out.println("PREY: " + PhysicsWorld.numPrey + "  |  PRED: " + PhysicsWorld.numPred);

        // Skip button check if transitioning
        if (main.transition.active) {
            return;
        }

        // Check click to leave
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            main.transition.fadeOut(MainWindow.SCREEN.MENU);
        }

        // To be shown on screen
        elapsedTime = (MainWindow.getTimeMs() - startTime)/1000;

    }

    @Override
    public void render(float delta) {
        // Seperate logic from rendering
        update(delta);

        // Physics
        pWorld.update(delta);

        // Blank Screen
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Camera
        cam.update();
        main.batch.setProjectionMatrix(cam.combined);
        pRenderer.getShapeRenderer().setProjectionMatrix(cam.combined);

        pRenderer.render(pWorld);

        // Prepare to draw
        main.batch.begin();
        

        // Display details on screen
        spriteBatch.begin(); 
        font.getData().setScale(2, 2);
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 784);
        font.draw(spriteBatch, "Seconds Elapsed: " + elapsedTime, 10, 754);
        font.draw(spriteBatch, "Number Predator: " + pWorld.getPredators() + " / " + MainWindow.MAX_PREDATORS, 10, 724); 
        font.draw(spriteBatch, "Number Prey: " + pWorld.getPrey() + " / " + MainWindow.MAX_PREY, 10, 694);
        font.draw(spriteBatch, "Grace Period: " + pWorld.getGraceCount(), 10, 664);
        spriteBatch.end();

        // Done drawing
        main.batch.end();


        // Transition screen fade in
        if (!main.transition.haveFadedIn && !main.transition.active)
            main.transition.fadeIn();
        
        // Draw transition
        main.transition.draw();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
        
    }
    
}
