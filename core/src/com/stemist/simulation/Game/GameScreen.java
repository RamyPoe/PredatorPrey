package com.stemist.simulation.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stemist.simulation.MainWindow;
import com.stemist.simulation.Physics.Entity;
import com.stemist.simulation.Physics.PhysicsWorld;
import com.stemist.simulation.Physics.PhysicsRenderer;
import java.util.Random; 
 


public class GameScreen implements Screen {

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
        for (int i = MainWindow.GAME_MAX_LEFT + 50 + MainWindow.ENTITY_RADIUS; i < MainWindow.GAME_MAX_RIGHT; i += MainWindow.ENTITY_RADIUS*20) {
            for (int j = MainWindow.GAME_MAX_BOTTOM + 50 + MainWindow.ENTITY_RADIUS; j < MainWindow.GAME_MAX_TOP; j += MainWindow.ENTITY_RADIUS*20) {
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
        if (Gdx.input.isKeyPressed(Input.Keys.Q) && cam.zoom < 6) { cam.zoom += CAM_ZOOM_FACTOR * delta; }
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

        /*
        // Move self
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            self.setVelocity(1, delta);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            self.setVelocity(-1, delta);
        } else {
            self.setVelocity(0, delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            self.changeAngle(1, delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            self.changeAngle(-1, delta);
        }
        */

        /*
        // Spawn Prey 
        if (Gdx.input.isKeyPressed(Input.Keys.N)) {
                Entity e = new Prey(new Vector2(spawn, spawn));
                e.setVelocity(100, delta);
                pWorld.addEntity(e);
        }
        
        // Spawn Predators 
        if (Gdx.input.isKeyPressed(Input.Keys.M)) {
                Entity e = new Predator(new Vector2(spawn, spawn)); 
                e.setVelocity(100, delta); 
                pWorld.addEntity(e); 
        }
        */

        /*
        // Randomly spawns entities (1/1000) based on whether or not they have survived on top of a selected entity. 
        Random rand = new Random();
        int result; 
        for (int i = 0; i < pWorld.getEntities().size; i++) { 
            Entity selectEntity = pWorld.getEntities().get(i);
            result = rand.nextInt(1000-1) + 1; 
            //System.out.println(result); 
            if (result == 500) {
                Entity e = new Prey(new Vector2(selectEntity.getX(),selectEntity.getY()), 30);
                e.setVelocity(100, delta);
                pWorld.addEntity(e);
            }
        }
        */

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
