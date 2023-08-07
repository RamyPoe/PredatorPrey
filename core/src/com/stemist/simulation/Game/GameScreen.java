package com.stemist.simulation.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stemist.simulation.MainWindow;
import com.stemist.simulation.Physics.Entity;
import com.stemist.simulation.Physics.PhysicsWorld;
import com.stemist.simulation.Physics.PhysicsRenderer;
 
 

public class GameScreen implements Screen {

    // Timer 
    private long timeElapsed = 0;
    private float lastDeltaFrameTime = 0;

    // To change screens
    private MainWindow main;

    // For game prespective
    private OrthographicCamera cam;
    private Viewport viewport;
    public static final float CAM_SPEED_FACTOR = 3200f;
    public static final float CAM_ZOOM_FACTOR = 13f;

    // Game hud
    private GameHud hud;

    // Entity world
    private GameWorld gameWorld;
    private PhysicsWorld pWorld;
    private PhysicsRenderer pRenderer;

    // Optional player
    Entity player;

    // For spectating
    private Entity spectatingEntity;
    private boolean spectating = false;

    
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
        gameWorld = new GameWorld();
        pWorld = new PhysicsWorld(gameWorld);

        // Spawn intial entities to world
        gameWorld.spawnInitial(pWorld);

        // To render world
        pRenderer = new PhysicsRenderer(new ShapeRenderer());

        // Debug (Add Player)
        // player = new Prey(new Vector2(0, 0));
        // player.brainEnabled = false;
        // pWorld.addEntity(player);

    }

    @Override
    public void show() {
    }

    // Processing
    private void update(float delta) {
        
        // Move camera
        if (!spectating && Gdx.input.isKeyPressed(Input.Keys.W) && cam.position.y < MainWindow.GAME_MAX_TOP)    { cam.position.y += CAM_SPEED_FACTOR * delta; }
        if (!spectating && Gdx.input.isKeyPressed(Input.Keys.S) && cam.position.y > MainWindow.GAME_MAX_BOTTOM) { cam.position.y -= CAM_SPEED_FACTOR * delta; }
        if (!spectating && Gdx.input.isKeyPressed(Input.Keys.D) && cam.position.x < MainWindow.GAME_MAX_RIGHT)  { cam.position.x += CAM_SPEED_FACTOR * delta; }
        if (!spectating && Gdx.input.isKeyPressed(Input.Keys.A) && cam.position.x > MainWindow.GAME_MAX_LEFT)   { cam.position.x -= CAM_SPEED_FACTOR * delta; }
        
        // Change zoom
        if (!spectating && Gdx.input.isKeyPressed(Input.Keys.Q) && cam.zoom < 25) { cam.zoom += CAM_ZOOM_FACTOR * delta; }
        if (!spectating && Gdx.input.isKeyPressed(Input.Keys.E) && cam.zoom > 1) { cam.zoom -= CAM_ZOOM_FACTOR * delta; }
        
        // Enter and leave spectating mode
        if (spectating) {
            while (spectatingEntity.isDead()) {
                spectatingEntity = pWorld.getEntities().get(
                    (int) (Math.random()*pWorld.getEntities().size())
                );
            }
            spectatingEntity.setSpectating(true);
            cam.position.x = spectatingEntity.getX();
            cam.position.y = spectatingEntity.getY();
            cam.zoom = 6;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            spectating = !spectating; 
            if (spectating) {
                spectatingEntity = pWorld.getEntities().get(
                    (int) (Math.random()*pWorld.getEntities().size())
                );
                spectatingEntity.setSpectating(true);
            } else {
                spectatingEntity.setSpectating(false);
            }
        }

        // Debug (Move player)
        /*
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) { player.setVelocity(MainWindow.ENTITY_MAX_VEL, delta); }
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) { player.setVelocity(-MainWindow.ENTITY_MAX_VEL, delta); }
        else { player.setVelocity(0, delta); }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) { player.changeAngle(-MainWindow.ENTITY_MAX_ANGLE_VEL, delta); }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) { player.changeAngle(MainWindow.ENTITY_MAX_ANGLE_VEL, delta); }
        */

        // Skip button check if transitioning
        if (main.transition.active) { return; }

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
        timeElapsed += delta*1000f;

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
        hud.draw();

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
    public float getTimeElapsed() { return timeElapsed/1000f; }
    public int getFps() { return Gdx.graphics.getFramesPerSecond(); }
    public float getFrameTime() { return lastDeltaFrameTime; }
    public int getNumPrey() { return gameWorld.getNumPrey(); }
    public int getNumPred() { return gameWorld.getNumPredators(); }
    public int getGracePeriod() { return gameWorld.getGraceCount(); }


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
        pWorld.closeThreads();
        hud.dispose();
    }
    
}
