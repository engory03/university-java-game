package test.gameplay;

import main.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MapEditorTest {
    private static final String PROJECT_ROOT = new File("").getAbsolutePath().replaceAll("\\\\", "/");
    private static final String TEST_MAP_FILE = PROJECT_ROOT + "/maps/test_map.csv";
    private Game game;

    @BeforeEach
    void setUp() throws Exception {
        Files.deleteIfExists(new File(TEST_MAP_FILE).toPath());
        new File(TEST_MAP_FILE).getParentFile().mkdirs();
    }

    @Test
    void testInvalidTileHandling() {
        String input = "2\n5\n5\n10,10,INVALID_TYPE\nSAVE\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        assertThrows(Exception.class, () -> game = new Game(),
                "Should reject invalid tile types");
    }
}