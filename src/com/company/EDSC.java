package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// A class that represents an event-driven smart contract platform
public class EDSC {
    // A list of blockchain events
    protected List<BlockchainEvent> events;
    // A socket for communicating with other nodes
    private Socket socket;

    public EDSC(List<BlockchainEvent> events, Socket socket) {
       // this.events = events;
        //this.socket = socket;
    }

       public EDSC(String localhost, int i) {
    }

    // A constructor that takes the IP address and port number of another node as parameters
    public void ED(String ip, int port) throws IOException {
        events = new ArrayList<>();


       // Changed the port number to 8888
        socket = new Socket(ip, 7777);

        // Start a new thread to listen for incoming messages from other nodes
        new Thread(new Listener()).start();
        System.out.println("ED started");
    }

    // A method that adds a new event to the blockchain and broadcasts it to other nodes
    // A method that adds a new event to the blockchain and broadcasts it to other nodes
    public void addEvent(String data) throws Exception {
        // Get the hash of the last event or use "0" if there is no event
       // String prevHash = events.isEmpty() ? "0" : events.get(events.size() - 1).getHash();
        String prevHash = events.isEmpty() ? "0" : events.get(events.size() - 1).getHash();

        // Create a new event with the given data and the previous hash
        BlockchainEvent event = new BlockchainEvent(data, prevHash);
        // Add the event to the list
        events.add(event);
        System.out.println("New event added: " + event.getData());
        // Send the event to other nodes using the socket's output stream
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(event.getData() + "," + event.getTimestamp() + "," + event.getPrevHash() + "," + event.getHash());
        }
    }


    // A method that validates an event by checking its hash and previous hash
    public boolean validateEvent(BlockchainEvent event) {

        // Check if the event's hash matches its calculated hash
        if (!event.getHash().equals(event.calculateHash())) {
            System.out.println("Invalid hash");

            return false;
        }
        // Check if the event's previous hash matches the last event's hash or "0" if there is no event
       // String prevHash = events.isEmpty() ? "0" : events.get(events.size() - 1).getHash();
        String prevHash = events.isEmpty() ? "0" : events.get(events.size() - 1).getHash();

        if (!event.getPrevHash().equals(prevHash)) {
            System.out.println("Invalid previous hash");
            return false;
        }

        return true;
    }


    // A method that triggers actions based on the event's data
    public void triggerAction(BlockchainEvent event) {
        // For simplicity, we assume that the event's data is a string that contains a command and an argument separated by a space
        String[] parts = event.getData().split(" ");
        String command = parts[0];
        String argument = parts[1];
        // Switch on the command and perform the corresponding action
        switch (command) {
            case "print":
                // Print the argument to the console
                System.out.println(argument);
                break;
            case "connect":
                // Connect to another node using the argument as the IP address and port number
                try {
                    socket = new Socket(argument.split(":")[0], Integer.parseInt(argument.split(":")[1]));
                    System.out.println("Connected to " + argument);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "disconnect":
                // Disconnect from the current node
                try {
                    socket.close();
                    System.out.println("Disconnected");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                // Unknown command
                System.out.println("Unknown command: " + command);
        }
    }

    // A class that implements Runnable interface to listen for incoming messages from other nodes
    class Listener implements Runnable {
        @Override
        public void run() {
            try {
                // Get the socket's input stream and wrap it in a buffered reader
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message;
                // Loop until the socket is closed or an exception occurs
                while ((message = in.readLine()) != null) {
                    // Parse the message as an event by splitting it by commas
                    String[] parts = message.split(",");
                    String data = parts[0];
                    long timestamp = Long.parseLong(parts[1]);
                    String prevHash = parts[2];
                    String hash = parts[3];
                    // Create a new event with the parsed data
                    BlockchainEvent event = new BlockchainEvent(data, prevHash);
                    event.setTimestamp(timestamp);
                    event.setHash(hash);
                    // Validate the event and add it to the list if valid
                    if (validateEvent(event)) {
                        events.add(event);
                        System.out.println("New event received: " + event.getData());
                        // Trigger an action based on the event's data
                        triggerAction(event);
                    } else {
                        System.out.println("Invalid event received: " + event.getData());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

