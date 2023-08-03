package com.stemist.simulation.Physics;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

    // Render World
    public void render(PhysicsWorld pWorld) {

        // Game box limits
        renderer.begin(ShapeType.Filled);
        renderer.setColor(135f / 255f, 206f / 255f, 250f / 255f, 1f);
        renderer.rect(MainWindow.GAME_MAX_LEFT, MainWindow.GAME_MAX_BOTTOM, MainWindow.GAME_MAX_RIGHT-MainWindow.GAME_MAX_LEFT, MainWindow.GAME_MAX_TOP-MainWindow.GAME_MAX_BOTTOM);
        renderer.end();
        
        // Draw cell grid
        renderer.begin(ShapeType.Filled);
        renderer.setColor(164f / 255f, 213f / 255f, 245f / 255f, 1f);
        for (int i = MainWindow.GAME_MAX_LEFT; i < MainWindow.GAME_MAX_RIGHT; i += MainWindow.CELL_SIZE) {
            renderer.rectLine(i, MainWindow.GAME_MAX_TOP, i, MainWindow.GAME_MAX_BOTTOM, 32);
        }
        for (int i = MainWindow.GAME_MAX_BOTTOM; i < MainWindow.GAME_MAX_TOP; i += MainWindow.CELL_SIZE) {
            renderer.rectLine(MainWindow.GAME_MAX_LEFT, i, MainWindow.GAME_MAX_RIGHT, i, 32);
        }
        renderer.end();


        // Get list of entities
        Array<Entity> e = pWorld.getEntities();

        // Begin renderer
        renderer.begin(ShapeType.Filled);
        
        // Render every shape
        for (int i = 0; i < e.size; i++) {
            Entity en = e.get(i);
            
            // Draw a circle
            renderer.setColor(en.getColor());
            renderer.circle(en.getX(), en.getY(), en.getRadius()); 

            // Show direction
            renderer.setColor(Color.LIGHT_GRAY);
            renderer.circle(
                en.getX() + (float) Math.cos(Math.toRadians(en.getAngle())) * (en.getRadius()-10),
                en.getY() + (float) Math.sin(Math.toRadians(en.getAngle())) * (en.getRadius()-10),
                10f
            );

            // Draw when key held
            if (!Gdx.input.isKeyPressed(Input.Keys.B)) {
                continue;
            }

            // Get rays instance
            Rays r = en.getRays();

            // Ray color
            renderer.setColor(Color.WHITE);

            // Draw the ray lines
            float startAngle = en.getAngle() - r.getFov()/2;
            float stepAngle = r.getFov()/(r.getNumRays()-1);
            Ray ray = new Ray();

            for (int j = 0; j < r.getNumRays(); j++) {

                // Get angle for this ray
                float angle = (startAngle + j*stepAngle) % 360;

                // Setup this ray
                ray.origin.set(en.getPositionVector());
                ray.end.set(r.getRayDistance() * r.getRayCollisions(j), 0);
                ray.end.setAngleDeg(angle);
                ray.end.add(ray.origin);

                // Draw line
                renderer.rectLine(ray.origin.x, ray.origin.y, ray.end.x, ray.end.y, 2);
            }

        }

        // End renderer
        renderer.end();

    }
}
