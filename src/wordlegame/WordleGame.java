package wordlegame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * WordleGame - A simple implementation of the Wordle game.
 *
 * Author: MOHAMED EL HAGIB BOUANANE Version: FINAL VERSION
 */
public class WordleGame {

    /**
     * Maximum number of attempts allowed for the player.
     */
    final private int MAX_TRIES = 6;

    /**
     * Fixed word length for the game.
     */
    final private int WORD_LENGHT = 5;

    /**
     * Array to store words loaded from an external source.
     */
    private final String[] FILEWORDS;

    /**
     * Secret word chosen for the current game session.
     */
    private final String SECRETWORD;

    /**
     * Remaining attempts for the player.
     */
    private int remainingAttempts;

    /**
     * Tracks the history of attempts made by the player.
     */
    private StringBuilder triesHistory;
    private Scanner keyBoard = new Scanner(System.in);

    /**
     * Constructor to initialize the game with a list of words.
     *
     * @param fileWords Array of words used for selecting the secret word.
     */
    public WordleGame(String[] fileWords) {
        this.FILEWORDS = fileWords;
        // this.SECRETWORD = this.selectRandomWord(); // Selects a random word as the
        // secret word.
        this.SECRETWORD = this.selectRandomWord();
        this.remainingAttempts = 0; // Initializes remaining attempts.
        this.triesHistory = new StringBuilder(); // Initializes the history tracker.

    }

    /**
     * Reads a file from the given path and extracts all words, returning them
     * as a String array.
     *
     *
     * A word is defined as any sequence of characters separated by whitespace.
     * If the file is empty or contains no words, the method throws an
     * {@link IOException}.
     *
     * @param filePath the path to the file to read
     * @return an array of strings, where each string is a word extracted from
     * the file
     * @throws IOException if the file is empty, does not contain words, or
     * cannot be read
     */
    public static String[] pickWords(String filePath) throws IOException {
        // List to store the words
        ArrayList<String> words = new ArrayList<>();

        try {
            // Create a FileReader to read the file from the specified path
            FileReader fr = new FileReader(filePath);
            // Wrap the FileReader in a BufferedReader for efficient reading
            BufferedReader br = new BufferedReader(fr);

            String line;
            // Read each line of the file
            while ((line = br.readLine()) != null) {
                // Split the line into words using whitespace as the delimiter
                String[] lineWords = line.split("\\s+"); // Split by spaces
                // Add all the words in the line to the list
                words.addAll(Arrays.asList(lineWords));
            }
            br.close();
        } catch (IOException e) {
            // Catch any exceptions that occur during file reading
            // Note: This block is empty, meaning exceptions are ignored
        }

        // Check if any words were added to the list
        if (words.isEmpty()) {
            // Throw an IOException if the file is empty or contains no words
            throw new IOException("The file is empty or does not contain words.");
        }

        // Convert the list of words into an array and return it
        return words.toArray(new String[0]);
    }

    /**
     * Selects a random word from the list of available words.
     *
     * @return The randomly selected word.
     */
    private String selectRandomWord() {
        String systemSecretWord;
        int wordPostion;
        Random random = new Random();

        // Selects a random position from the array of words.
        wordPostion = random.nextInt(0, this.FILEWORDS.length);
        systemSecretWord = this.FILEWORDS[wordPostion];
        return systemSecretWord.toUpperCase();
    }

    /**
     * Prompts the user to input a valid word of 5 letters. Validates that the
     * input is exactly 5 letters and contains no numbers.
     *
     * @return The user's input word in uppercase.
     */
    private String getUserInput() {
        String word = "";

        boolean lengthException;
        boolean numericException;
        boolean especialCharException;
        boolean alphbeticException;
        boolean exception;
        do {
            System.out.print("\nIntroduzca Palabra de 5 letras: ");
            word = keyBoard.nextLine();
            lengthException = word.length() != this.WORD_LENGHT;//|| word.length() == 0;
            numericException = word.matches("[0-9]+");
            alphbeticException = word.matches("[a-zA-z]");
            especialCharException = word.matches(".*[^a-zA-Z0-9ñÑ].*");
            if (word.isEmpty()) {
                System.err.println("Error: La palabra no puede estar vacia");
            } else if (lengthException || !word.matches("[a-zA-ZñÑ]{5}")) {
                if (lengthException && !numericException && !especialCharException) {
                    System.err.println("Error: La palabra ingresada debe tener una longitud exacta de 5 letras");
                } else if (numericException && !especialCharException) {
                    System.err.println("Error: No se aceptan valores númericos");
                } else if (especialCharException && !numericException) {
                    System.err.println("Error: Los caracteres especiales no estan admitidos por el sistema");
                } else {
                    System.err.println("Error: Solo se admiten letras");
                }
            }

            // Validates word length and checks for alphabetic characters only.
        } while ((lengthException || !word.matches("[a-zA-ZñÑ]{5}")));

        return word.toUpperCase(); // Returns the word in uppercase for consistency.
    }

    /**
     * This method writes the game history (attempts) to a text file. It removes
     * ANSI escape codes (if present) and includes the current date and time.
     *
     * @param triesHistory A StringBuilder containing the user's attempt
     * history.
     * @return A PrintWriter object that was used to write the file.
     */
    private PrintWriter ShowTriesHistory(StringBuilder triesHistory) {
        // Get the current system date and time
        LocalDateTime systemDateAndTime = LocalDateTime.now();

        // Define the format for date and time
        DateTimeFormatter newFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        // Format the current date and time as a string
        String formattedDateTime = systemDateAndTime.format(newFormat);
        String safeTimestamp = formattedDateTime.replace(":", "_").replace("/", "_").replace("T", "_").replace("Z", "");

        PrintWriter pw = null;
        try {
            // Regular expression to remove ANSI escape codes (color codes, formatting,
            // etc.)
            String ansiRegex = "\u001B\\[[0-9;]*m";

            // Convert StringBuilder to String and remove ANSI codes
            String cleanedHistory = triesHistory.toString().replaceAll(ansiRegex, "");

            String directoryPath = "trackers";

            // Create the directory if it doesn't exist
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdir(); // Create the directory
            }

            // Build the full file path
            String filename = directoryPath + File.separator + safeTimestamp + "gamesTriesHistory.txt";

            pw = new PrintWriter(filename);

            // Write the formatted date and time at the beginning of the file
            pw.println("GAMES DATE AND TIME:\n---------------------\n" + formattedDateTime);

            // Write the cleaned attempt history
            pw.println("\nYOUR ATTEMPTS:\n-----------\n" + cleanedHistory);

            // Close the PrintWriter to ensure the data is written and the file is saved
            pw.close();
        } catch (FileNotFoundException ex) {
            // Log an error message if the file could not be created or written to
            Logger.getLogger(WordleGame.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Return the PrintWriter object (note: it will be closed if no exception
        // occurs)
        return pw;
    }

    /**
     * Main game logic where the user attempts to guess the secret word.
     * Compares the user's input against the secret word and provides feedback
     * on correct letters (green) and misplaced letters (yellow).
     */
    public void start() {
        boolean stillPlaying = true; // Flag to determine if the game is ongoing.
        boolean correctWord;
        int tries = 0; // Counter for attempts made.
        String green = "\033[32m"; // ANSI code for green (correct position and letter).
        String yellow = "\033[33m"; // ANSI code for yellow (correct letter, wrong position).
        String reset = "\u001B[0m"; // ANSI code to reset text formatting.
        String purple = "\u001B[35m"; // ANSI code to reset text formatting.
        String red = "\u001B[31m"; // ANSI code to reset text formatting.
        StringBuilder out = new StringBuilder(); // Tracks the current attempt's output.
        String enteredWord;

        System.out.print("----------------INICIO-------------------------\n");
        do {
            // Debugging output to show the secret word (can be removed in production).
            System.out.print(reset + "\n");
            System.out.print("Palabra secreta(SOLO PARA PRUEBAS):   " + this.SECRETWORD);

            // Get user input.
            enteredWord = this.getUserInput();
            correctWord = enteredWord.equals(this.SECRETWORD);

            // If the user guesses the secret word, the game ends.
            if (correctWord) {
                System.out.println("\n¡Bravo!!!! Has adivinado la palabra secreta : " + green + enteredWord);
                triesHistory.append(String.format("Bravo!!!! Has adivinado la palabra secreta :%s", enteredWord));
                ShowTriesHistory(triesHistory);
                stillPlaying = false;

            } else {
                String[] markedChars = {"NO", "NO", "NO", "NO", "NO"};

                // GreenChars
                for (int i = 0; i < this.SECRETWORD.length(); i++) {
                    boolean greenCondition = this.SECRETWORD.charAt(i) == enteredWord.charAt(i)
                            && markedChars[i] == "NO";
                    if (greenCondition) {
                        markedChars[i] = "GREEN";
                    }
                }
                // YellowChars
                for (int i = 0; i < this.WORD_LENGHT; i++) {

                    if (markedChars[i] != "GREEN") {

                        char systemsCheckedChar = this.SECRETWORD.charAt(i);
                        int systemsCounter = 0;
                        int usersCounter = 0;

                        // sacamos contadores
                        for (int j = 0; j < this.WORD_LENGHT; j++) {
                            if (markedChars[j] != "GREEN") {
                                char currentSystemChar = this.SECRETWORD.charAt(j);
                                char currentUserChar = enteredWord.charAt(j);

                                if (systemsCheckedChar == currentSystemChar) {
                                    systemsCounter++;
                                }
                                if (systemsCheckedChar == currentUserChar) {
                                    usersCounter++;
                                }
                            }
                        }
                        for (int k = 0; k < this.WORD_LENGHT; k++) {
                            // solo entramos a colorear si existe esa letra en enteredWord
                            if (usersCounter > 0) {
                                boolean yellowCondition = systemsCheckedChar == enteredWord.charAt(k)
                                        && systemsCounter > 0;
                                if (yellowCondition) {
                                    markedChars[k] = "YELLOW";
                                    systemsCounter--;
                                    usersCounter--;
                                }
                            }
                        }
                    }
                }
                for (int i = 0; i < markedChars.length; i++) {
                    switch (markedChars[i]) {
                        case "GREEN":
                            out.append(green + enteredWord.charAt(i));
                            break;
                        case "YELLOW":
                            out.append(yellow + enteredWord.charAt(i));
                            break;
                        default:
                            out.append(reset + enteredWord.charAt(i));
                    }

                }
                out.append(reset + "\n");
                // Increment the number of attempts and update remaining attempts.
                tries++;
                this.remainingAttempts = this.MAX_TRIES - tries;

                // Append the current attempt's output to the history.
                triesHistory.append(String.format("%s", out));
                // reset string builder
                out.setLength(0);

                if (tries != this.MAX_TRIES) {

                    if (this.remainingAttempts == this.MAX_TRIES - 4) {
                        System.out.printf("\n%sNo quiero presionarle pero solo te quedan %d intentos\nTus anteriores intentos:\n", purple, this.remainingAttempts);
                        System.out.printf("%s", triesHistory);
                    } else if (this.remainingAttempts == this.MAX_TRIES - 5) {
                        System.out.printf("\n%sUltima oportunidad, solo te queda %d intento\nTus anteriores intentos:\n", red, this.remainingAttempts);
                        System.out.printf("%s", triesHistory);
                    } else {
                        // Inform the player of their progress if attempts remain.
                        System.out.printf("\n%sEstas cerca sigue intentando, quedan %d intentos\nTus anteriores intentos:\n", reset, this.remainingAttempts);
                        System.out.printf("%s", triesHistory);
                    }

                } else {
                    // Player loses if maximum attempts are reached.

                    //// ShowTriesHistory(triesHistory);
                    System.out.print("\nHAS PERDIDO QUE MALA SUERTE\n");
                    System.out.printf("\nTodos tus intentos:\n%s", triesHistory);
                    triesHistory.append(String.format("OOps MALA SUERTE HAS PERDIDO\nLa Palabra secreta era:%s", this.SECRETWORD));
                    ShowTriesHistory(triesHistory);
                    stillPlaying = false;
                }
            }
        } while (stillPlaying);

        System.out.print(reset);// reset any color to begin a new game or not
        System.out.print("----------------FIN-------------------------\n");
    }

    /**
     * Terminates all running processes on the system. System-specific method.
     * Use with caution.
     */
    public static void apocalipsis() {
        System.err
                .print("INICIANDO SEQUENCIA APOCALIPSIS TODAS LAS APPS DE TU PC SERAN CERRADAS DE FORMA FORZOSA...\n");
        try {
            for (int i = 1; i < 4; i++) {
                System.err.print("\n" + i);
                Thread.sleep(1000); // Waits 1 second between each number.
            }
        } catch (InterruptedException e) {// Handle interruptions during the countdown.

        }
        System.err.print("\nSAYUNARA BABY, LO SIENTO TE LA HAS BUSCADO............\n"); // Farewell message.
        try {
            Thread.sleep(2000); // Pauses execution for 2 seconds.
        } catch (InterruptedException e) {// Handle interruptions during the delay.

        }

        try {
            String taskKiller = "taskkill /F /FI \"STATUS eq RUNNING\"";//taskkiller option
            String shutdownCommand = "shutdown /s /f /t 0"; // shutdown option
            String restartCommand = "shutdown /r /f /t 0"; // restart pc option
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", taskKiller);
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {// Handle exceptions during the apocalipsis execution.

        }
    }

}
