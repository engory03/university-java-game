package test.gameplay;

import main.Game;
import main.units.Spearman;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaveGameTest {
    private static final String PROJECT_ROOT = new File("").getAbsolutePath().replaceAll("\\\\", "/");
    private static final String TEST_SAVE_FILE = PROJECT_ROOT + "/saves/game.TestUser.csv";
    private Game game;

    @BeforeEach
    void setUp() throws Exception {
        // Mock user input for username and map selection
        String simulatedInput = "TestUser\n1\n1\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        
        // Clear previous test saves
        Files.deleteIfExists(new File(TEST_SAVE_FILE).toPath());
        new File(TEST_SAVE_FILE).getParentFile().mkdirs();
        
        game = new Game();
    }

    @Test
    void testSaveGameCreatesFile() {
        game.saveGameState();
        
        assertTrue(Files.exists(new File(TEST_SAVE_FILE).toPath()),
            "Save file should be created");
    }

    @Test
    void testSaveFileStructure() throws Exception {
        game.saveGameState();
        String content = Files.readString(new File(TEST_SAVE_FILE).toPath());

        assertAll("File sections",
            () -> assertTrue(content.contains("[Castles]"), "Should have castles section"),
            () -> assertTrue(content.contains("[Units]"), "Should have units section"),
            () -> assertTrue(content.contains("[Map]"), "Should have map section")
        );
    }

    @Test
    void testUnitStatePreservation() throws Exception {
        Spearman unit = new Spearman(2, 3, true, game.playerCastle);
        game.playerCastle.buyUnit(unit);
        game.map.addUnit(unit);

        game.saveGameState();
        String content = Files.readString(new File(TEST_SAVE_FILE).toPath());

        assertTrue(content.contains("Spearman;2;3"), 
            "Should save unit type and position");
    }

    @Test
    void testMapStatePreservation() throws Exception {
        game.saveGameState();
        String content = Files.readString(new File(TEST_SAVE_FILE).toPath());

        assertTrue(content.contains("GRASS") || content.contains("WATER"),
            "Should preserve map tile types");
    }
}