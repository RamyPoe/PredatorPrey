package com.stemist.simulation.Physics;

import com.badlogic.gdx.math.Vector2;

public class Entity extends VerletObject {

    // For drawing and collisions
    private float radius;

    // Can only apply velocity on this axis
    private float angle;

    // For applying velocity
    private Vector2 v;

    // Constructor
    public Entity(Vector2 position, float radius) {
        super(position);

        // Set radius
        this.radius = radius;

        // Face random direction
        angle = (float) Math.random()*360f;
        //System.out.println(angle);

        // For setting velocity
        v = new Vector2(0, 0);
    }

    // Setting angle
    public void setAngle(float angle) {
        this.angle = angle;
    }

    // Getting current angle
    public float getAngle() {
        return angle;
    }

    // Change the angle
    public void changeAngle(float angle) {
        this.angle += angle;
        angle = angle % 360;
    }

    // Set velocity
    public void setVelocity(float vel, float dt) {
        v.set(vel, 0);
        v.setAngleDeg(angle);
        setVelocity(v, dt);
    }

    // Get position
    public float getX() { return position.x; }
    public float getY() { return position.y; }

    // Get radius
    public float getRadius() { return radius; }

}
