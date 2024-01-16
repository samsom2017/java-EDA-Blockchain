package com.company;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


// A class that represents a blockchain event
class BlockchainEvent {
    // The data of the event
    private String data;
    // The timestamp of the event
    private long timestamp;
    // The hash of the previous block
    private String prevHash;
    // The hash of the current block
    private String hash;
    //private Object DatatypeConverter;

    // A constructor that takes the data and the previous hash as parameters
    public BlockchainEvent(String data, String prevHash) {
        this.data = data;
        this.prevHash = prevHash;
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }
      public String bytesToHex(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for(byte b: bytes){
            sb.append(String.format("%20",b));
        }
        return sb.toString();
      }
    // A method that calculates the hash of the current block using SHA-256 algorithm
    public String calculateHash() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String input = data + timestamp + prevHash;
            byte[] output = md.digest(input.getBytes());
            return bytesToHex(output);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
//Hex.encodeHexString() method3.


    // Getters and setters for the fields
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        this.hash = calculateHash();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        this.hash = calculateHash();
    }

    public String getPrevHash() {
        return prevHash;
    }

    public void setPrevHash(String prevHash) {
        this.prevHash = prevHash;
        this.hash = calculateHash();
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }


}
