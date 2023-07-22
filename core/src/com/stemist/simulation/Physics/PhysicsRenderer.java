package com.stemist.simulation.Physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.stemist.simulation.MainWindow;
import java.util.Random; 

public class PhysicsRenderer {
    Random random = new Random(); 
    boolean isHeads = random.nextBoolean(); 
    
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
        
        // Render every shape
        for (int i = 0; i < e.size; i++) {
            Entity en = e.get(i);
            
            // Draw a circle

            if (en instanceof Prey) {
                renderer.setColor(Color.GREEN);
                renderer.circle(en.getX(), en.getY(), en.getRadius());
            }
            else {
                renderer.setColor(Color.RED); 
                renderer.circle(en.getX(), en.getY(), en.getRadius()); 
            }

            // Show direction
            renderer.setColor(Color.LIGHT_GRAY);
            renderer.circle(
                en.getX() + (float) Math.cos(Math.toRadians(en.getAngle())) * (en.getRadius()-10),
                en.getY() + (float) Math.sin(Math.toRadians(en.getAngle())) * (en.getRadius()-10),
                10f
            );
            // System.out.println("CIRCLE: " + en.getX() + " " + en.getY() + " " + en.getRadius());
        }

        // End renderer
        renderer.end();

    }
}
