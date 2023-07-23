package com.stemist.simulation.Game;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.stemist.simulation.MainWindow;
import com.stemist.simulation.Physics.Entity;
import com.stemist.simulation.Physics.PhysicsWorld;
import com.stemist.simulation.Physics.Rays;

public class Prey extends Entity {
    
    // Split timer
    private long splitTimer;

    // Inherit position and radius 
    public Prey(Vector2 position) {
        super(position); 

        // Prey color
        this.color = Color.GREEN;

        // Rays for preys
        rays = new Rays(270, MainWindow.ENTITY_NUM_RAYS, 600);

        // Random offset to timer
        splitTimer = MainWindow.getTimeMs() + (long) (Math.random() * 100);
    }

    // Check for reproduction
    public void checkSplit(Array<Entity> e) {
        if (PhysicsWorld.numPrey > MainWindow.MAX_PREY) { return; }

        if (energy > 0 && MainWindow.getTimeMs()-splitTimer > MainWindow.SPLIT_TIME_MS) {

            // Reset split timer
            splitTimer = MainWindow.getTimeMs();

            Vector2 newPos = new Vector2(MainWindow.ENTITY_RADIUS, 0);
            newPos.setAngleDeg((float) Math.random()*360f);
            newPos.add(position);

            Prey p = new Prey(newPos);
            e.add(p);
            PhysicsWorld.numPrey++;

            // Mutate brain
            p.brain = this.brain.copy().randomMutate();
        }
    }

    // Lose energy based on velocity or gain when still
    @Override
    public void changeEnergy(float dt) {
        float vel = getVelMagnitude(dt);
        if (vel == 0) { energy += MainWindow.IDLE_ENERGY_GAIN * dt; }
        else { energy -= getVelMagnitude(dt)/MainWindow.ENTITY_MAX_VEL * MainWindow.VEL_ENERGY_DEPLETION * dt; }
    }


}
