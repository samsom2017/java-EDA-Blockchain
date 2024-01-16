package com.company;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.util.HashMap;

import java.util.Map;


// A class that represents a hospital that can admit patients and communicate with a broker
class Hospital {
    // A name for the hospital
    protected String name;
    // A socket for communicating with the broker
    private Socket socket;
    // A map of patient IDs and names
    protected Map<String, String> patients;
    // A constructor that takes the name, IP address and port number of the broker as parameters
    public Hospital(String name, String ip, int port) throws IOException {
        this.name = name;
        socket = new Socket(ip, port);
        patients = new HashMap<>();
        // Start a new thread to listen for incoming messages from the broker
        new Thread(new Listener()).start();

        System.out.println(name + " started");
    }

    // A method that admits a patient and sends its information to the broker
    public void admitPatient(String id, String name) throws IOException {
        System.out.println("Test admitPat Hospita ");
        // Add the patient to the map
        patients.put(id, name);
        System.out.println(this.name + " admitted patient: " + id + " - " + name);
        // Send the patient's ID and name to the broker using the socket's output stream
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println(id + "," + name);
        out.write(id +""+ name + patients.size());
    }

    // A class that implements Runnable interface to listen for incoming messages from the broker
    class Listener implements Runnable {
        private boolean name;

        @Override
        public void run() {
            try {
                // Get the socket's input stream and wrap it in a buffered reader
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message;
                // Loop until the socket is closed or an exception occurs
                while ((message = in.readLine()) != null) {
                    // Parse the message as a patient ID and data by splitting it by commas
                    String[] parts = message.split(",");
                    String id = parts[0];
                    String data = parts[1];
                    // Check if the patient is in

// Check if the patient is in the map
                    if (patients.containsKey(id)) {
                        // Print the received data for the patient

                        System.out.println(true + " received data for patient: " + id + " - " + patients.get(id) + ": " + data);
                    } else {
                        // Unknown patient
                        System.out.println(this.name + " received data for unknown patient: " + id);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


