package com.stemist.simulation.Physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
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

    // Render shapes
    public void render(PhysicsWorld pWorld) {

        // Game box limits
        renderer.begin(ShapeType.Filled);
        // renderer.setColor(153, 204, 255, 1);
        renderer.setColor(Color.BLUE);
        renderer.rect(MainWindow.GAME_MAX_LEFT, MainWindow.GAME_MAX_BOTTOM, MainWindow.GAME_MAX_RIGHT-MainWindow.GAME_MAX_LEFT, MainWindow.GAME_MAX_TOP-MainWindow.GAME_MAX_BOTTOM);
        renderer.end();

        // Get list of entities
        Array<Entity> e = pWorld.getEntities();

        // Begin renderer
        renderer.begin(ShapeType.Filled);
        renderer.setColor(Color.WHITE);

        // Render every shape
        for (int i = 0; i < e.size; i++) {
            // Draw a circle
            renderer.circle(e.get(i).getX(), e.get(i).getY(), e.get(i).getRadius());
            // System.out.println("CIRCLE: " + e.get(i).getX() + " " + e.get(i).getY() + " " + e.get(i).getRadius());
        }

        // End renderer
        renderer.end();

    }
}
