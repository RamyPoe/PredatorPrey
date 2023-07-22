package com.stemist.simulation.Physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.stemist.simulation.MainWindow;

public class PhysicsWorld {
    
    // Engine Constants
    private final int SUB_STEPS = 1;

    // List of entities
    private Array<Entity> entities;

    // TODO: Add world constraints

    // Constructor
    public PhysicsWorld() {

        // Create array
        entities = new Array<>();

    }

    // Add a new entity
    public void addEntity(Entity e) {
        entities.add(e);
    }

    // Get list of entities
    public Array<Entity> getEntities() {
        return entities;
    }

    // Solve entity positions
    public void update(float dt) {

        // Divide into substeps
        float dtsub = dt / SUB_STEPS;
        for (int i = 0; i < SUB_STEPS; i++) {
            
            checkCollisions(dtsub);
            applyConstraint();
            updateObjects(dtsub);
            PredatorIsDead();
            HaltPrey();

        }

    }

    // Constantly reduces energy of predator. If predator reaches 0 energy then it is removed from the array. 
    private void PredatorIsDead() {
        for (int i = 0; i < entities.size; i++) {
            Entity e = entities.get(i); 
            if (e instanceof Predator) {
                if (((Predator) e).getEnergy() == 0) {
                    entities.removeIndex(i);
                }
                ((Predator) e).deleteEnergy();
            }
        }
    }
    // Constantly reduces energy of prey. If prey reaches 0 energy then the velocity and acceleration is set to 0. 
    public void HaltPrey () {
        for (int i = 0; i < entities.size; i++) {
            Entity e = entities.get(i); 
            if (e instanceof Prey) {
                if (((Prey) e).getEnergy() == 0) {
                    e.setVelocity(0, 0);
                }
                ((Prey) e).deleteEnergy();
            }
        }
    }


    // Checks and mitigates entityoverlapping
    private void checkCollisions(float dt) {

        final float responseCoef = 0.1f;

        // Check every entity with every other
        for (int i = 0; i < entities.size; i++) {
            Entity e1 = entities.get(i);
            for (int j = i+1; j < entities.size; j++) {
                Entity e2 = entities.get(j);

                // Get distance between them
                float dist = e1.getPositionVector().dst(e2.getPositionVector());
                float minDist = e1.getRadius() + e2.getRadius();

                // Ignore if not touching
                if (dist > minDist) { continue; }

                // If there is a mismatch in predator and prey... 
                else if ((e1 instanceof Predator && e2 instanceof Prey) || (e1 instanceof Prey && e2 instanceof Predator)) {
                    // Remove eaten prey.
                    if (e1 instanceof Prey) {
                        entities.removeIndex(i); 
                        ((Predator) e2).gainEnergy(); 
                    }
                    if (e2 instanceof Prey) {
                        entities.removeIndex(j);
                        ((Predator)e1).gainEnergy(); 
                    }
                }
                else {
                    // Normalize delta
                    Vector2 n1 = new Vector2(e1.getPositionVector()).sub(e2.getPositionVector()).nor();
                    Vector2 n2 = new Vector2(n1);

                    // Ratio of movement
                    float ratio1 = e1.getRadius() / minDist;                
                    float ratio2 = e2.getRadius() / minDist;
                    
                    // Apply changes
                    float delta = 0.5f * responseCoef * (dist - minDist);
                    e1.getPositionVector().sub(n1.scl(ratio2 * delta));
                    e2.getPositionVector().add(n2.scl(ratio1 * delta));

                }

 

            }
        }
    }

    
    private void updateObjects(float dt) {

        for (Entity e : entities) {
            e.update(dt);
        }

    }
    
    private void applyConstraint() {

        // Game box
        for (Entity e : entities) {

            // Position
            Vector2 pos = e.getPositionVector();

            // Check bounds
            if (pos.x-e.getRadius() < MainWindow.GAME_MAX_LEFT) {
                pos.set(MainWindow.GAME_MAX_LEFT+e.getRadius(), pos.y);
            }
            if (pos.x+e.getRadius() > MainWindow.GAME_MAX_RIGHT) {
                pos.set(MainWindow.GAME_MAX_RIGHT-e.getRadius(), pos.y);
            }
            if (pos.y+e.getRadius() > MainWindow.GAME_MAX_TOP) {
                pos.set(pos.x, MainWindow.GAME_MAX_TOP-e.getRadius());
            }
            if (pos.y-e.getRadius() < MainWindow.GAME_MAX_BOTTOM) {
                pos.set(pos.x, MainWindow.GAME_MAX_BOTTOM+e.getRadius());
            }

        }

    }



}
