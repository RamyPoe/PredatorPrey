package com.stemist.simulation.Physics;
import com.badlogic.gdx.math.Vector2;
public class Predator extends Entity {
    private int energy = 1000; 
    private static final int energyLoss = 5; 
    

    // Inherit position and radius 
    public Predator(Vector2 position, float radius) {
        super(position,radius); 
    }


    // Static energy loss depletion 
    public void deleteEnergy() {
        energy -= energyLoss; 
    }

    // Reset predator energy if it eats a prey.  
    public void gainEnergy() { 
        energy = 1000;  
    }

    // Returns energy. 
    public int getEnergy() {
        return energy;
    }

}
