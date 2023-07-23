package com.stemist.simulation.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.stemist.simulation.MainWindow;
import com.stemist.simulation.Physics.Entity;
import com.stemist.simulation.Physics.PhysicsWorld;
import com.stemist.simulation.Physics.Rays;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

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
        int rate = 1; 
        // Use file reader to get the reproductive rate. 
        File reproductiveFile = new File("reproductiveRate.txt"); 
        if (reproductiveFile.exists()) {
            try {
                BufferedReader buffreader = new BufferedReader(new FileReader(reproductiveFile));
                String reproductionNumber = buffreader.readLine();
                rate = Integer.parseInt(reproductionNumber);
                buffreader.close();
            }
            catch (IOException e2) {
                System.out.println(e2);  
            }
        }
        else {
            ; 
        }
          

        if (PhysicsWorld.numPrey > MainWindow.MAX_PREY) { return; }

        // Based on the number provided this will be the denominator of split time. If the reproductive rate is higher (i.e. 2x) then it will take half as long to reproduce. 
        if (energy > 0 && MainWindow.getTimeMs()-splitTimer > (MainWindow.SPLIT_TIME_MS/rate)) {

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
