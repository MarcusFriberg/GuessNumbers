import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Server {
    // Properties
    private List<Integer> secretNumberList = new ArrayList<>();
    private List<Integer> possibleNumbersToGuess = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    private boolean gameIsRunning;
    private BufferedReader reader;
    private PrintWriter writer;

    // Constructor
    public Server() {
    }

    // Methods
    public void start() throws IOException {
        try {
            // Create a serverSocket that listens for a client at port 8082
            ServerSocket serverSocket = new ServerSocket(8082);
            System.out.println("Servern är startad och väntar på klient");

            // When a client connects, accept the socket and store it
            Socket socket = serverSocket.accept();
            System.out.println("En klient har anslutit");

            // Create an input stream
            InputStream inputStream = socket.getInputStream();

            // Create a reader
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Create an output stream
            OutputStream outputStream = socket.getOutputStream();

            // Create a printwriter
            writer = new PrintWriter(outputStream, true);

        } catch(IOException ioException) {
            System.out.println(ioException);
        }

        while(gameIsRunning) {
            if(reader.ready()) {
                String incomingMessage = reader.readLine();
                System.out.println("Client says: " + incomingMessage);
                String outputText = checkResultAndCreateReply(incomingMessage);
                try {
                    Thread.sleep(2000);
                } catch(InterruptedException e) {
                    System.out.println("Error trying to pause for two seconds with message " + e);
                }
                System.out.println(outputText);
                writer.println(outputText);
            }
        }
    }

    public void init() {
        System.out.println("initializing server...");
        System.out.println("Server is picking secret numbers: ");
        while(secretNumberList.size() < 3) {
            int randomNumber = ((int)(Math.random()*10)+1);
            if(!secretNumberList.contains(randomNumber)) {
                secretNumberList.add(randomNumber);
                System.out.print(randomNumber + " ");
            }
        }
        gameIsRunning = true;
        System.out.println("\nServer init complete...");
    }

    public String checkResultAndCreateReply(String input) {
        if(input.equals("I lost")) {
            gameIsRunning = false;
            return "Yay! I won!";
        } else {
            // Split string on " ", will return an array of strings of size 4
            // String example: "Correct! im guessing 5"
            // The number will be located at index 3
            String[] inputData = input.split(" ");
            int guessedNumber = Integer.parseInt(inputData[3]);
            if(secretNumberList.contains(guessedNumber)) {
                secretNumberList.remove(secretNumberList.indexOf(guessedNumber));
                if(secretNumberList.size() == 0) {
                    gameIsRunning = false;
                    return "I lost";
                } else {
                    return "Correct! im guessing " + getNewNumber();
                }
            } else {
                return "Wrong! im guessing " + getNewNumber();
            }
        }
    }

    public int getNewNumber() {
        Collections.shuffle(possibleNumbersToGuess);
        return possibleNumbersToGuess.remove(0);
    }
}
