import java.io.*;
import java.net.*;

public class ServerJoc {


    private ServerSocket serverSocket;

    public ServerJoc(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    private static void restartPlaca(){
        File file = new File("placa.txt");
        if (file.exists()) {
            try {
                // Close any streams associated with the file
                FileInputStream fileInputStream = new FileInputStream(file);
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Delete the file
            if (file.delete()) {
                System.out.println("SERVER:Placa a fost restartata!");
            } else {
                file.deleteOnExit();
                System.out.println("SERVER:Failed to delete the matrix file.");
            }
        } else {
            System.out.println("SERVER:Matrix file does not exist.");
        }
    }

    public void startServer(){

        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("Un nou client s-a conectat!");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch (IOException e){
            closeServerSocket();
        }


    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        restartPlaca();
        System.out.println("Serverul este pornit!");
        ServerSocket serverSocket = new ServerSocket(55000);
        ServerJoc server = new ServerJoc(serverSocket);
        server.startServer();
    }

}