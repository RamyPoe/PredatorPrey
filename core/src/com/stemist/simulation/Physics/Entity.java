package com.stemist.simulation.Physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.stemist.simulation.MainWindow;
import com.stemist.simulation.Ai.RtNeat;

public class Entity extends VerletObject {

    // Entity energy
    protected float energy;

    // For drawing and collisions
    private float radius;
    private float radiusSqrd;

    // Can only apply velocity on this axis
    private float angle;

    // Color for drawing
    protected Color color;

    // For applying velocity
    private Vector2 v;

    // For network inputs
    protected Rays rays;

    // Neural Network
    protected RtNeat brain;
    public boolean brainEnabled = true;

    // Constructor
    public Entity(Vector2 position) {
        super(position);

        // Starting energy
        energy = MainWindow.ENTITY_MAX_ENERGY;

        // Set radius
        this.radius = MainWindow.ENTITY_RADIUS;
        this.radiusSqrd = this.radius * this.radius;

        // Face random direction
        angle = (float) Math.random()*360f;

        // For setting velocity
        v = new Vector2(0, 0);

        // Create brain
        brain = new RtNeat(MainWindow.ENTITY_NUM_RAYS+1, 2).randomMutate();
        
    }

    /* ========================== */
    /*       AABB COLLISION       */
    /* ========================== */

    public float getAabbLeft() { return position.x-radius; }
    public float getAabbRight() { return position.x+radius; }
    public float getAabbBottom() { return position.y-radius; }
    public float getAabbTop() { return position.y+radius; }

    public boolean isAabbCollide(Entity e) {
        return (getAabbLeft() < e.getAabbRight() &&
                getAabbRight() > e.getAabbLeft() &&
                getAabbBottom() < e.getAabbTop() &&
                getAabbTop() > e.getAabbBottom());
    }
    public boolean isAabbCollide(float x, float y) {
        return (getAabbLeft() < x   &&
                getAabbRight() > x  &&
                getAabbBottom() < y &&
                getAabbTop() > y );
    }


    /* ========================== */
    /* ========================== */


    // Get this entites gen
    public int getGeneration() { return brain.getGeneration(); }

    // Get neural net output
    public float[] brainForward(float[] inputs) { return brain.forward(inputs); }

    // Get the rays instance
    public Rays getRays() { return rays; }    
    
    // Changing angle
    public void setAngle(float angle) { this.angle = angle; }
    public float getAngle() { return angle; }

    // Change the angle from percentage of max
    public void changeAngle(float angle, float delta) {
        this.angle += angle * delta;
        angle = angle % 360;
    }

    // Set velocity from percentage of max
    public void setVelocity(float vel, float dt) {

        // Can't move without energy
        if (energy <= 0) {
            energy = 0;
            vel = 0;
        }

        v.set(Math.abs(vel), 0);
        v.setAngleDeg(angle + (vel < 0 ? 180 : 0));
        setVelocity(v, dt);
    }

    // To be overriden
    public void changeEnergy(float dt) {}

    // Get color for drawing
    public Color getColor() { return color; }

    // Get position
    public float getX() { return position.x; }
    public float getY() { return position.y; }

    // Get radius
    public float getRadius() { return radius; }
    public float getRadiusSqrd() { return radiusSqrd; }

    // Get current energy
    public float getEnergy() { return energy; }

}
