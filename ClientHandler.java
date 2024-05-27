import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); // Keep track of each client
    private Socket socket;
    private BufferedReader bufferedReader; // To read data from the client
    private BufferedWriter bufferedWriter; // To send data to the client
    private String username;
    private char symbol; // To store the symbol for the client
    private static int clientCount = 0; // Track the number of connected clients

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = bufferedReader.readLine();

            // Assign symbols based on the number of clients connected
            synchronized (ClientHandler.class) {
                clientCount++;
                if (clientCount == 1) {
                    this.symbol = 'X';
                } else if (clientCount == 2) {
                    this.symbol = 'O';
                } else {
                    this.symbol = ' '; // Assign a default or error symbol for more clients
                }
            }

            clientHandlers.add(this);
            bufferedWriter.write("SYMBOL:" + symbol);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            sendMessage("Jucatorul cu username-ul " + username + " (" + symbol + ") s-a conectat!\nJocul incepe! Introduce: pozI+pozJ (ex:03)");
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.username.equals(username)) {
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine(); // Simulate pressing the Enter key to send the message
                    clientHandler.bufferedWriter.flush(); // Flush the buffer
                }
            } catch (IOException e) {
                closeAll(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    @Override
    public void run() {
        String messageFrom;

        while (socket.isConnected()) {
            try {
                messageFrom = bufferedReader.readLine();
                if (messageFrom != null) {
                    if (messageFrom.startsWith("MOARA:")) {
                        // Only send the mill formation message to the client who formed the mill
                        bufferedWriter.write(messageFrom);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    } else if (messageFrom.startsWith("STERGE:")) {
                        // Send piece elimination message to all clients
                        sendMessage(messageFrom);
                    } else {
                        sendMessage(messageFrom);
                    }
                }
            } catch (IOException e) {
                closeAll(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void remove() {
        clientHandlers.remove(this); // When the user leaves the game
        sendMessage("SERVER:" + username + " a parasit jocul! :(");
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        remove();
        try {
            if (bufferedReader != null)
                bufferedReader.close();
            if (bufferedWriter != null)
                bufferedWriter.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
