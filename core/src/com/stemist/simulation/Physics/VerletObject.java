package com.stemist.simulation.Physics;

import com.badlogic.gdx.math.Vector2;

public class VerletObject {
    
    // Properties
    private Vector2 displacement;
    protected Vector2 position;
    protected Vector2 position_last;
    protected Vector2 acceleration;

    // Constructor
    public VerletObject(Vector2 position) {
        // Initial values
        this.position = new Vector2(position);
        this.position_last = new Vector2(position);
        this.acceleration = new Vector2(0, 0);
        this.displacement = new Vector2(0, 0);

    }

    // Update position
    public void update(float dt) {
        // Get displacement
        displacement.set(position).sub(position_last);

        // Update from last
        position_last.set(position);
        position.add(displacement).mulAdd(acceleration, dt*dt);

        // Reset acceleration
        acceleration.set(0, 0);
    }

    // Accelerate object
    public void accelerate(Vector2 a) {
        acceleration.add(a);
    }

    // Get current velocity
    public Vector2 getVelocity(float dt) {
        Vector2 vel = new Vector2(position);
        vel.sub(position_last).scl(1f/dt);

        return vel;
    }

    // Get magnitude of velocity
    public float getVelMagnitude(float dt) {
        Vector2 vel = getVelocity(dt);
        return (float) Math.sqrt(vel.x * vel.x + vel.y * vel.y);
    }

    // Set the velocity
    protected void setVelocity(Vector2 v, float dt) {
        position_last.set(position).sub(v.scl(dt));
    }

    // Add to the velocity
    public void addVelocity(Vector2 v, float dt) {
        position_last.sub(v.scl(dt));
    }

    // To allow solver to change
    public Vector2 getPositionVector() {
        return position;
    }

}
