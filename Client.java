import java.io.*;
import java.net.*;
import java.security.*;
import java.math.BigInteger;

public class Client {

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 8888;

        try (Socket socket = new Socket(serverAddress, port);
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())) {

            // Menerima ukuran file
            int fileSize = dataInputStream.readInt();
            byte[] receivedFileContent = new byte[fileSize];

            // Menerima isi file
            dataInputStream.readFully(receivedFileContent);
            System.out.println("File diterima. Ukuran: " + fileSize + " bytes.");

            // Menerima nilai hash
            String receivedHash = dataInputStream.readUTF();
            System.out.println("Hash diterima: " + receivedHash);

            // Menggunakan MD5 untuk menghitung nilai hash dari file yang diterima
            String calculatedHash = calculateMD5(receivedFileContent);

            // Membandingkan nilai hash
            if (calculatedHash.equals(receivedHash)) {
                System.out.println("File integritas terjamin. File asli.");
            } else {
                System.out.println("Perubahan terdeteksi. File tidak dapat diandalkan.");
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String calculateMD5(byte[] fileContent) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(fileContent);

        // Convert ke format hexadecimal
        BigInteger number = new BigInteger(1, hashBytes);
        String hash = number.toString(16);

        // Padding nol jika perlu
        while (hash.length() < 32) {
            hash = "0" + hash;
        }
        return hash;
    }
}
