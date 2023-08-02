package com.stemist.simulation.Physics;

import com.badlogic.gdx.math.Vector2;

public class Rays {

    // How rays are distributed
    private float fov;
    private int numRays;

    // How far it can detect
    private float distance;

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
    }

    // Reset to allow for check again
    public void resetRays() {
        for (int i = 0; i < rayCollisions.length; i++) {
            rayCollisions[i] = 1f;
        }
    }

    // Update list based on possible collision
    public void checkRaysHits(Entity self, Entity e) {

        // Setup for loop
        float startAngle = self.getAngle() - fov/2;
        float stepAngle = fov/(numRays-1);

        // For checking ray hits
        Ray r = new Ray();

        // Loop through all rays
        for (int i = 0; i < numRays; i++) {

            // Get angle for this ray
            float angle = (startAngle + i*stepAngle) % 360;

            // Setup this ray
            r.origin.set(self.getPositionVector());
            r.end.set(distance, 0);
            r.end.setAngleDeg(angle);
            r.end.add(r.origin);

            // Check for collision
            float distance = checkRayHit(r, e);
            
            // Check for min for this ray
            if (rayCollisions[i] > distance) { rayCollisions[i] = distance; }

        }

    }

    // Check one ray and return percent along ray it hit
    // https://math.stackexchange.com/questions/311921/get-location-of-vector-circle-intersection
    private float checkRayHit(Ray r, Entity e) {

        // Quadratic formula setup
        float dx1 = r.end.x-r.origin.x;
        float dx2 = r.origin.x-e.getX();
        float dy1 = r.end.y-r.origin.y;
        float dy2 = r.origin.y-e.getY();

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

    // GETTERS
    public int getNumRays() { return numRays; }
    public float getFov() { return fov; }
    public float getRayDistance() { return distance; }
    public float getRayCollisions(int index) { return rayCollisions[index]; }


}


// Ray Struct
class Ray {

    // Starting point
    public Vector2 origin; 

    // Magnitude determines length
    public Vector2 end;

    // Constructor
    public Ray() {
        origin = new Vector2(0, 0);
        end = new Vector2(0, 0);
    }

}