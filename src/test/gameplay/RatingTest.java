package test.gameplay;

import main.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class RatingTest {
    private static final String TEST_RATING_FILE = "rating.csv";
    private Game game;

    @BeforeEach
    void setUp() throws Exception {
        // Mock user input for username and map selection
        String simulatedInput = "TestUser\n1\n1\n";  // Username + map menu choices
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Cleanup test file before each test
        Files.deleteIfExists(new File(TEST_RATING_FILE).toPath());
        game = new Game();
    }

    @Test
    void testSavePlayerRating() throws Exception {
        assertEquals("TestUser", game.username, "Username should be mocked");

        game.savePlayerRating();

        assertTrue(Files.exists(new File(TEST_RATING_FILE).toPath()),
                "Rating file should be created");

        String content = Files.readString(new File(TEST_RATING_FILE).toPath());
        assertTrue(content.contains("username;points;map"),
                "File should contain header");
    }

    @Test
    void testScoreUpdateLogic() throws Exception {
        // Initial save
        game.savePlayerRating();

        // Simulate score improvement
        game.playerCastle.addPoints(50);
        game.savePlayerRating();

        String content = Files.readString(new File(TEST_RATING_FILE).toPath());
        long entries = content.lines()
                .filter(line -> line.startsWith(game.username))
                .count();

        assertEquals(2, entries, "Should allow multiple entries");
    }

    @Test
    void testNewFileCreation() {
        assertDoesNotThrow(() -> game.savePlayerRating(),
                "Should create new rating file if not exists");
    }
}
