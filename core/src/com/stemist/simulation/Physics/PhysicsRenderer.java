package com.stemist.simulation.Physics;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.stemist.simulation.MainWindow;

public class PhysicsRenderer {

    // For drawing
    private ShapeRenderer renderer;

    // Constructor
    public PhysicsRenderer(ShapeRenderer renderer) {
        // Set renderer
        this.renderer = renderer;
    }

    // Get the shape renderer
    public ShapeRenderer getShapeRenderer() {
        return renderer;
    }

    // Render World
    public void render(PhysicsWorld pWorld) {

        // Begin renderer
        renderer.begin(ShapeType.Filled);

        // Game box limits
        renderer.setColor(135f / 255f, 206f / 255f, 250f / 255f, 1f);
        renderer.rect(MainWindow.GAME_MAX_LEFT, MainWindow.GAME_MAX_BOTTOM, MainWindow.GAME_MAX_RIGHT-MainWindow.GAME_MAX_LEFT, MainWindow.GAME_MAX_TOP-MainWindow.GAME_MAX_BOTTOM);
        
        // Draw cell grid
        renderer.setColor(164f / 255f, 213f / 255f, 245f / 255f, 1f);
        for (int i = MainWindow.GAME_MAX_LEFT+MainWindow.CELL_SIZE; i < MainWindow.GAME_MAX_RIGHT; i += MainWindow.CELL_SIZE) {
            renderer.rectLine(i, MainWindow.GAME_MAX_TOP, i, MainWindow.GAME_MAX_BOTTOM, 32);
        }
        for (int i = MainWindow.GAME_MAX_BOTTOM+MainWindow.CELL_SIZE; i < MainWindow.GAME_MAX_TOP; i += MainWindow.CELL_SIZE) {
            renderer.rectLine(MainWindow.GAME_MAX_LEFT, i, MainWindow.GAME_MAX_RIGHT, i, 32);
        }

        // Get list of entities
        List<Entity> e = pWorld.getEntities();

        // Render every shape
        for (int i = 0; i < e.size(); i++) {
            Entity en = e.get(i);
            
            // Draw a circle
            renderer.setColor(en.getColor());
            renderer.circle(en.getX(), en.getY(), en.getRadius(), 8); 

            // Show direction
            renderer.setColor(Color.LIGHT_GRAY);
            renderer.circle(
                en.getX() + (float) Math.cos(Math.toRadians(en.getAngle())) * (en.getRadius()-10),
                en.getY() + (float) Math.sin(Math.toRadians(en.getAngle())) * (en.getRadius()-10),
                10f
            );

            // Draw when key held
            if (!Gdx.input.isKeyPressed(Input.Keys.B) && !en.isSpectating()) { continue; }

            // Get rays instance
            Rays rays = en.getRays();

            // Ray color
            renderer.setColor(Color.WHITE);

            // Loop through all rays
            for (int j = 0; j < rays.getNumRays(); j++) {
                
                // Get angle for this ray
                // float angle = (startAngle + j*stepAngle) % 360;
                Ray r = rays.getRayArray()[j];

                // Get end point of ray
                float endx = r.origin.x + r.dir.x * r.getMagnitude() * en.getRayCollisionOutArr()[j];
                float endy = r.origin.y + r.dir.y * r.getMagnitude() * en.getRayCollisionOutArr()[j];

                // Draw ray
                renderer.rectLine(en.getX(), en.getY(), endx, endy, 2);
            }

        }

        // End renderer
        renderer.end();

    }
}
