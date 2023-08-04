package com.stemist.simulation.Physics;

import com.badlogic.gdx.math.Vector2;

public class Rays {

    // How rays are distributed
    private float fov;
    private int numRays;
    private float distance;

    // Hold all the rays
    private Ray[] rays;

    // Length to nearest collision for each ray
    private float[] rayCollisions;

    // Constructor
    public Rays (float fov, int numRays, float distance) {
        this.fov = fov;
        this.numRays = numRays;
        this.distance = distance;

        // Not enough rays
        if (numRays < 2) { throw new Error("CANNOT HAVE LESS THAN 2 RAYS!"); }

        // For output
        rayCollisions = new float[numRays];

        // For raycasting
        rays = new Ray[numRays];
        for (int i = 0; i < numRays; i++) { rays[i] = new Ray(distance); }
    }

    // Reset to allow for check again while updating the rays' positions and angles
    public void updateResetRays(Entity e) {

        // Setup for loop
        float startAngle = e.getAngle() - fov/2;
        float stepAngle = fov/(numRays-1);

        // Loop through all rays
        for (int i = 0; i < numRays; i++) {
            
            // Reset ray output
            rayCollisions[i] = 1f;

            // Get angle for this ray
            float angle = (startAngle + i*stepAngle) % 360;

            // Update current ray
            Ray r = rays[i];
            r.origin.set(e.getPositionVector());
            r.dir.set(1f, 0);
            r.dir.setAngleDeg(angle);
            r.dir.nor();
        }
    }

    

    // GETTERS
    public int getNumRays() { return numRays; }
    public float getFov() { return fov; }
    public float getRayDistance() { return distance; }
    public float getRayCollisionsOutput(int index) { return rayCollisions[index]; }
    public void setRayCollisionsOutput(int index, float n) { rayCollisions[index] = n; }
    public Ray[] getRayArray() { return rays; }


}


// Ray Struct
class Ray {

    // Starting point
    public Vector2 origin;

    // Normalized direction
    public Vector2 dir;

    // Magnitude determines length
    private float magnitude;

    // Constructor
    public Ray(float magnitude) {
        origin = new Vector2(0, 0);
        dir = new Vector2(0, 0);
        this.magnitude = magnitude;
    }

    // Get the magnitude
    public float getMagnitude() { return magnitude; }

    // Check ray and return percent along ray it hit
    // https://math.stackexchange.com/questions/311921/get-location-of-vector-circle-intersection
    public float checkRayHit(Entity e) {

        // Quadratic formula setup
        float endx = origin.x + getMagnitude() * dir.x;
        float endy = origin.y + getMagnitude() * dir.y;

        float dx1 = endx-origin.x;
        float dx2 = origin.x-e.getX();
        float dy1 = endy-origin.y;
        float dy2 = origin.y-e.getY();

        float a = (float) Math.pow(dx1, 2) + (float) Math.pow(dy1, 2);
        float b = 2 * (dx1) * (dx2) + 2 * (dy1) * (dy2);
        float c = (float) Math.pow(dx2, 2) + (float) Math.pow(dy2, 2) - (e.getRadiusSqrd());

        // Apply discriminant
        float t = (b*b) - 4*a*c;
        
        // No solutions/collision
        if (t < 0) { return 1f; }

        // Calculate rest of quadratic
        t = (float) -Math.sqrt(t) - b;
        t = t / (2*a);

        // 0 < t < 1
        return t < 0 ? 1f : t;

    }

}