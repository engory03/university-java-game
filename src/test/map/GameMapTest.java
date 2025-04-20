package test.map;

import main.buildings.Castle;
import main.map.GameMap;
import main.units.Spearman;
import main.units.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.logging.*;

import static org.junit.jupiter.api.Assertions.*;

class GameMapTest {

    private GameMap gameMap;
    private static final Logger logger = Logger.getLogger(GameMapTest.class.getName());

    @BeforeEach
    void setUp() throws IOException {
        gameMap = new GameMap();

        // устанавливаем логгер, записываем error логи в файл
        FileHandler fileHandler = new FileHandler("error_logs.txt", true);
        fileHandler.setFormatter(new SimpleFormatter());
        fileHandler.setLevel(Level.SEVERE);
        logger.addHandler(fileHandler);

        // выводим info и warn логи в консоль
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        logger.addHandler(consoleHandler);
    }

    @Test
    void isPlayerZoneWorksForCorner() {
        logger.info("Testing Player Zone at corner...");
        assertTrue(gameMap.isPlayerZone(0, 0));
        assertTrue(gameMap.isCompZone(GameMap.WIDTH - 1, GameMap.HEIGHT - 1));
    }

    @Test
    void isPlayerZoneWorksForOtherZones() {
        logger.info("Testing Player Zone for other zones...");
        for (int y = 0; y < GameMap.PLAYER_ZONE_SIZE; y++) {
            for (int x = 0; x < GameMap.PLAYER_ZONE_SIZE; x++) {
                if (x == 0 && y == 0) continue;
                assertTrue(gameMap.isPlayerZone(x, y));
            }
        }
    }

    @Test
    void isCompZoneWorksForOtherZones() {
        logger.info("Testing Computer Zone for other zones...");
        for (int y = GameMap.HEIGHT - GameMap.COMP_ZONE_SIZE; y < GameMap.HEIGHT; y++) {
            for (int x = GameMap.WIDTH - GameMap.COMP_ZONE_SIZE; x < GameMap.WIDTH; x++) {
                if (x == GameMap.WIDTH - 1 && y == GameMap.HEIGHT - 1) continue;
                assertTrue(gameMap.isCompZone(x, y));
            }
        }
    }

    @Test
    void isRoadWorksForDiagonal() {
        logger.warning("Testing Road functionality along diagonal...");
        for (int i = 0; i < Math.min(GameMap.WIDTH, GameMap.HEIGHT); i++) {
            if (i != 0 && i != GameMap.HEIGHT - 1) {
                assertTrue(gameMap.isRoad(i, i));
            }
        }
    }

    @Test
    void handleInvalidCoordinatesError() {
        logger.info("Testing invalid coordinates (-1,-1)...");

        boolean isWalkable = gameMap.isWalkable(-1, -1);

        if (!isWalkable) {
            logger.severe("Error: Invalid coordinates (-1,-1) detected. isWalkable returned false.");
        }

        assertFalse(isWalkable, "Expected isWalkable to return false for invalid coordinates (-1,-1)");
    }


    @Test
    void hasObstacleInMap() {
        logger.info("Checking for obstacles in the map...");
        boolean hasObstacle = false;
        for (int y = 0; y < GameMap.HEIGHT; y++) {
            for (int x = 0; x < GameMap.WIDTH; x++) {
                if (!gameMap.isWalkable(x, y) && !gameMap.isPlayerZone(x, y) && !gameMap.isCompZone(x, y) && !gameMap.isRoad(x, y)) {
                    hasObstacle = true;
                    break;
                }
            }
        }
        assertTrue(hasObstacle);
    }

    @Test
    void isWalkableReturnsFalseForOutOfBounds() {
        logger.info("Testing out-of-bounds coordinates...");

        boolean isWalkableNegative = gameMap.isWalkable(-1, -1);
        boolean isWalkableMax = gameMap.isWalkable(GameMap.WIDTH, GameMap.HEIGHT);

        if (!isWalkableNegative) {
            logger.severe("Error: isWalkable returned false for out-of-bounds coordinates (-1,-1).");
        }
        if (!isWalkableMax) {
            logger.severe("Error: isWalkable returned false for out-of-bounds coordinates (" + GameMap.WIDTH + "," + GameMap.HEIGHT + ").");
        }

        assertFalse(isWalkableNegative, "Expected isWalkable to return false for (-1,-1)");
        assertFalse(isWalkableMax, "Expected isWalkable to return false for (" + GameMap.WIDTH + "," + GameMap.HEIGHT + ")");
    }


    @Test
    void isPlayerZoneReturnsTrueForPlayerZone() {
        logger.info("Testing if (0, 0) is a Player Zone...");
        assertTrue(gameMap.isPlayerZone(0, 0));
        logger.info("Testing if (5, 5) is NOT a Player Zone...");
        assertFalse(gameMap.isPlayerZone(5, 5));
    }

    @Test
    void isCompZoneReturnsTrueForCompZone() {
        logger.info("Testing if (9, 9) is a Computer Zone...");
        assertTrue(gameMap.isCompZone(9, 9));
        logger.info("Testing if (4, 4) is NOT a Computer Zone...");
        assertFalse(gameMap.isCompZone(4, 4));
    }

    @Test
    void isRoadReturnsTrueForRoad() {
        logger.info("Testing if (2, 2) is a Road...");
        assertTrue(gameMap.isRoad(2, 2));
        logger.info("Testing if (0, 1) is NOT a Road...");
        assertFalse(gameMap.isRoad(0, 1));
    }

    @Test
    void getMoveCostReturnsRandomValueForRoad() {
        logger.info("Testing the move cost for a road tile...");
        int cost = gameMap.getMoveCost(2, 2);
        logger.info("Move cost for road tile: " + cost);
        assertTrue(cost >= 0 && cost <= 20);
    }

    @Test
    void getMoveCostReturnsZeroForNonRoad() {
        logger.info("Testing the move cost for a non-road tile...");
        assertEquals(0, gameMap.getMoveCost(0, 1));
    }

    @Test
    void getPlayerMoveStepReturnsCorrectValues() {
        logger.info("Testing Player move steps for various tiles...");
        assertEquals(5, gameMap.getPlayerMoveStep(1, 0)); // Player zone
        assertEquals(10, gameMap.getPlayerMoveStep(8, 7)); // Computer zone
        assertEquals(0, gameMap.getPlayerMoveStep(2, 2)); // Road
        assertEquals(1, gameMap.getPlayerMoveStep(5, 0)); // Neutral zone
    }

    @Test
    void getCompMoveStepReturnsCorrectValues() {
        logger.info("Testing Computer move steps for various tiles...");
        assertEquals(10, gameMap.getCompMoveStep(1, 0)); // Player zone
        assertEquals(5, gameMap.getCompMoveStep(8, 7)); // Computer zone
        assertEquals(0, gameMap.getCompMoveStep(2, 2)); // Road
        assertEquals(1, gameMap.getCompMoveStep(5, 0)); // Neutral zone
    }

    @Test
    void addUnitAddsUnitCorrectly() {
        logger.info("Testing unit addition to the map...");
        Castle castle = new Castle("Игрок", 0, 0);
        Unit unit = new Spearman(1, 1, true, castle);

        gameMap.addUnit(unit);
        logger.info("Checking if unit is added at position (1, 1)...");
        assertEquals(unit, gameMap.getUnitAt(1, 1));
    }

    @Test
    void isWalkableReturnsTrueForWalkableTiles() {
        logger.info("Testing walkability for walkable tiles...");
        assertTrue(gameMap.isWalkable(1, 0));
        assertTrue(gameMap.isWalkable(5, 5));
    }

    @Test
    void isWalkableReturnsFalseForObstacles() {
        logger.info("Testing walkability for obstacles...");
        for (int y = 0; y < GameMap.HEIGHT; y++) {
            for (int x = 0; x < GameMap.WIDTH; x++) {
                if (!gameMap.isWalkable(x, y) && !gameMap.isPlayerZone(x, y) && !gameMap.isCompZone(x, y) && !gameMap.isRoad(x, y)) {
                    logger.warning("Found an obstacle at (" + x + "," + y + ")");
                    assertFalse(gameMap.isWalkable(x, y));
                    return;
                }
            }
        }
    }

    @Test
    void getPlayerMoveStepReturnsCorrectValuesForInvalidCoordinates() {
        logger.info("Testing getPlayerMoveStep for invalid coordinates...");

        int moveStepNegative = gameMap.getPlayerMoveStep(-1, -1);
        int moveStepMax = gameMap.getPlayerMoveStep(GameMap.WIDTH, GameMap.HEIGHT);

        if (moveStepNegative == 1) {
            logger.severe("Error: getPlayerMoveStep returned default value (1) for invalid coordinates (-1,-1).");
        }
        if (moveStepMax == 1) {
            logger.severe("Error: getPlayerMoveStep returned default value (1) for invalid coordinates (" + GameMap.WIDTH + "," + GameMap.HEIGHT + ").");
        }

        assertEquals(1, moveStepNegative, "Expected getPlayerMoveStep to return 1 for (-1,-1)");
        assertEquals(1, moveStepMax, "Expected getPlayerMoveStep to return 1 for (" + GameMap.WIDTH + "," + GameMap.HEIGHT + ")");
    }

    @Test
    void getCompMoveStepReturnsCorrectValuesForInvalidCoordinates() {
        logger.info("Testing Computer move step for invalid coordinates...");

        int step = gameMap.getCompMoveStep(-1, -1); // Invalid coordinates
        if (step == -1) { // Assuming -1 or another specific value is returned for invalid input
            logger.severe("Error: Invalid coordinates (-1,-1) detected in getCompMoveStep.");
        }

        assertNotEquals(-1, step, "Expected getCompMoveStep to handle invalid coordinates safely.");
    }

    @Test
    void addUnitWithInvalidPosition() {
        logger.info("Testing unit addition with invalid position...");

        Castle castle = new Castle("Игрок", -1, -1);
        Unit unit = new Spearman(-1, -1, true, castle);

        boolean result = gameMap.addUnit(unit);
        if (!result) {
            logger.severe("Error: Cannot add unit at invalid coordinates (-1,-1).");
        }

        assertFalse(result, "Expected addUnit to handle invalid coordinates safely.");
    }
}
