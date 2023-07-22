package com.stemist.simulation.Physics;
import com.badlogic.gdx.math.Vector2;
public class Prey extends Entity {
    
    private int energy = 1000; 
    private static final int energyLoss = 5; 
    

    public Prey(Vector2 position, float radius) {
        super(position,radius); 
    }

    public void deleteEnergy() {
        energy -= energyLoss; 
    }
    public int getEnergy() {
        return energy; 
    }
    
}
