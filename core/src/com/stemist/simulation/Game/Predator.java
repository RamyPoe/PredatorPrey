package com.stemist.simulation.Game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.stemist.simulation.MainWindow;
import com.stemist.simulation.Physics.Entity;
import com.stemist.simulation.Physics.PhysicsWorld;
import com.stemist.simulation.Physics.Rays;
public class Predator extends Entity {
 
    // Digestion timer
    private long digestTimer;

    // Split energy
    private float splitEnergy = 0;

    // Inherit position and radius 
    public Predator(Vector2 position) {
        super(position); 

        // Predator color
        this.color = Color.RED;

        // Rays for predators
        rays = new Rays(50, MainWindow.ENTITY_NUM_RAYS, 2500);
    }

    // Check for reproduction
    public void checkSplit(Array<Entity> e) {
        if (PhysicsWorld.numPred > MainWindow.MAX_PREDATORS) { return; }

        if (energy > 0 && splitEnergy >= MainWindow.SPLIT_ENERGY_THRESHOLD) {

            // Reset split energy
            splitEnergy = 0;

            // Split
            split(e);
            
        }
    }

    // Split
    public void split(Array<Entity> e) {
        if (PhysicsWorld.numPred > MainWindow.MAX_PREDATORS) { return; }

        // Get spawn pos
        Vector2 newPos = new Vector2(MainWindow.ENTITY_RADIUS, 0);
        newPos.setAngleDeg((float) Math.random()*360f);
        newPos.add(position);

        Predator p = new Predator(newPos);
        e.add(p);
        PhysicsWorld.numPred++;

        // Mutate brain
        p.brain = this.brain.copy().randomMutate();

        // Debug
        System.out.println("NODES: " + (p.brain.numNodes()-p.brain.numInputs()) + "   |   CONNS: " + p.brain.numConnections());
    }

    // Lose energy based on velocity
    @Override
    public void changeEnergy(float dt) {
        energy -= MainWindow.IDLE_ENERGY_DEPLETION * dt + getVelMagnitude(dt)/MainWindow.ENTITY_MAX_VEL * MainWindow.VEL_ENERGY_DEPLETION * dt;
        splitEnergy -= MainWindow.SPLIT_ENERGY_DEPLETION * dt;
        if (splitEnergy < 0) { splitEnergy = 0; }
    }

    // When making kill
    public void madeKill() {
        if (digesting()) { return; }

        energy += MainWindow.KILL_ENERGY_GAIN;
        energy = Math.min(energy, MainWindow.ENTITY_MAX_ENERGY);
        splitEnergy += MainWindow.KILL_SPLIT_GAIN;
        startDigesting();
    }

    // Check if still digesting
    public boolean digesting() {
        return MainWindow.getTimeMs()-digestTimer < MainWindow.DIGESTION_TIME_MS;
    }

    // Reset digestion
    public void startDigesting() {
        digestTimer = MainWindow.getTimeMs();
    }

}
