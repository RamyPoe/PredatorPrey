package com.stemist.simulation.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stemist.simulation.MainWindow;
import com.stemist.simulation.Physics.Entity;
import com.stemist.simulation.Physics.PhysicsWorld;
import com.stemist.simulation.Physics.PhysicsRenderer;
 
 

public class GameScreen implements Screen {

    // Timer 
    private long startTime = MainWindow.getTimeMs();
    private float lastDeltaFrameTime = 0;

    // To change screens
    private MainWindow main;

    // For game prespective
    private OrthographicCamera cam;
    private Viewport viewport;
    public static final float CAM_SPEED_FACTOR = 1200f;
    public static final float CAM_ZOOM_FACTOR = 13f;

    // Game hud
    private GameHud hud;

    // Game world
    private PhysicsWorld pWorld;
    private PhysicsRenderer pRenderer;

    
    // Constructor
    public GameScreen(MainWindow main) {

        // For changing screens
        this.main = main;

        // Create hud
        hud = new GameHud(main.batch, this);

        // For game prespective
        cam = new OrthographicCamera();
        cam.zoom = 1f;
        viewport = new ExtendViewport(MainWindow.V_WIDTH, MainWindow.V_HEIGHT, cam);
        
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

    }

    @Override
    public void render(float delta) {
        // Seperate logic from rendering
        update(delta);
        lastDeltaFrameTime = delta;

        // Physics
        pWorld.update(delta);

        // Blank Screen
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Camera
        viewport.apply(false);
        cam.update();
        main.batch.setProjectionMatrix(cam.combined);
        pRenderer.getShapeRenderer().setProjectionMatrix(cam.combined);
        
        // Render game
        pRenderer.render(pWorld);
        
        // Show hud
        hud.draw(delta);

        // Transition screen fade in
        if (!main.transition.haveFadedIn && !main.transition.active)
            main.transition.fadeIn();
        
        // Draw transition
        main.transition.draw(delta);

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hud.stage.getViewport().update(width, height);
    }

    // Used for HUD
    public float getTimeElapsed() { return (MainWindow.getTimeMs()-startTime)/1000f; }
    public int getFps() { return Gdx.graphics.getFramesPerSecond(); }
    public float getFrameTime() { return lastDeltaFrameTime; }
    public int getNumPrey() { return pWorld.getNumPrey(); }
    public int getNumPred() { return pWorld.getNumPredators(); }
    public int getGracePeriod() { return pWorld.getGraceCount(); }




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
