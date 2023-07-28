package com.stemist.simulation.Ai;

import java.util.*;

public class RtNeat {
    
    // Define inputs and outputs
    private int inputs;
    private int outputs;

    // Input nodes
    private Node[] inputNodes;
    private Node[] outputNodes;

    // Output array
    private float[] outputArr;

    // List of all nodes
    private ArrayList<Node> allNodes;
    private ArrayList<Connection> allConnections;

    // Constructor
    public RtNeat(int inputs, int outputs) {
        this.inputs = inputs;
        this.outputs = outputs;

        // Create array lists
        allNodes = new ArrayList<>();
        allConnections = new ArrayList<>();

        // Create arrays for inputs and outputs
        inputNodes = new Node[inputs];
        outputNodes = new Node[outputs];

        // Array for returning output
        outputArr = new float[outputs];

        // Fill input nodes
        for (int i = 0; i < inputs; i++) {
            Node n = new Node(0.01f);
            allNodes.add(n);
            inputNodes[i] = n;
        }
        
        // Fill output nodes
        for (int i = 0; i < outputs; i++) {
            Node n = new Node(0.99f);
            allNodes.add(n);
            outputNodes[i] = n;
        }

    }

    // Pass input and get output from network
    public float[] forward(float[] input) {

        // Set the input nodes
        for (int i = 0; i < inputs; i++) {
            inputNodes[i].setVal(input[i]);
        }

        // Sort by xLayer
        allNodes.sort(new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                return Float.compare(n1.getLayerX(), n2.getLayerX());
            }
        });

        // Iterate
        for (int i = 0; i < allNodes.size(); i++) {
            allNodes.get(i).calcVal();

            // If not output node then activate
            if (i < allNodes.size()-outputs) {
                // allNodes.get(i).activateSigmabs();
            }
        }

        // Fill output array
        for (int i = 0; i < outputNodes.length; i++) {
            outputArr[i] = outputNodes[i].activateSigmabs().getVal();
        }
        
        // Return output
        return outputArr;

    }

    // Delegates the different mutations
    public RtNeat randomMutate() {

        if (Math.random() < NeatConstants.WEIGHT_MUTATION_CHANCE) {
            boolean success = mutateWeights();
            if (!success) { mutateConnection(); }
        } else if (Math.random() < NeatConstants.NODE_MUTATION_CHANCE) {
            boolean success = mutateNode();
            if (!success) { mutateConnection(); }
        } else {
            mutateConnection();
        }

        // Return self
        return this;

    }

    // Randomly changes one of the weights
    private boolean mutateWeights() {

        // See if there are connections to mutate
        if (allConnections.size() == 0) { return false; }

        // Choose random connection
        int index = (int) (allConnections.size() * Math.random());

        // Change val
        if (Math.random() < NeatConstants.PROBABILITY_PERTURBING) {
            allConnections.get(index).setWeight(
                allConnections.get(index).getWeight() * NeatConstants.getRandomWeight()
            ); 
        } else {
            allConnections.get(index).setWeight(NeatConstants.getRandomWeight());
        }

        // Successful
        return true;

    }


    // Mutate a connection between nodes
    private void mutateConnection() {

        // Choose two nodes
        Node n1, n2;
        do {
            n1 = allNodes.get( (int) (Math.random() * allNodes.size() ));
            n2 = allNodes.get( (int) (Math.random() * allNodes.size() ));
        } while (n1.getLayerX() == n2.getLayerX());

        // Create the connection and add to node
        Connection con;
        if (n1.getLayerX() > n2.getLayerX()) {
            con = new Connection(n2, n1, NeatConstants.getRandomWeight());
            n1.addConnection(con);
        } else {
            con = new Connection(n1, n2, NeatConstants.getRandomWeight());
            n2.addConnection(con);
        }

        // Add connection to list of all
        allConnections.add(con);

    }

    // Mutate a Node in a connection
    private boolean mutateNode() {

        // See if there are connections to mutate
        if (allConnections.size() == 0) { return false; }

        // Choose a random connection
        int index = (int) (Math.random() * allConnections.size());
        Connection c = allConnections.get(index);

        // Disable this connection
        c.disable();

        // Create node
        Node n = new Node(Node.randomRange(
            c.getFromNode().getLayerX(),
            c.getToNode().getLayerX()
        ));

        // Add node to list
        allNodes.add(n);

        // A connection coming from
        Connection c1 = new Connection(c.getFromNode(), n, 1f);
        n.addConnection(c1);
        allConnections.add(c1);
        
        // A connection going out
        Connection c2 = new Connection(n, c.getToNode(), NeatConstants.getRandomWeight());
        c.getToNode().addConnection(c2);
        allConnections.add(c2);

        // Successful
        return true;

    }

    // Get the number of inputs
    public int numInputs() { return inputs; }

    // Number of nodes
    public int numNodes() { return allNodes.size(); }

    // Number of connections
    public int numConnections() { return allConnections.size(); }

    // Return copy for child
    public RtNeat copy() {

        // Copy instance to be filled
        RtNeat cpy = new RtNeat(this.inputs, this.outputArr.length);

        // Clear the array
        cpy.allNodes.clear();

        // One pass for creating all nodes
        for (int i = 0; i < allNodes.size(); i++) {
            Node n = new Node(allNodes.get(i).getLayerX());
            cpy.allNodes.add(n); 
        }

        // Add input nodes in array
        for (int i = 0; i < inputs; i++) {
            int index = allNodes.indexOf(inputNodes[i]);
            cpy.inputNodes[i] = cpy.allNodes.get(index);
        }
        
        // Add output nodes to array
        for (int i = 0; i < outputArr.length; i++) {
            int index = allNodes.indexOf(outputNodes[i]);
            cpy.outputNodes[i] = cpy.allNodes.get(index);
        }
        

        // One pass for adding all connections
        for (int i = 0; i < allConnections.size(); i++) {
            // Connection we want to mirror
            Connection c = allConnections.get(i);
            
            // Indexes of nodes involved
            int i1 = allNodes.indexOf( c.getFromNode() );
            int i2 = allNodes.indexOf( c.getToNode() );

            // Make new connection
            Connection con = new Connection(cpy.allNodes.get(i1), cpy.allNodes.get(i2), c.getWeight());

            // Check if enabled
            if (!c.isEnabled()) { con.disable(); }

            // Add to list
            cpy.allConnections.add(con);
        }

        // One pass for adding the connections for each node
        for (int i = 0; i < allNodes.size(); i++) {
            ArrayList<Connection> cons = allNodes.get(i).getConnections();
            for (int j = 0; j < cons.size(); j++) {
                int index = allConnections.indexOf(cons.get(j));
                cpy.allNodes.get(i).addConnection(cpy.allConnections.get(index));
            }

        }

        // Return the copy
        return cpy;

    }

}
