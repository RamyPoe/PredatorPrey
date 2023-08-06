package com.stemist.simulation.Game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.stemist.simulation.MainWindow;
import com.stemist.simulation.Physics.Entity;
import com.stemist.simulation.Physics.Rays;
public class Predator extends Entity {
 
    // For raycasting
    private static Rays rays;
    static {
        // Rays for predators
        rays = new Rays(MainWindow.PREDATOR_FOV, MainWindow.ENTITY_NUM_RAYS, MainWindow.PREDATOR_SIGHT_RANGE);
    }

    // Digestion timer
    private float digestTimer;

    // Split energy
    private float splitEnergy = 0;

    // Inherit position and radius 
    public Predator(Vector2 position) {
        super(position); 

        // Predator color
        this.color = Color.RED;

        // Initial timer
        startDigesting();
    }

    // Check for reproduction
    public boolean checkSplit() {
        if (!brainEnabled) { return false; }
        return (energy > 0 && splitEnergy >= MainWindow.SPLIT_ENERGY_THRESHOLD);
    }
    
    // Returns child
    public Predator split() {

        // Reset split energy
        splitEnergy = 0;
        
        // Spawn at random offset from parent
        Vector2 newPos = new Vector2(MainWindow.ENTITY_RADIUS, 0);
        newPos.setAngleDeg((float) Math.random()*360f);
        newPos.add(position);

        // Mutate brain
        Predator p = new Predator(newPos);
        p.brain = this.brain.copy().randomMutate();
        
        // Return new entity
        return p;
    }

    // Lose energy based on velocity
    @Override
    public void changeEnergy(float dt) {
        energy -= MainWindow.IDLE_ENERGY_DEPLETION * dt + getVelMagnitude(dt)/MainWindow.ENTITY_MAX_VEL * MainWindow.VEL_ENERGY_DEPLETION * dt;
        splitEnergy -= MainWindow.SPLIT_ENERGY_DEPLETION * dt;
        if (splitEnergy < 0) { splitEnergy = 0; }
        digestTimer -= dt*1000f;
    }

    // When making kill
    public void madeKill() {
        energy += MainWindow.KILL_ENERGY_GAIN;
        energy = Math.min(energy, MainWindow.ENTITY_MAX_ENERGY);
        splitEnergy += MainWindow.KILL_SPLIT_GAIN;
        startDigesting();
    }

    // Check if still digesting
    public boolean digesting() {
        return digestTimer > 0;
    }

    // Reset digestion
    private void startDigesting() {
        digestTimer = MainWindow.DIGESTION_TIME_MS;
    }

    // Getting predator rays
    public static Rays getRays() { return rays; }

}
