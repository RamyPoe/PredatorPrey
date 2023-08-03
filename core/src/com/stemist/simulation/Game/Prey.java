package com.stemist.simulation.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.stemist.simulation.MainWindow;
import com.stemist.simulation.Physics.Entity;
import com.stemist.simulation.Physics.Rays;

public class Prey extends Entity {
    
    // Split timer
    private float splitTimer;

    // Inherit position and radius 
    public Prey(Vector2 position) {
        super(position); 

        // Prey color
        this.color = Color.GREEN;

        // Rays for preys
        rays = new Rays(MainWindow.PREY_FOV, MainWindow.ENTITY_NUM_RAYS, MainWindow.PREY_SIGHT_RANGE);

        // Random offset to timer
        splitTimer = MainWindow.SPLIT_TIME_MS + (int) (Math.random() * 200f);
    }

    // Check for reproduction
    public boolean checkSplit() {
        if (!brainEnabled) { return false; }
        return (energy > 0 && splitTimer < 0);
    }

    // Returns child
    public Prey split() {

        // Reset timer
        splitTimer = MainWindow.SPLIT_TIME_MS;

        // Spawn at random offset from parent
        Vector2 newPos = new Vector2(MainWindow.ENTITY_RADIUS, 0);
        newPos.setAngleDeg((float) Math.random()*360f);
        newPos.add(position);
        
        // Mutate brain
        Prey p = new Prey(newPos);
        p.brain = this.brain.copy().randomMutate();

        // Return new entity
        return p;
    }

    // Lose energy based on velocity or gain when still
    @Override
    public void changeEnergy(float dt) {
        float vel = getVelMagnitude(dt);
        if (vel == 0) { energy += MainWindow.IDLE_ENERGY_GAIN * dt; }
        else { energy -= getVelMagnitude(dt)/MainWindow.ENTITY_MAX_VEL * MainWindow.VEL_ENERGY_DEPLETION * dt; }
        splitTimer -= dt*1000f;
    }


}
