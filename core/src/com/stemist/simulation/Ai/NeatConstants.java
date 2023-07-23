package com.stemist.simulation.Ai;

public class NeatConstants {
    
    // Chance weight is changed instead of reassigned
    public static float PROBABILITY_PERTURBING = 0.8f;

    // Chance weight gets mutated
    public static float WEIGHT_MUTATION_CHANCE = 0.4f;

    // If weight doesn't get mutated (should add to 1.0)
    public static float CONNECTION_MUTATION_CHANCE = 0.5f;
    public static float NODE_MUTATION_CHANCE = 0.5f;

    public static float getRandomWeight() {
        return (float) Math.random() * 2f - 1f;
    }

}
