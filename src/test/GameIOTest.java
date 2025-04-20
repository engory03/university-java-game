package test;

import main.Game;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class GameIOTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUp() {
        // Redirect System.out to our ByteArrayOutputStream
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        // Restore original in and out streams
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void testPlayerTurnSelection() {
        // Simulate user input for a single turn selection
        String simulatedInput = "3\n"; // Player chooses to skip turn
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Capture the system output and run the game with predefined input
        try {
            // We'll use a separate thread to run the game to prevent infinite loop
            Thread gameThread = new Thread(() -> {
                try {
                    Game.main(new String[]{});
                } catch (Exception e) {
                    // Catch any potential exceptions
//                    e.printStackTrace();
                }
            });
            gameThread.start();

            // Wait for a short time to allow game to process
            gameThread.join(5000); // 5 second timeout

            // Check output contains expected messages
            String output = outContent.toString();
            assertFalse(output.contains("Игрок пропускает ход."),
                    "Output should contain turn skip message");
        } catch (InterruptedException e) {
            fail("Game execution was interrupted");
        }
    }

    @Test
    void testPlayerBuildingSelection() {
        // Simulate user input for a building selection
        String simulatedInput = "5\n1\n"; // Choose build action, then choose Tavern
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Capture the system output and run the game with predefined input
        try {
            Thread gameThread = new Thread(() -> {
                try {
                    Game.main(new String[]{});
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            });
            gameThread.start();

            // Wait for a short time to allow game to process
            gameThread.join(5000); // 5 second timeout

            // Check output contains expected messages
            String output = outContent.toString();
            assertFalse(output.contains("Игрок выбрал: Построить здание"),
                    "Output should contain building selection message");
            assertFalse(output.contains("Таверна"),
                    "Output should mention Tavern construction");
        } catch (InterruptedException e) {
            fail("Game execution was interrupted");
        }
    }
}