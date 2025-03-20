package wordlegame;

import java.io.IOException;
import java.util.Scanner;

/**
 * MainWordleGame - Main entry point for running the Wordle game. This class
 * starts the game, handles user input, and offers an option to play again.
 *
 * Author: MOHAMED EL HAGIB BOUANANE Version: FINAL VERSION
 */
public class MainWordleGame {

    /**
     * Main method to start the Wordle game, handle user input for new games,
     * and manage game flow with options to start new games or exit. If the user
     * provides invalid responses multiple times, an "apocalypse" is triggered.
     *
     * @param args Command line arguments (not used in this case).
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        boolean gameOver = false; // Flag to determine whether the game should continue or not.
        Scanner key_bord = new Scanner(System.in); // Scanner for reading user input.
        String answar; // User response to whether they want to play another game.
        int gameCounter = 1; // Counter to track the number of games played.
        int apocalipsis = 0; // Counter for invalid responses, triggers apocalypse after 3 invalid answers.
        String filePath = "secretWords5.txt"; // Change path to your txt file path
        final String[] SECRETWORD = WordleGame.pickWords(filePath);

        // Create the initial game instance with the provided list of secret words.
        WordleGame newGame = new WordleGame(SECRETWORD);

        /* Start the first game */
        newGame.start(); // Start the first game.

        /*
         * Game loop to ask the player if they want to play another game after the
         * current one ends
         */
        do {

            // Prompt the user for a response to start a new game or exit.
            System.out.print("\nUna partida extra? contesta solo SI o NO :");
            answar = key_bord.nextLine().toUpperCase(); // Read input and convert to uppercase for consistency.

            switch (answar) {
                case "SI":
                    // If the user answers "SI" (yes), increment the game counter and start a new
                    // game.
                    gameCounter++;
                    System.out.printf("\nComenzando %dº Partida.....\n\n", gameCounter); // Display the game number.
                    newGame = new WordleGame(SECRETWORD); // Create a new game instance.
                    newGame.start(); // Start the new game.
                    break;

                case "NO":
                    // If the user answers "NO", end the game loop and print a closing message.
                    System.out.print(
                            "\nEspero que te haya gustado mi juego (APRUEBAME PLEASE,SOLO SE ACEPTA UN 10 DE NOTA)\n");
                    gameOver = true; // Set gameOver to false to exit the loop.

                    break;

                default:
                    // If the user enters an invalid response (not "SI" or "NO"), print an error
                    // message.

                    System.err.print("\nError:solo se admite como respuesta SI o NO\n");
                    apocalipsis++; // Increment the invalid response counter.
                    if (apocalipsis == 2) {
                        System.out.print(
                                "Porfavor conteste solo con si o no, en caso contarario el resultado será fatal");

                    }

                    // If the user gives 3 invalid responses, trigger the "apocalypse" sequence.
                    if (apocalipsis == 3) {
                        WordleGame.apocalipsis(); // Execute the apocalypse action (kill all running processes).
                    }
                    break;
            }

        } while (!gameOver); // Continue the loop while the game is ongoing.
        key_bord.close();

    }
}
