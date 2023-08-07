package com.stemist.simulation.Game;

import com.badlogic.gdx.math.Vector2;
import com.stemist.simulation.MainWindow;
import com.stemist.simulation.Physics.Entity;
import com.stemist.simulation.Physics.PhysicsTick;
import com.stemist.simulation.Physics.PhysicsWorld;

public class GameWorld implements PhysicsTick {

    // Used for neural net
    private float[] netIn = new float[MainWindow.ENTITY_NUM_RAYS*2+1];
    private float[] netOut;

    // Artifically split predators until inital deaths
    private int gracePeriod = MainWindow.PREDATOR_GRACE_PERIOD;
    private float graceTimer = MainWindow.PREDATOR_GRACE_TIMER;

    // Count of each species
    private int numPrey = 0;
    private int numPred = 0;

    // Initial spawnings
    public void spawnInitial(PhysicsWorld pWorld) {
        Vector2 pos = new Vector2(0, 0);
        for (int i = MainWindow.GAME_MAX_BOTTOM + 50 + MainWindow.ENTITY_RADIUS; i < MainWindow.GAME_MAX_TOP; i += MainWindow.ENTITY_RADIUS*30) {
            for (int j = MainWindow.GAME_MAX_LEFT + 50 + MainWindow.ENTITY_RADIUS; j < MainWindow.GAME_MAX_RIGHT; j += MainWindow.ENTITY_RADIUS*30) {
                pos.set(j, i);
                Entity e = Math.random() < MainWindow.CHANCE_INITIAL_PREY ? new Prey(pos) : new Predator(pos);
                addEntity(pWorld, e);
            }
        }
    }

    // Calculate the number of predators and prey
    @Override
    public void countEntityPredPrey(PhysicsWorld pWorld) {
        numPred = 0; numPrey = 0;
        for (int i = 0; i < pWorld.getEntities().size(); i++) {
            if (pWorld.getEntities().get(i) instanceof Prey) { numPrey++; }
            else if (pWorld.getEntities().get(i) instanceof Predator) { numPred++; }
        }
    }

    // Called for every entity every tick, returns if entity should be removed
    @Override
    public void tickEntities(PhysicsWorld pWorld, float dt) {
        // Reduce grace timer
        graceTimer -= dt*1000f;

        // Avoid the children made
        int size = pWorld.getEntities().size();

        // Tick every entity
        for (int i = 0; i < size;) {
            Entity e = pWorld.getEntities().get(i);
            
            // Update energy and check for predator death
            boolean death = updateEnergy(pWorld, e, dt);
            if (death) {
                numPred--; size--;
                pWorld.getEntities().get(i).setDead();
                pWorld.getEntities().remove(i);
                continue;
            } else { i++; }

            // Random spawn for predator during grace period
            if (e instanceof Predator &&
                gracePeriod > 0 &&
                numPred < MainWindow.PREDATOR_GRACE_DEATH_THRESHOLD &&
                graceTimer <= 0 &&
                Math.random() < e.getEnergy()/MainWindow.ENTITY_MAX_ENERGY
            ) { addEntity(pWorld, ((Predator) e).split()); }
        
        }

        // Reset timer
        if (gracePeriod > 0 && graceTimer <= 0) { graceTimer = MainWindow.PREDATOR_GRACE_TIMER; }

    }

    @Override
    public void updateNeuralEntities(PhysicsWorld pWorld, float dt) {
        for (int i = 0; i < pWorld.getEntities().size(); i++) {
            Entity e = pWorld.getEntities().get(i);
            updateNeuralNetRays(e, dt);
        }
    }

    @Override
    public int onCollision(PhysicsWorld pWorld, Entity e1, Entity e2) {

        // No kill if there are the same species
        if (!PhysicsWorld.onePreyOnePred(e1, e2)) { return PhysicsTick.TICK_NO_KILL; } 

        // Find which is which
        Predator ePred = (Predator) (e1 instanceof Predator ? e1 : e2);
        Prey ePrey = (Prey) (e1 instanceof Prey ? e1 : e2);
        
        // See if prey has hit predator
        float facingSameDir = ePred.getVelocity(1f).angleDeg() - ePrey.getVelocity(1f).angleDeg();
        facingSameDir = Math.abs(facingSameDir);
        if (facingSameDir <= MainWindow.PREY_HIT_ANGLE_WINDOW) {
            // If prey hit predator
            Vector2 angleToPred = new Vector2(ePred.getPositionVector()).sub(ePrey.getPositionVector());
            float sameVel = angleToPred.angleDeg()-ePrey.getVelocity(1f).angleDeg();
            sameVel = Math.abs(sameVel);

            // Prey hit predator
            if (sameVel <= MainWindow.PREY_HIT_ANGLE_WINDOW) {
                ePred.reduceHitpoints();
                if (ePred.isHitpointDead()) {
                    // Remove predator
                    ePred.setDead();
                    return (e1 instanceof Predator ? PhysicsTick.TICK_KILL_1 : PhysicsTick.TICK_KILL_2);
                }
            }
        }

        // Kill has been made
        if (!ePred.digesting()) {

            // Reduce grace period
            gracePeriod--;
            
            // Establish kill for predator
            ePred.madeKill();
            if (ePred.checkSplit()) { addEntity(pWorld, ePred.split()); }

            // Remove prey
            e1.setDead();
            return (e1 instanceof Prey ? PhysicsTick.TICK_KILL_1 : PhysicsTick.TICK_KILL_2);

        }

        // No kill if digesting
        return PhysicsTick.TICK_NO_KILL;


    }

    // Gets the raycast inputs for neural net and apply output
    private void updateNeuralNetRays(Entity e, float dt) {

        /* https://www.desmos.com/calculator/tam8eh34fe */
        for (int j = 0; j < MainWindow.ENTITY_NUM_RAYS; j++) {
            netIn[j*2] = 1f - e.getRayCollisionOutArr()[j];
            netIn[j*2+1] = e.getRayHitEnemyArr()[j] ? -1f : 1f;

            // If ray didn't hit anything then don't bother
            if (netIn[j*2] == 0f)
                netIn[j*2+1] = 0f;
        }

        // Bias neuron
        netIn[netIn.length-1] = 1f;

        // Get output
        netOut = e.brainForward(netIn);

        // Apply output
        if (e.brainEnabled) {
            e.changeAngle(netOut[0] * MainWindow.ENTITY_MAX_ANGLE_VEL, dt);

            // Discourage predator backward movement
            if (e instanceof Predator)
                e.setVelocity((netOut[1] > 0 ? netOut[1] : netOut[1]*0.2f) * MainWindow.ENTITY_MAX_VEL, dt);
            if (e instanceof Prey)
                e.setVelocity(netOut[1] * MainWindow.ENTITY_MAX_VEL, dt);
        }

    }

    // Update energy and check prey split, return true if predator died
    private boolean updateEnergy(PhysicsWorld pWorld, Entity e, float dt) {

        // Make energy change
        e.changeEnergy(dt);

        // Predators can die during grace period if limit is reached
        if (e instanceof Predator) {
            if (e.getEnergy() <= 0) {
                if (gracePeriod > 0 && numPred < MainWindow.PREDATOR_GRACE_DEATH_THRESHOLD-10) { return false; }
                e.setDead();
                return true;
            }
        } else {

            // Prey
            Prey eTemp = (Prey) e;

            // Check for split
            if (eTemp.checkSplit()) { addEntity(pWorld, eTemp.split()); }

        }

        // No death
        return false;
    }

    // Add entity to world
    private void addEntity(PhysicsWorld pWorld, Entity e) {
        if (e instanceof Prey) {
            if (numPrey >= MainWindow.MAX_PREY) { return; }
            numPrey++;
        } else {
            if (numPred >= MainWindow.MAX_PREDATORS) { return; }
            numPred++;
        }
        pWorld.addEntity(e);
    }

    // GETTERS
    public int getNumPredators() { return numPred; }
    public int getNumPrey() { return numPrey; }
    public int getGraceCount() { return gracePeriod > 0 ? gracePeriod : 0; }
    
}
