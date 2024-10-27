import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            System.out.println("Server is listening on port 8888...");

            // Accept a connection from the client
            try (Socket socket = serverSocket.accept()) {
                System.out.println("Client connected.");

                // Path file yang akan dikirim
                Path filePath = Paths.get("sample.txt");
                byte[] fileContent = Files.readAllBytes(filePath);

                // Menggunakan MD5 untuk menghitung nilai hash
                String hash = calculateMD5(fileContent);

                // Mengirim file dan nilai hash ke client
                sendFileAndHash(socket, fileContent, hash);
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

    private static void sendFileAndHash(Socket socket, byte[] fileContent, String hash) {
        try (OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {

            // Mengirim ukuran file
            dataOutputStream.writeInt(fileContent.length);

            // Mengirim isi file
            dataOutputStream.write(fileContent);

            // Mengirim nilai hash
            dataOutputStream.writeUTF(hash);

            System.out.println("File and hash sent to client.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
