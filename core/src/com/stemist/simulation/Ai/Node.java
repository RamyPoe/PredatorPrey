package com.stemist.simulation.Ai;

import java.util.ArrayList;

public class Node {

    // Connections coming to this node
    private ArrayList<Connection> connections;

    // Current value
    private float val = 0;

    // For sorting
    private float xLayer;

    // Constructor
    public Node(float xLayer) {
        this.xLayer = xLayer;
        connections = new ArrayList<>();
    }

    // Calculate value
    public void calcVal() {

        // Reset val if not input
        if (connections.size() > 0) { val = 0;}

        // Calc new val
        for (Connection c : connections) {
            if (!c.isEnabled()) { continue; }

            val += c.getFromNode().getVal() * c.getWeight();
        }

    }

    // Sigmoid approx with absolute
    public Node activateSigmabs() {
        val = 2*val / (1 + Math.abs(val));
        return this;
    }

    // Sigmoid activation
    public Node activateSigmoid() {
        val = 1f / (1f + (float) Math.exp(-val));
        return this;
    }

    // Rectified Linear activation
    public Node activateRelu() {
        val = val > 0 ? val : 0f;
        return this;
    }

    // Leaky Rectified Linear activation
    public Node activateRelulk() {
        val = val > 0 ? val : val * 0.02f;
        return this;
    }

    // Generate exclusive random number
    public static float randomRange(float low, float high) {
        // To make sure its exclusive
        low += 0.000001;
        high -= 0.000001;
        float range = high-low;
        return (float) Math.random() * range + low;
    }

    // Add a node
    public void addConnection(Connection c) { connections.add(c); }

    // Change val
    public float getVal() { return val; }
    public void setVal(float v) { val = v; }

    // Get xLayer for sorting
    public float getLayerX() { return xLayer; }

    // Get connections list
    public ArrayList<Connection> getConnections() { return connections; }

    // For debug
    @Override
    public String toString() {
        return "" + this.getLayerX();
    }

}
