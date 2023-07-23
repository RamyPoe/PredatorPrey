package com.stemist.simulation.Ai;

public class Connection {

    // Properties
    private Node fromNode;
    private Node toNode;
    private float weight;
    private boolean enabled = true;

    // Constructor
    public Connection(Node fromNode, Node toNode, float weight) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.weight = weight;
    }

    // Change weight
    public void setWeight(float w) { weight = w; }
    public float getWeight() { return weight; }

    // Change enabled
    public void disable() { enabled = false; }
    public boolean isEnabled() { return enabled; }

    // Change fromNode
    public void setFromNode(Node n) { fromNode = n; }
    public Node getFromNode() { return fromNode; }

    // Change toNode
    public void setToNode(Node n) { toNode = n; }
    public Node getToNode() { return toNode; }

}
