package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// A class that represents a broker that can communicate with multiple hospitals using sockets
public class Broker {

    // A server socket for accepting connections from hospitals
    private ServerSocket  serverSocket;
    // A map of patient IDs and hospital sockets
    private Map<String, Socket> patientHospitalMap;
    // A map of hospital sockets and output streams
    private Map<Socket, PrintWriter> hospitalOutMap;

    // A constructor that takes a port number as a parameter
    public Broker(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        patientHospitalMap = new HashMap<>();
        hospitalOutMap = new HashMap<>();

        System.out.println("Broker started");
    }


    // A method that accepts connections from hospitals and starts a new thread for each one
    public void acceptConnections() throws IOException {
        while (true) {
            // Accept a connection from a hospital and get its socket
            Socket socket = serverSocket.accept();
            System.out.println("New connection from: " + socket.getInetAddress());
            // Create an output stream for the socket and store it in the map
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            hospitalOutMap.put(socket, out);
            // Start a new thread to listen for incoming messages from the hospital
            new Thread(new Listener(socket)).start();
        }
    }

    // A method that sends data about a patient to the admitted hospital
    public void sendData(String id, String data) throws IOException {
        // Check if the patient is in the map
        if (patientHospitalMap.containsKey(id)) {
            // Get the socket of the hospital that admitted the patient
            Socket socket = patientHospitalMap.get(id);
            // Get the output stream for the socket from the map
            PrintWriter out = hospitalOutMap.get(socket);
            // Send the patient's ID and data to the hospital
            out.println(id + "," + data);
            System.out.println("Broker sent data for patient: " + id + ": " + data);
        } else {
            // Unknown patient
            System.out.println("Broker received data for unknown patient: " + id);
        }
    }

    // A class that implements Runnable interface to listen for incoming messages from a hospital
    class Listener implements Runnable {
        // The socket of the hospital
        private Socket socket;

        // A constructor that takes the socket as a parameter
        public Listener(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // Get the socket's input stream and wrap it in a buffered reader
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message;
                // Loop until the socket is closed or an exception occurs
                while ((message = in.readLine()) != null) {
                    // Parse the message as a patient ID and name by splitting it by commas
                    String[] parts = message.split(",");
                    String id = parts[0];
                    String name = parts[1];
                    // Add the patient and the hospital socket to the map
                    patientHospitalMap.put(id, socket);
                    System.out.println("Broker received patient: " + id + " - " + name);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // Create an instance of EDSC and connect to another node on localhost:7777
        EDSC edsc = new EDSC("localhost", 9999);
        // Create an instance of Broker and listen on port 8888
        Broker broker = new Broker(8888);
        // Start a new thread to accept connections from hospitals
        new Thread(() -> {
            try {
                broker.acceptConnections();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }).start();
        // Create two instances of Hospital and connect to the broker on localhost:8888 with different names

        Hospital hospital1 = new Hospital("Hospital 1", "localhost", 8888);
        Hospital hospital2 = new Hospital("Hospital 2", "localhost", 8888);
        // Wait for 2 seconds to establish connections
        Thread.sleep(1000);
        // Add some events to the EDSC with different commands and arguments
        try {
            edsc.addEvent("print Hello world");
        } catch (Exception e) {
            //e.printStackTrace();
        }
        try {
            edsc.addEvent("connect localhost:8888");
        } catch (Exception e) {
           // e.printStackTrace();
        }
        try {
            edsc.addEvent("disconnect");
        } catch (Exception e) {
           // e.printStackTrace();
        }
        try {
            edsc.addEvent("print Goodbye world");
        } catch (Exception e) {
           // e.printStackTrace();
        }
        // Admit some patients to the hospitals and send their information to the broker
        hospital1.admitPatient("P1", "Alice");
        hospital2.admitPatient("P2", "Bob");
        hospital1.admitPatient("P3", "Charlie");
        hospital2.admitPatient("P4", "David");
        // Wait for 2 seconds to send the messages
        Thread.sleep(2000);
        // Send some data about the patients to the broker
        broker.sendData("P1", "Blood pressure: 120/80");
        broker.sendData("P2", "Heart rate: 72 bpm");
        broker.sendData("P3", "Temperature: 37.5 C");
        broker.sendData("P4", "Oxygen level: 98%");

    }
}
/*
Broker received patient: P3 - Charlie
Broker received patient: P4 - David
Broker sent data for patient: P1: Blood pressure: 120/80
Broker sent data for patient: P2: Heart rate: 72 bpm
Broker sent data for patient: P3: Temperature: 37.5 C
Broker sent data for patient: P4: Oxygen level: 98%
false received data for patient: P2 - Bob: Heart rate: 72 bpm
false received data for patient: P1 - Alice: Blood pressure: 120/80
false received data for patient: P4 - David: Oxygen level: 98%
false received data for patient: P3 - Charlie: Temperature: 37.5 C
 */