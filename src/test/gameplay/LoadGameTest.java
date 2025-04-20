package test.gameplay;

import main.Game;
import main.units.Spearman;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class LoadGameTest {
    private static final String PROJECT_ROOT = new File("").getAbsolutePath().replaceAll("\\\\", "/");
    private static final String TEST_SAVE_FILE = PROJECT_ROOT + "/saves/game.TestUser.csv";
    private Game game;

    @BeforeEach
    void setUp() throws Exception {
        String simulatedInput = "TestUser\n1\n1\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        Files.deleteIfExists(new File(TEST_SAVE_FILE).toPath());
        new File(TEST_SAVE_FILE).getParentFile().mkdirs();

        game = new Game();
    }

    @Test
    void testLoadGameRestoresCastleState() throws Exception {
        game.playerCastle.addPoints(150);
        game.saveGameState();

        // Reset input mocking for new instance
        System.setIn(new ByteArrayInputStream("TestUser\n1\n1\n".getBytes()));
        Game loadedGame = new Game();
        loadedGame.loadGame();

        assertEquals(150, loadedGame.playerCastle.getPoints(), "Should restore player points");
    }

    @Test
    void testLoadGameRestoresUnits() throws Exception {
        Spearman unit = new Spearman(5, 5, true, game.playerCastle);
        game.playerCastle.buyUnit(unit);
        game.saveGameState();

        // Reset input mocking for new instance
        System.setIn(new ByteArrayInputStream("TestUser\n1\n1\n".getBytes()));
        Game loadedGame = new Game();
        loadedGame.loadGame();

        assertFalse(loadedGame.playerCastle.getUnits().isEmpty(), "Should restore player units");
    }

    @Test
    void testLoadMissingFileHandling() {
        // Reset input mocking for new instance
        System.setIn(new ByteArrayInputStream("TestUser\n1\n1\n".getBytes()));
        Game loadedGame = new Game();
        loadedGame.loadGame();
        
        assertNotNull(loadedGame.map);
    }

    @Test
    void testLoadCorruptedFileHandling() throws Exception {
        Files.writeString(new File(TEST_SAVE_FILE).toPath(), "Invalid\n[Castles]\nBadData");

        // Reset input mocking for new instance
        System.setIn(new ByteArrayInputStream("TestUser\n1\n1\n".getBytes()));
        Game loadedGame = new Game();
        loadedGame.loadGame();

        assertFalse(loadedGame.playerCastle.getUnits().isEmpty());
    }
}