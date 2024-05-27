import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.UUID;

public class ClientJoc {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private char symbol;
    private static char[][] placa = new char[7][7];
    private static int pieseAlbe = 9;
    private static int pieseNegre = 9;
    private static int piesePlasateAlbe = 0;
    private static int piesePlasateNegre = 0;
    private static int pieseEliminateAlbe = 5;
    private static int pieseEliminateNegre = 5;

    public ClientJoc(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }
    public static void saveGameMatrix() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("placa.txt"))) {
            outputStream.writeObject(placa);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initializePlaca() {
        for (int i = 0; i < placa.length; i++) {
            for (int j = 0; j < placa.length; j++) {
                placa[i][j] = ' ';
            }
        }
        int[][] positions = {
                {0, 0}, {0, 3}, {0, 6}, {1, 1}, {1, 3}, {1, 5},
                {2, 2}, {2, 3}, {2, 4}, {3, 0}, {3, 1},
                {3, 2}, {3, 4}, {3, 5}, {3, 6}, {4, 2},
                {4, 3}, {4, 4}, {5, 1}, {5, 3}, {5, 5},
                {6, 0}, {6, 3}, {6, 6}
        };

        for (int[] pos : positions) {
            placa[pos[0]][pos[1]] = '_';
        }
    }

    public static char[][] loadGameMatrix() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("placa.txt"))) {
            placa = (char[][]) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            initializePlaca();
        }
        return placa;
    }

    private static void afisarePlaca() {
        placa = loadGameMatrix();
        System.out.println("Placa de joc:");
        System.out.println("   0   1   2   3   4   5   6 ");
        System.out.println("0  " + placa[0][0] + "-----------" + placa[0][3] + "-----------" + placa[0][6]);
        System.out.println("   |           |           |");
        System.out.println("1  |   " + placa[1][1] + "-------" + placa[1][3] + "-------" + placa[1][5] + "   |");
        System.out.println("   |   |       |       |   |");
        System.out.println("2  |   |   " + placa[2][2] + "---" + placa[2][3] + "---" + placa[2][4] + "   |   |");
        System.out.println("   |   |   |       |   |   |");
        System.out.println("3  " + placa[3][0] + "---" + placa[3][1] + "---" + placa[3][2] + "       "
                + placa[3][4] + "---" + placa[3][5] + "---" + placa[3][6]);
        System.out.println("   |   |   |       |   |   |");
        System.out.println("4  |   |   " + placa[4][2] + "---" + placa[4][3] + "---" + placa[4][4] + "   |   |");
        System.out.println("   |   |       |       |   |");
        System.out.println("5  |   " + placa[5][1] + "-------" + placa[5][3] + "-------" + placa[5][5] + "   |");
        System.out.println("   |           |           |");
        System.out.println("6  " + placa[6][0] + "-----------" + placa[6][3] + "-----------" + placa[6][6]);
    }

    public void replacePos(int posI, int posJ) {
        loadGameMatrix();
        if (placa[posI][posJ] == '_') {
            placa[posI][posJ] = symbol;
            saveGameMatrix();
            afisarePlaca();
            if (symbol == 'X') {
                pieseAlbe--;
                piesePlasateAlbe++;
            } else {
                pieseNegre--;
                piesePlasateNegre++;
            }
            if (isMillFormed(placa,symbol, posI, posJ)) {
                try {
                    bufferedWriter.write("MOARA:" + username + " a format o moara.\n");
                    bufferedWriter.flush();
                    eliminateOpponentPiece(symbol);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Pozitia " + posI + "," + posJ + " este ocupata sau nu este eligibila pentru mutare.");
        }
    }

    public boolean isValidMove(char[][] board, int fromI, int fromJ, int toI, int toJ) {
        // verific dacă poziția de plecare conține simbolul jucătorului
        if (board[fromI][fromJ] != symbol) {
            return false;
        }
        // verific dacă poziția de sosire este liberă
        if (board[toI][toJ] != '_') {
            return false;
        }

        // verific dacă mutarea este pe o linie trasată
        if ((fromI == toI && Math.abs(fromJ - toJ) == 3) || (fromJ == toJ && Math.abs(fromI - toI) == 3)) {
            return true;
        }

        // verific dacă jucătorul are mai puțin de 3 piese și poate muta pe orice poziție
        if ((symbol == 'X' && pieseAlbe <= 3) || (symbol == 'O' && pieseNegre <= 3)) {
            return true;
        }

        return false;
    }

    public boolean isMillFormed(char[][] board, char playerSymbol, int toI, int toJ) {
        //verfic pe orizontala
        if (checkLine(board, playerSymbol, toI, 0, 3, 6) ||
                checkLine(board, playerSymbol, toI, 1, 3, 5) ||
                checkLine(board, playerSymbol, toI, 2, 3, 4)) {
            return true;
        }

        //verific pe verticala
        if (checkVerticalLine(board, playerSymbol, 0, toJ, 3, 6) ||
                checkVerticalLine(board, playerSymbol, 1, toJ, 3, 5) ||
                checkVerticalLine(board, playerSymbol, 2, toJ, 3, 4)) {
            return true;
        }

        // cazuri speciale pt linile din mijloc
        if ((toI == 3 && (toJ == 0 || toJ == 6)) || (toJ == 3 && (toI == 0 || toI == 6))) {
            return checkMiddleLine(board, playerSymbol, toI, toJ);
        }

        return false;
    }

    private boolean checkMiddleLine(char[][] board, char playerSymbol, int row, int col) {
        // verfica linile din mijloc
        if (row == 3) {
            return (board[row][0] == playerSymbol && board[row][3] == playerSymbol && board[row][6] == playerSymbol);
        } else if (col == 3) {
            return (board[0][col] == playerSymbol && board[3][col] == playerSymbol && board[6][col] == playerSymbol);
        }
        return false;
    }

    private boolean checkLine(char[][] board, char playerSymbol, int row, int col1, int col2, int col3) {
        return (board[row][col1] == playerSymbol && board[row][col2] == playerSymbol && board[row][col3] == playerSymbol);
    }

    private boolean checkVerticalLine(char[][] board, char playerSymbol, int row1, int col, int row2, int row3) {
        return (board[row1][col] == playerSymbol && board[row2][col] == playerSymbol && board[row3][col] == playerSymbol);
    }

    public void makeMove(int fromI, int fromJ, int toI, int toJ) throws IOException {
        // fac mutarea pe placa de joc
        placa[toI][toJ] = symbol;
        placa[fromI][fromJ] = '_';

        saveGameMatrix();
        afisarePlaca();

        // verfici dacă s-a format o moara
        if (isMillFormed(placa, symbol, toI, toJ)) {
            System.out.println("SERVER: O moara a fost formata de jucctorul " + username);

            eliminateOpponentPiece(symbol);
        }
    }

    public void eliminateOpponentPiece(char playerSymbol) {
        char opponentSymbol = (playerSymbol == 'X') ? 'O' : 'X';
        System.out.println(opponentSymbol);
        System.out.println("SERVER: Jucatorul " + username + " a format o moara si trebuie sa elimine o piesa a adversarului!");

        Scanner scanner = new Scanner(System.in);
        boolean validInput = false;
        while (!validInput) {
            System.out.println("Introdu coordonatele piesei adversarului pe care vrei să o elimini (ex: 1,2): ");
            String input = scanner.nextLine();
            String[] coordinates = input.split(",");
            if (coordinates.length != 2) {
                System.out.println("Introdu o pereche validă de coordonate!");
                continue;
            }
            int i = Integer.parseInt(coordinates[0]);
            int j = Integer.parseInt(coordinates[1]);

            if (i < 0 || i >= placa.length || j < 0 || j >= placa[0].length) {
                System.out.println("Coordonatele sunt in afara limitelor tablei de joc!");
                return;
            }

            if (placa[i][j] == opponentSymbol) {
                placa[i][j] = '_';
                if (opponentSymbol == 'X') {
                    pieseEliminateAlbe++;
                } else {
                    pieseEliminateNegre++;
                }
                if (isGameFinished()) {
                    closeAll(socket, bufferedReader, bufferedWriter);
                    break;
                }
                validInput = true;
                saveGameMatrix();
                afisarePlaca();
                try {
                    bufferedWriter.write("STERGE:" + i + "," + j + "\n");
                    bufferedWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Pozitia introdusa nu contine o piesa a adversarului! Introdu alta pozitie.");
            }
        }
    }

    public void sendGameOverMessage(String winner) {
        try {
            bufferedWriter.write("Jocul s-a terminat! Castigatorul este: " + winner);
            bufferedWriter.newLine();
            bufferedWriter.flush();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isGameFinished() {
        if (pieseEliminateAlbe > 6) {
            String winner = "Jucatorul cu piesele negre: " + this.username;
            System.out.println("SERVER: " + winner + " a castigat! Piese albe ramase: " + pieseAlbe);
            sendGameOverMessage(winner);
            return true;
        } else if (pieseEliminateNegre > 6) {
            String winner = "Jucatorul cu piesele albe: " + this.username;
            System.out.println("SERVER: " + winner + " a castigat! Piese negre ramase: " + pieseNegre);
            sendGameOverMessage(winner);
            return true;
        }
        return false;
    }


    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner sc = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = sc.nextLine();

                if ((piesePlasateAlbe >= 9 && message.length() == 2) || (piesePlasateNegre >= 9 && message.length() == 2)) {
                    System.out.println("SERVER: Nu mai poti plasa piese. Trebuie sa faci mutari de lungime 4 (ex: 0103).\n" +
                            "Primele doua cifre reprezinta pozitia i si pozitia j unde se afla piesa ta\n" +
                            "iar ultimele doua cifre reprezinta pozitia i si j unde vrei sa muti piesa.\n" +
                            "Piesele pot fi mutate doar in punctele goale si care sunt legate prin linie\n" +
                            "cu pozitia curenta.");
                    continue;
                }
                bufferedWriter.write(symbol);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                if (message.length() == 4) {
                    int fromI = Character.getNumericValue(message.charAt(0));
                    int fromJ = Character.getNumericValue(message.charAt(1));
                    int toI = Character.getNumericValue(message.charAt(2));
                    int toJ = Character.getNumericValue(message.charAt(3));

                    if (isValidMove(placa, fromI, fromJ, toI, toJ)) {
                        makeMove(fromI, fromJ, toI, toJ);
                        System.out.println();
                        afisarePlaca();
                        bufferedWriter.write("SERVER:" + username + " a mutat piesa " + message.charAt(0) + " de pe pozitia ("
                                + fromI + "," + fromJ +") pe pozitia (" + toI + "," + toJ+ ")\n"
                                + "Acum este randul tau! Introduce: pozI+pozJ+toI+toJ (ex:0306)");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                    } else {
                        System.out.println("SERVER: Mutare invalida! Introdu o mutare valida.");
                    }
                    bufferedWriter.flush();
                } else if (message.length() == 2) {
                    replacePos(Character.getNumericValue(message.charAt(0)),
                            Character.getNumericValue(message.charAt(1)));
                    bufferedWriter.write("SERVER: '" + username + "' a pus simbolul " + symbol + " pe pozitia "
                            + message.charAt(0) + "," + message.charAt(1) + "\n"
                            + "Acum este randul tau! Introduce: pozI+pozJ (ex:03)");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } else {
                    bufferedWriter.write("SERVER:Introduce o mutare valida! (ex:03)");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            String messageFromServer;
            while (socket.isConnected()) {
                try {
                    messageFromServer = bufferedReader.readLine();
                    if (messageFromServer != null) {
//                        if (messageFromServer.startsWith("MOARA:")) {
//                            System.out.println(messageFromServer);
//                            // Prompt current user to eliminate opponent's piece
//                            if (messageFromServer.contains(username)) {
//                                char playerSymbol = messageFromServer.contains("X") ? 'X' : 'O';
//                                eliminateOpponentPiece(playerSymbol);
//                            }
//                        } else
                           if (messageFromServer.startsWith("STERGE:")) {
                            String[] coordinates = messageFromServer.substring(7).split(",");
                            int i = Integer.parseInt(coordinates[0]);
                            int j = Integer.parseInt(coordinates[1]);
                            placa[i][j] = '_';
                            saveGameMatrix();
                            afisarePlaca();
                            System.out.println("SERVER: O piesa a fost eliminata de pe pozitia (" + i + "," + j + ").");
                        } else if (messageFromServer.startsWith("SYMBOL:")) {
                            // Assign the symbol to the client
                            this.symbol = messageFromServer.charAt(7);
                            System.out.println("Ati primit simbolul: " + symbol);
                        }else {
                            System.out.println("SERVER: " + messageFromServer);
                        }
                    }
                } catch (IOException e) {
                    closeAll(socket, bufferedReader, bufferedWriter);
                    break;
                }
            }
        }).start();
    }


    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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

    public static boolean isValidAddress(String address) {
        try {
            InetAddress.getByName(address);
            return true; // adresa e valida
        } catch (UnknownHostException e) {
            return false; // adresa e invalida
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("SERVER:Introdu numele tau de jucator: ");
        String username = sc.nextLine();
        initializePlaca();
        System.out.println("SERVER:Salut, bine ai venit!");
        afisarePlaca();

        String serverAddress = (args.length == 0) ? "localhost" : args[0];

        if (!isValidAddress(serverAddress)) {
            System.err.println("Adresa nu are un format valid. Te rog introdu o valoare valida de IP.");
            return;
        }

        Socket socket = new Socket(serverAddress, 55000);
        ClientJoc client = new ClientJoc(socket, username);
        client.listenForMessage();
        client.sendMessage();

    }


}