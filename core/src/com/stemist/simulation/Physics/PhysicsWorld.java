package com.stemist.simulation.Physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.stemist.simulation.MainWindow;
import com.stemist.simulation.Game.Predator;
import com.stemist.simulation.Game.Prey;

public class PhysicsWorld {
    
    // List of entities
    private Array<Entity> entities;

    // No energy until inital deaths
    private int gracePeriod = MainWindow.PREADTOR_GRACE_PERIOD;

    // Count of each species
    public int numPrey = 0;
    public int numPred = 0;

    // Used for neural net
    private float[] netIn = new float[MainWindow.ENTITY_NUM_RAYS+1];
    private float[] netOut;



    // Constructor
    public PhysicsWorld() {

        // Create array
        entities = new Array<>();

    }

    // Add a new entity wihtin count limits
    public void addEntity(Entity e) {
        if (e instanceof Prey) {
            if (numPrey >= MainWindow.MAX_PREY) { return; }
            numPrey++;
        } else {
            if (numPred >= MainWindow.MAX_PREDATORS) { return; }
            numPred++;
        }
        entities.add(e);
    }

    // Get list of entities
    public Array<Entity> getEntities() {
        return entities;
    }

    // Solve entity positions
    public void update(float dt) {

        checkEntities(dt);
        applyConstraint();
        updateObjects(dt);

    }

    // Checks and mitigates entity overlapping as well as calculating ray distances
    private void checkEntities(float dt) {

        // How "squishy" collisions are
        final float responseCoef = 0.7f;

        // Reset them all before checking collision
        for (int i = 0; i < entities.size;) {

            // Get entity
            Entity e = entities.get(i);

            // Prepare neural net input with bias neuron
            for (int j = 0; j < MainWindow.ENTITY_NUM_RAYS; j++) {
                netIn[j] = 1f - e.getRays().getRayCollisions(j);
            }
            netIn[netIn.length-1] = 1f;

            // Get output
            netOut = e.brain.forward(netIn);

            // Apply output
            e.changeAngle(netOut[0] * MainWindow.ENTITY_MAX_ANGLE_VEL, dt);

            // Reset rays
            e.getRays().resetRays();

            
            // If predator loses energy it dies
            if (e instanceof Predator) {

                // Predator can't move backward
                e.setVelocity((netOut[1] > 0 ? netOut[1] : netOut[1]*0.2f) * MainWindow.ENTITY_MAX_VEL, dt);

                // Predators can start dying now
                e.changeEnergy(dt);
                // if (gracePeriod <= 0) {
                    // e.changeEnergy(dt);
                // }

                if (e.getEnergy() <= 0) {
                    entities.removeIndex(i);
                    numPred--;
                    continue;
                }
            }

            // If entity is a prey
            else {

                // Neural network output with deadzone
                // e.setVelocity((Math.abs(netOut[1]) < 0.1 ? 0f : netOut[0]) * MainWindow.ENTITY_MAX_VEL, dt);
                e.setVelocity(netOut[1] * MainWindow.ENTITY_MAX_VEL, dt);

                // Check for split
                ((Prey) e).checkSplit(this);

                // Change energy
                e.changeEnergy(dt);
            }

            // Get next index
            i++;

        }

        // Size before we make reproductions
        int enSize = entities.size;

        // If entity was removed don't increment loop
        boolean incr_i = true;

        // Check every entity with every other
        for (int i = 0; i < enSize;) {

            // First entity
            Entity e1 = entities.get(i);

            // Check for ray collisions as well
            Rays e1Rays = e1.getRays();

            // Reset flag
            incr_i = true;

            // Predator has chance to split during grace period
            if (gracePeriod > 0 && e1 instanceof Predator && Math.random() < 0.003) {
                ((Predator) e1).split(this);
            }
            
            // Check with every other entity
            for (int j = i+1; j < enSize;) {
                
                // Second entity
                Entity e2 = entities.get(j);
                
                // Check for ray collisions as well
                Rays e2Rays = e2.getRays();

                // Get distance between the entities
                float dist = e1.getPositionVector().dst(e2.getPositionVector());
                float minDist = e1.getRadius() + e2.getRadius();

                // Check ray collisions if applicable
                if ((e1 instanceof Predator && e2 instanceof Prey) || (e1 instanceof Prey && e2 instanceof Predator)) {
                    e1Rays.checkRaysHits(e1, e2);
                    e2Rays.checkRaysHits(e2, e1);
                }

                // Ignore if not touching
                if (dist > minDist) { j++; continue; }

                // If predator touches prey
                if ((e1 instanceof Predator && e2 instanceof Prey) ||
                        (e1 instanceof Prey && e2 instanceof Predator)) {

                    // Reduce grace period
                    gracePeriod--;
                    
                    // Remove eaten prey
                    Entity eTemp = null;
                    if (e1 instanceof Prey) {
                        entities.removeIndex(i);
                        incr_i = false;
                        eTemp = e2;
                        j++;
                    }
                    if (e2 instanceof Prey) {
                        entities.removeIndex(j);
                        eTemp = e1;
                    }

                    ((Predator) eTemp).madeKill();
                    ((Predator) eTemp).checkSplit(this);

                    // Reduce number of original entities remaining
                    enSize--;

                    // Keep count
                    numPrey--;
                }

                // If not killing then collide
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

                    // Increment next entity
                    j++;
                    
                }

            }

            // Increment next entity
            if (incr_i) i++;

        }
    }

    // Update the physics model for each entity
    private void updateObjects(float dt) {

        for (Entity e : entities) {
            e.update(dt);
        }

    }

    public int getNumPredators() { 
        return numPred;
    }

    public int getNumPrey() { 
        return numPrey;
    }

    public int getGraceCount() {
        return gracePeriod > 0 ? gracePeriod : 0;
    }

    // Apply world constraints to all entities
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
