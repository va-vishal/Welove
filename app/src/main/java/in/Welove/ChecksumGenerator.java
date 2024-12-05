package in.Welove;

import java.security.MessageDigest;


public class ChecksumGenerator {

    // Method to generate checksum
    public String generateChecksum(String base64Payload, String apiEndPoint, String salt, String saltIndex) {
        try {
            // Concatenate base64Payload, apiEndPoint, and salt to create the data to hash
            String dataToHash = base64Payload + apiEndPoint + salt;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(dataToHash.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b)); // Convert byte to hex
            }
            return hexString.toString() + "###" + saltIndex; // Append salt index
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

