package com.stemist.simulation.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stemist.simulation.MainWindow;
import com.stemist.simulation.Physics.Entity;
import com.stemist.simulation.Physics.Predator; 
import com.stemist.simulation.Physics.Prey; 
import com.stemist.simulation.Physics.PhysicsWorld;
import com.stemist.simulation.Physics.PhysicsRenderer;
import java.util.Random; 
 


public class GameScreen implements Screen {


    // To keep track of time 
    long startTime = TimeUtils.millis();

    // To change screens
    MainWindow main;

    // For prespective
    private OrthographicCamera cam;
    private Viewport viewport;
    public static final float CAM_SPEED_FACTOR = 800f;
    public static final float CAM_ZOOM_FACTOR = 10f;


    // Game world
    PhysicsWorld pWorld;
    PhysicsRenderer pRenderer;
    
    int spawn = 0;

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
        if (Gdx.input.isKeyPressed(Input.Keys.Q) && cam.zoom < 4) { cam.zoom += CAM_ZOOM_FACTOR * delta; }
        if (Gdx.input.isKeyPressed(Input.Keys.E) && cam.zoom > 1) { cam.zoom -= CAM_ZOOM_FACTOR * delta; }

        // Skip button check if transitioning
        if (main.transition.active) {
            return;
        }


    

        // Check click
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            main.transition.fadeOut(MainWindow.SCREEN.MENU);
        }

        // Spawn Prey 
        if (Gdx.input.isKeyPressed(Input.Keys.N)) {
                Entity e = new Prey(new Vector2(spawn, spawn), 30);
                e.setVelocity(100, delta);
                pWorld.addEntity(e);
        }
        
        // Spawn Predators 
        if (Gdx.input.isKeyPressed(Input.Keys.M)) {
                Entity e = new Predator(new Vector2(spawn, spawn), 30); 
                e.setVelocity(100, delta); 
                pWorld.addEntity(e); 
        }
    }

    @Override
    public void render(float delta) {
        long elapsedTime = (TimeUtils.timeSinceMillis(startTime)/1000);
        System.out.println(elapsedTime);

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

        // Draw stuf


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
