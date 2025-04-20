package test.units;

import main.buildings.Castle;
import main.units.Crossbowman;
import main.map.GameMap;
import main.units.Unit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class CrossbowmanTest {

    private Castle createMockCastle(int steps, int gold, List<Unit> units) {
        return new Castle("Игрок", 0, 0) {
            private int currentSteps = steps;
            private int currentGold = gold;
            private List<Unit> currentUnits = new ArrayList<>(units);

            @Override
            public int getSteps() {
                return currentSteps;
            }

            @Override
            public void spendSteps(int steps) {
                currentSteps -= steps;
            }

            @Override
            public int getGold() {
                return currentGold;
            }

            @Override
            public void spendGold(int gold) {
                currentGold -= gold;
            }

            @Override
            public void addGold(int gold) {
                currentGold += gold;
            }

            @Override
            public List<Unit> getUnits() {
                return currentUnits;
            }

            @Override
            public void removeUnit(Unit unit) {
                currentUnits.remove(unit);
            }

            @Override
            public String getOwner() {
                return "Player";
            }
        };
    }

    @Test
    void constructorInitializesFieldsCorrectly() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Crossbowman crossbowman = new Crossbowman(0, 0, true, castle);

        assertEquals("Арбалетчик", crossbowman.getType());
        assertEquals(30, crossbowman.getHp());
        assertEquals(20, crossbowman.getAttack());
        assertEquals(2, crossbowman.getMovement());
        assertEquals(8, crossbowman.getRange());
        assertEquals(8, crossbowman.getCost());
        assertEquals(0, crossbowman.getX());
        assertEquals(0, crossbowman.getY());
        assertTrue(crossbowman.isPlayer());
    }

    @Test
    void getSymbolReturnsCorrectSymbolForPlayerCrossbowman() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Crossbowman crossbowman = new Crossbowman(0, 0, true, castle);

        assertEquals('А', crossbowman.getSymbol());
    }

    @Test
    void getSymbolReturnsCorrectSymbolForComputerCrossbowman() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Crossbowman crossbowman = new Crossbowman(0, 0, false, castle);

        assertEquals('а', crossbowman.getSymbol());
    }

    @Test
    void attackTargetInRangeReducesTargetHp() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Crossbowman crossbowman = new Crossbowman(0, 0, true, castle);
        Unit target = new Unit("Копейщик", 50, 10, 2, 2, 5, 5, 0, false, castle);

        GameMap map = new GameMap() {
            @Override
            public boolean isWalkable(int x, int y) {
                return true;
            }

            @Override
            public int getPlayerMoveStep(int x, int y) {
                return 1;
            }

            @Override
            public int getCompMoveStep(int x, int y) {
                return 1;
            }

            @Override
            public int getMoveCost(int x, int y) {
                return 0;
            }

            @Override
            public Unit getUnitAt(int x, int y) {
                return target;
            }
        };

        crossbowman.attack(5, 0, map);
        assertEquals(30, target.getHp());
    }

    @Test
    void attackTargetOutOfRangePrintsErrorMessage() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Crossbowman crossbowman = new Crossbowman(0, 0, true, castle);
        Unit target = new Unit("Копейщик", 50, 10, 2, 2, 5, 10, 0, false, castle);

        GameMap map = new GameMap() {
            @Override
            public boolean isWalkable(int x, int y) {
                return true;
            }

            @Override
            public int getPlayerMoveStep(int x, int y) {
                return 1;
            }

            @Override
            public int getCompMoveStep(int x, int y) {
                return 1;
            }

            @Override
            public int getMoveCost(int x, int y) {
                return 0;
            }

            @Override
            public Unit getUnitAt(int x, int y) {
                return target;
            }
        };

        crossbowman.attack(10, 0, map);
        assertEquals(50, target.getHp());
    }

    @Test
    void moveValidMoveUpdatesPositionAndSpendsSteps() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Crossbowman crossbowman = new Crossbowman(0, 0, true, castle);
        GameMap map = new GameMap() {
            @Override
            public boolean isWalkable(int x, int y) {
                return true;
            }

            @Override
            public int getPlayerMoveStep(int x, int y) {
                return 5;
            }

            @Override
            public int getCompMoveStep(int x, int y) {
                return 1;
            }

            @Override
            public int getMoveCost(int x, int y) {
                return 0;
            }

            @Override
            public Unit getUnitAt(int x, int y) {
                return null;
            }
        };

        crossbowman.move(1, 0, map, castle);
        assertEquals(1, crossbowman.getX());
        assertEquals(0, crossbowman.getY());
        assertEquals(5, castle.getSteps());
    }
}
