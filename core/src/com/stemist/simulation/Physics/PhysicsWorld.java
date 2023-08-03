package com.stemist.simulation.Physics;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.stemist.simulation.MainWindow;
import com.stemist.simulation.Game.Predator;
import com.stemist.simulation.Game.Prey;

public class PhysicsWorld {
    
    // How "squishy" collisions are
    private final float RESPONSE_COEf = 0.7f;
    
    // List of entities
    private Array<Entity> entities;

    // For spatial hash grid
    private Array<Entity>[] buckets;

    // For responses and game ticks
    private PhysicsTick physicsTick;


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
    }

    // Add a new entity wihtin count limits
    public void addEntity(Entity e) {

        entities.add(e);
    }

    // Remove entity while keeping count
    public void removeEntityIndex(int index) {
        
        entities.removeIndex(index);
    }



    // Solve entity positions
    public void update(float dt) {

        tickEntities(dt);
        checkCollisions(dt);
        updateObjects(dt);
        applyConstraint();
        
    }

    // Apply tick to each entity
    public void tickEntities(float dt) {
        for (int i = 0; i < entities.size;) {
            int death = physicsTick.tick(entities.get(i), dt);
            if (death == PhysicsTick.TICK_KILL_1) { entities.removeIndex(i); continue; }
            i++;
        }
    }


    // Checks all body and raycast collisions and registers kills
    private void checkCollisions(float dt) {
        
        // If entity was removed don't increment loop
        boolean incr_i = true;

        // Check every entity with every other
        for (int i = 0; i < entities.size;) {

            // First entity
            Entity e1 = entities.get(i);

            // Check for ray collisions as well
            Rays e1Rays = e1.getRays();

            // Reset flag
            incr_i = true;

            // Check with every other entity
            for (int j = i+1; j < entities.size;) {
                
                // Second entity
                Entity e2 = entities.get(j);
                
                // Check for ray collisions as well
                Rays e2Rays = e2.getRays();

                
                // Get distance between the entities
                float dist = e1.getPositionVector().dst(e2.getPositionVector());
                float minDist = e1.getRadius() + e2.getRadius();

                // Check ray collisions if applicable
                if (onePreyOnePred(e1, e2)) {

                    // If close enough
                    if (dist < Math.max(MainWindow.PREDATOR_SIGHT_RANGE, MainWindow.PREY_SIGHT_RANGE)) {
                        e1Rays.checkRaysHits(e1, e2);
                        e2Rays.checkRaysHits(e2, e1);
                    }

                }

                // From now on means there was collision
                if (dist > minDist) { j++; continue; }

                // Refer to physicsTick for collision
                int result = physicsTick.onCollision(e1, e2);
                if (result == PhysicsTick.TICK_KILL_1) {
                    removeEntityIndex(i);
                    incr_i = false;
                    j++;
                } else if (result == PhysicsTick.TICK_KILL_2) {
                    removeEntityIndex(j);
                }

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

                    // Increment to next entity
                    j++;
                }

            }

            // Increment next entity
            if (incr_i) i++;

        }
    }

    

    

    // Update the physics model for each entity
    private void updateObjects(float dt) {

        for (Entity e : entities) {
            e.update(dt);
        }

    }

    // Get the list of entites
    public Array<Entity> getEntities() { return entities; }

    // If either one is prey and the other is pred return true
    public static boolean onePreyOnePred(Entity e1, Entity e2) { 
        return (e1 instanceof Predator && e2 instanceof Prey) || (e1 instanceof Prey && e2 instanceof Predator);
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



}
