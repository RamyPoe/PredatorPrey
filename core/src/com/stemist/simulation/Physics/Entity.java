package com.stemist.simulation.Physics;

import com.badlogic.gdx.math.Vector2;

public class Entity extends VerletObject {

    // For drawing and collisions
    private float radius;

    // Constructor
    public Entity(Vector2 position, float radius) {
        super(position);

        // Set radius
        this.radius = radius;
    }

    // Get position
    public float getX() { return position.x; }
    public float getY() { return position.y; }

    // Get radius
    public float getRadius() { return radius; }

}
