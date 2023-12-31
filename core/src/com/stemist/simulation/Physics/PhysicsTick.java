package com.stemist.simulation.Physics;

public interface PhysicsTick {
    
    // Communication Constants
    public static final int TICK_NO_KILL = 0;
    public static final int TICK_KILL_1 = 1;
    public static final int TICK_KILL_2 = 2;

    // Methods
    public void countEntityPredPrey(PhysicsWorld pWorld);
    public void tickEntities(PhysicsWorld pWorld, float dt);
    public void updateNeuralEntities(PhysicsWorld pWorld, float dt);
    public int onCollision(PhysicsWorld pWorld, Entity e1, Entity e2);

}
