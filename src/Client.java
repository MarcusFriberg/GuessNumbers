import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Client {
    // Properties
    private List<Integer> secretNumberList = new ArrayList<>();
    private List<Integer> possibleNumbersToGuess = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    private boolean gameIsRunning;
    private BufferedReader reader;
    private PrintWriter writer;
    private boolean firstGuess;
    private String outgoingMessage;

    // Constructor
    public Client() {
    }

    // Methods
    public void start() throws IOException {
        try {
            // When a client connects, accept the socket and store it
            Socket socket = new Socket("localhost", 8082);

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
            if(firstGuess) {
                outgoingMessage = "Ok! im guessing " + getNewNumber();
                System.out.println(outgoingMessage);
                writer.println(outgoingMessage);
                firstGuess = false;
            } else {
                if(reader.ready()) {
                    String incomingMessage = reader.readLine();
                    System.out.println("Server says: " + incomingMessage);
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
    }

    public void init() {
        System.out.println("initializing client...");
        System.out.println("Client is picking secret numbers: ");
        while(secretNumberList.size() < 3) {
            int randomNumber = ((int)(Math.random()*10)+1);
            if(!secretNumberList.contains(randomNumber)) {
                secretNumberList.add(randomNumber);
                System.out.print(randomNumber + " ");
            }
        }
        firstGuess = true;
        gameIsRunning = true;
        System.out.println("\nClient init complete...");
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
