package com.stemist.simulation.Physics;


import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.stemist.simulation.MainWindow;
import com.stemist.simulation.Game.Predator;
import com.stemist.simulation.Game.Prey;

public class PhysicsWorld {
    
    // How "squishy" collisions are
    private final float RESPONSE_COEf = 0.2f;

    // How many threads
    private final int NUM_THREADS = 2;
    
    // List of entities
    private Array<Entity> entities;

    // For spatial hash grid
    private Array<Entity>[] buckets;
    private Set<Integer> uniqueVals;

    // For responses and game ticks
    private PhysicsTick physicsTick;

    // Multi threading
    ExecutorService executor;
    class PhysicsThread implements Runnable {
        public int i1, i2;
        public boolean done = false;
        PhysicsWorld pWorld = null;

        public PhysicsThread(PhysicsWorld pWorld) {
            this.pWorld = pWorld;
        }

        @Override
        public void run() {
                try { pWorld.checkRayCast(i1, i2); } catch (Exception e) {}
                done = true;
            }
        }
    
    private PhysicsThread[] physicsThreads;

    // Constructor
    @SuppressWarnings("unchecked")
    public PhysicsWorld(PhysicsTick physicsTick) {
        this.physicsTick = physicsTick;

        // Create array
        entities = new Array<>();

        // Create buckets
        buckets = new Array[MainWindow.COLS*MainWindow.ROWS];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new Array<Entity>();
        }

        // Helper for avoiding duplicates
        uniqueVals = new HashSet<>();

        // Multi threading
        executor = Executors.newFixedThreadPool(NUM_THREADS);
        physicsThreads = new PhysicsThread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            physicsThreads[i] = new PhysicsThread(this);
        }

    }

    // Add a new entity wihtin count limits
    public void addEntity(Entity e) { entities.add(e); }

    // Remove entity
    private void removeEntityVal(Entity e) { entities.removeValue(e, true); }



    // Solve entity positions
    public void update(float dt) {

        // Count number we have for HUD
        physicsTick.countEntityPredPrey(this);

        
        physicsTick.tickEntities(this, dt);
        physicsTick.updateNeuralEntities(this, dt);
        
        // Add entites to hash grid
        clearBuckets();
        fillBuckets();
        
        checkCollisions(dt);
        
        updateObjects(dt);
        
        // double timer = MainWindow.getTimeMs();
    
        // Use thread pool
        for (int i = 0; i < NUM_THREADS; i++) {
            int step = entities.size/NUM_THREADS-1;
            int i1 = i * step;
            int i2 = (i == NUM_THREADS-1 ? entities.size : (i+1)*step);
            
            physicsThreads[i].i1 = i1;
            physicsThreads[i].i2 = i2;

            executor.execute(physicsThreads[i]);
        }
        

        // Apply world constraints to entities
        applyConstraint();

        // Wait until threads are done
        boolean threadDone = true;
        do {
            threadDone = true;
            for (int i = 0; i < physicsThreads.length; i++) {
                if (!physicsThreads[i].done) { threadDone = false; }
            }
        } while (!threadDone);

        // System.out.println(MainWindow.getTimeMs()-timer);

    }



    // Checks all body and raycast collisions and registers kills
    private void checkCollisions(float dt) {

        // Check collisions in each bucket
        for (int i = 0; i < buckets.length; i++) {

            // Check each entity with every other
            for (int j = 0; j < buckets[i].size; j++) {
                Entity e1 = buckets[i].get(j);
                for (int k = j+1; k < buckets[i].size; k++) {
                    Entity e2 = buckets[i].get(k);
                    
                    // Broad phase collision
                    if (!e1.isAabbCollide(e2)) { continue; }

                    // Get distance between the entities
                    float dist = e1.getPositionVector().dst(e2.getPositionVector());
                    float minDist = e1.getRadius() + e2.getRadius();

                    // Too far to collide
                    if (dist > minDist) { continue; }

                    // Refer to physicsTick for collision
                    int result = physicsTick.onCollision(this, e1, e2);
                    if (result == PhysicsTick.TICK_KILL_1) { removeEntityVal(e1); }
                    else if (result == PhysicsTick.TICK_KILL_2) { removeEntityVal(e2); }

                    // If not killing then collide
                    else {

                        // Normalize delta
                        Vector2 n1 = new Vector2(e1.getPositionVector()).sub(e2.getPositionVector()).nor();
                        Vector2 n2 = new Vector2(n1);

                        // Ratio of movement
                        float ratio1 = e1.getRadius() / minDist;                
                        float ratio2 = e2.getRadius() / minDist;
                        
                        // Apply changes
                        float delta = 0.5f * RESPONSE_COEf * (dist - minDist);
                        e1.getPositionVector().sub(n1.scl(ratio2 * delta));
                        e2.getPositionVector().add(n2.scl(ratio1 * delta));

                    }

                }

            }

        }

    }

    // Clears all the buckets
    private void clearBuckets() {
        for (int i = 0; i < buckets.length; i++) {
            buckets[i].clear();
        }
    }

    // Fills buckets with entites
    private void fillBuckets() {

        // Add each entity
        for (int i = 0; i < entities.size; i++) {

            // Get entity
            Entity e = entities.get(i);

            // Get bucket for each corner
            uniqueVals.clear();
            uniqueVals.add(hashGridBucket(e.getAabbLeft(), e.getAabbTop()));
            uniqueVals.add(hashGridBucket(e.getAabbRight(), e.getAabbTop()));
            uniqueVals.add(hashGridBucket(e.getAabbLeft(), e.getAabbBottom()));
            uniqueVals.add(hashGridBucket(e.getAabbRight(), e.getAabbBottom()));
            
            // Add entity to its buckets
            for (int bucket : uniqueVals) {
                buckets[bucket].add(e);
            }

        }
    }
    
    // Update the physics model for each entity
    private void updateObjects(float dt) {

        for (Entity e : entities) {
            e.update(dt);
        }

    }
    

    // Checks raycast hits for all entites
    public void checkRayCast(int i1, int i2) {
        // Every entity
        for (int i = i1; i < i2; i++) {
            Entity e = entities.get(i);
            e.resetRayCollisionOutArr();
            Rays rays = e instanceof Prey ? Prey.getRays() : Predator.getRays();
            rays.updateResetRays(e);

            // Every ray for this entity
            for (int j = 0; j < rays.getNumRays(); j++) {
                Ray r = rays.getRayArray()[j];

                // How far we have travelled
                float t = 0; Vector2 pos = new Vector2(r.origin);

                // True means going right/up
                boolean xDir = r.dir.x > 0;
                boolean yDir = r.dir.y > 0;

                // Got collision flag
                boolean gotCollision = false;

                // Ray loop
                while (t < r.getMagnitude() && !gotCollision) {
                    // Set position
                    pos.set(r.dir).scl(t).add(r.origin);

                    // Check bounds
                    if (
                        pos.x >= MainWindow.GAME_MAX_RIGHT   ||
                        pos.x <= MainWindow.GAME_MAX_LEFT    ||
                        pos.y >= MainWindow.GAME_MAX_TOP     ||
                        pos.y <= MainWindow.GAME_MAX_BOTTOM
                    ) {
                        break;
                    }

                    // Check current cell for collisions
                    int bucket = hashGridBucket(pos.x, pos.y);
                    for (int k = 0; k < buckets[bucket].size; k++) {
                        Entity test = buckets[bucket].get(k);

                        // Don't collide with yourself or same species
                        if (test == e) { continue; }
                        if ((e instanceof Prey && test instanceof Prey) || (e instanceof Predator && test instanceof Predator)) { continue; }
                        float out = r.checkRayHit(buckets[bucket].get(k));

                        // We got a hit
                        if (out < 1f) {
                            e.getRayCollisionOutArr()[j] = out;
                            gotCollision = true;
                            break;
                        }
                    }

                    // Move to next ray if got hit
                    if (gotCollision) { break; }

                    // Find closest vertical line
                    float lineX = (float) Math.floor(pos.x/MainWindow.CELL_SIZE) + (xDir ? 1f : 0f);
                    lineX *= MainWindow.CELL_SIZE;

                    // Find closest horizontal line
                    float lineY = (float) Math.floor(pos.y/MainWindow.CELL_SIZE) + (yDir ? 1f : 0f);
                    lineY *= MainWindow.CELL_SIZE;

                    // Find distance to border
                    float tDeltaX = (lineX-pos.x)/r.dir.x;
                    float tDeltaY = (lineY-pos.y)/r.dir.y;

                    // Move toward the closer one, add a bit to make sure we don't land exactly on line
                    t += (tDeltaX < tDeltaY ? tDeltaX : tDeltaY) + 0.001f;

                }
            }

        }
    }

    // Returns the hash grid bucket the coordinate falls under
    private int hashGridBucket(float x, float y) {
        int bx = (int) (x / MainWindow.CELL_SIZE); 
        bx = clamp(bx, 0, MainWindow.COLS-1);
        int by = (int) (y / MainWindow.CELL_SIZE);
        by = clamp(by, 0, MainWindow.ROWS-1);
        return by * MainWindow.COLS + bx;
    }

    // Apply world constraints to all entities
    private void applyConstraint() {
        
        // Game box
        for (Entity e : entities) {
            
            // Position
            Vector2 pos = e.getPositionVector();
            
            // Check bounds
            if (pos.x-e.getRadius() < MainWindow.GAME_MAX_LEFT) {
                pos.set(MainWindow.GAME_MAX_LEFT+e.getRadius(), pos.y);
            }
            else if (pos.x+e.getRadius() > MainWindow.GAME_MAX_RIGHT) {
                pos.set(MainWindow.GAME_MAX_RIGHT-e.getRadius(), pos.y);
            }
            if (pos.y+e.getRadius() > MainWindow.GAME_MAX_TOP) {
                pos.set(pos.x, MainWindow.GAME_MAX_TOP-e.getRadius());
            }
            else if (pos.y-e.getRadius() < MainWindow.GAME_MAX_BOTTOM) {
                pos.set(pos.x, MainWindow.GAME_MAX_BOTTOM+e.getRadius());
            }
            
        }

    }

    // Make sure value is within bounds
    public static int clamp(int val, int lower, int upper) { return Math.max(Math.min(val, upper), lower); }
    
    // Get the list of entites
    public Array<Entity> getEntities() { return entities; }
    
    // If either one is prey and the other is pred return true
    public static boolean onePreyOnePred(Entity e1, Entity e2) { 
        return (e1 instanceof Predator && e2 instanceof Prey) || (e1 instanceof Prey && e2 instanceof Predator);
    }
    
    
}
