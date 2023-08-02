package com.stemist.simulation.Physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.stemist.simulation.MainWindow;
import com.stemist.simulation.Game.Predator;
import com.stemist.simulation.Game.Prey;

public class PhysicsWorld {
    
    // List of entities
    private Array<Entity> entities;

    // How "squishy" collisions are
    private final float RESPONSE_COEf = 0.7f;

    // Conserve predators until inital deaths
    private int graceGen = MainWindow.PREADTOR_GRACE_PERIOD;

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



    // Solve entity positions
    public void update(float dt) {

        tickEntities(dt);
        checkCollisions(dt);
        updateObjects(dt);
        applyConstraint();
        
    }

    // Checks and mitigates entity overlapping as well as calculating ray distances
    private void tickEntities(float dt) {

        // Reset them all before checking collision
        for (int i = 0; i < entities.size;) {

            // Get entity
            Entity e = entities.get(i);

            // Use raycast and neural network
            updateNeuralNetRays(e, dt);

            // Update energy and check for events
            boolean death = updateEnergy(e, dt);
            if (death) { entities.removeIndex(i); numPred--; continue; }

            // Get next index
            i++;

        }

    }

    // Checks all body and raycast collisions and registers kills
    private void checkCollisions(float dt) {
        
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
            if (graceGen > 0 && e1 instanceof Predator && Math.random() < 0.006) {
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
                Predator predTemp = null;
                if (onePreyOnePred(e1, e2)) {

                    // If close enough
                    if (dist < Math.max(MainWindow.PREDATOR_SIGHT_RANGE, MainWindow.PREY_SIGHT_RANGE)) {
                        e1Rays.checkRaysHits(e1, e2);
                        e2Rays.checkRaysHits(e2, e1);
                    }

                    // Check digestion cooldown for kill
                    predTemp = (Predator) (e1 instanceof Predator ? e1 : e2);

                }

                // From now on means there was collision
                if (dist > minDist) { j++; continue; }

                
                // If predator touches prey
                if ( onePreyOnePred(e1, e2) && (predTemp != null && !predTemp.digesting()) ) {

                    // Reduce grace period
                    graceGen--;
                    
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
                    float delta = 0.5f * RESPONSE_COEf * (dist - minDist);
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

    // Update energy and check event, return true if predator died
    private boolean updateEnergy(Entity e, float dt) {

        // Make energy change
        e.changeEnergy(dt);

        // Predators can die #Debug
        if (e instanceof Predator) {
            if (e.getEnergy() <= 0) {
                if (graceGen > 0 && getNumPredators() != MainWindow.MAX_PREDATORS) { return false; }
                return true;
            }
        } else {

            // Check for split
            ((Prey) e).checkSplit(this);

        }


        // No death
        return false;

    }

    // Gets the raycast inputs for neural net and apply output
    private void updateNeuralNetRays(Entity e, float dt) {

        /* https://www.desmos.com/calculator/tam8eh34fe */
        for (int j = 0; j < MainWindow.ENTITY_NUM_RAYS; j++) {
            netIn[j] = 1 - e.getRays().getRayCollisions(j);

            // netIn[j] = (netIn[j]) > 0.01f ? 1f : 0f;
            // netIn[j] = (float) Math.pow(netIn[j], 1f/2f);
            netIn[j] = (float) Math.pow(netIn[j], 1f/3f);
        }

        // Bias neuron
        netIn[netIn.length-1] = 1f;

        // Get output
        netOut = e.brain.forward(netIn);

        // Apply output
        if (e.brainEnabled) {
            e.changeAngle(netOut[0] * MainWindow.ENTITY_MAX_ANGLE_VEL, dt);

            // Handicap predator and discourage backward
            if (e instanceof Predator)
                e.setVelocity((netOut[1] > 0 ? netOut[1]*0.8f : netOut[1]*0.2f) * MainWindow.ENTITY_MAX_VEL, dt);
            if (e instanceof Prey)
                e.setVelocity(netOut[1] * MainWindow.ENTITY_MAX_VEL, dt);
        }

        // Reset rays
        e.getRays().resetRays();

    }

    // Update the physics model for each entity
    private void updateObjects(float dt) {

        for (Entity e : entities) {
            e.update(dt);
        }

    }

    // GETTERS
    public int getNumPredators() { return numPred; }
    public int getNumPrey() { return numPrey; }
    public int getGraceCount() { return graceGen > 0 ? graceGen : 0; }
    public Array<Entity> getEntities() { return entities; }

    // If either one is prey and the other is pred return true
    public static boolean onePreyOnePred(Entity e1, Entity e2) { 
        return (e1 instanceof Predator && e2 instanceof Prey) || (e1 instanceof Prey && e2 instanceof Predator);
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
            else if (pos.x+e.getRadius() > MainWindow.GAME_MAX_RIGHT) {
                pos.set(MainWindow.GAME_MAX_RIGHT-e.getRadius(), pos.y);
            }
            if (pos.y+e.getRadius() > MainWindow.GAME_MAX_TOP) {
                pos.set(pos.x, MainWindow.GAME_MAX_TOP-e.getRadius());
            }
            else if (pos.y-e.getRadius() < MainWindow.GAME_MAX_BOTTOM) {
                pos.set(pos.x, MainWindow.GAME_MAX_BOTTOM+e.getRadius());
            }

        }

    }



}
