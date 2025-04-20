package test.units;

import main.buildings.Castle;
import main.map.GameMap;
import main.units.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class UnitTest {

    private Castle castle;

    @BeforeEach
    void setUp() {
        castle = createMockCastle(10, 100, new ArrayList<>());
    }

    private GameMap createMockGameMap(boolean isWalkable, int playerStepCost, int compStepCost, int moveCost) {
        return new GameMap() {
            @Override
            public boolean isWalkable(int x, int y) {
                return isWalkable;
            }

            @Override
            public int getPlayerMoveStep(int x, int y) {
                return playerStepCost;
            }

            @Override
            public int getCompMoveStep(int x, int y) {
                return compStepCost;
            }

            @Override
            public int getMoveCost(int x, int y) {
                return moveCost;
            }

            @Override
            public Unit getUnitAt(int x, int y) {
                return null;
            }
        };
    }

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
        Unit unit = new Unit("Копейщик", 100, 10, 3, 1, 50, 0, 0, true, castle);

        assertEquals("Копейщик", unit.getType());
        assertEquals(100, unit.getHp());
        assertEquals(10, unit.getAttack());
        assertEquals(3, unit.getMovement());
        assertEquals(1, unit.getRange());
        assertEquals(50, unit.getCost());
        assertEquals(0, unit.getX());
        assertEquals(0, unit.getY());
        assertTrue(unit.isPlayer());
    }

    @Test
    void getSymbolReturnsCorrectSymbolForPlayerUnit() {
        Unit unit = new Unit("Копейщик", 100, 10, 3, 1, 50, 0, 0, true, castle);

        assertEquals('К', unit.getSymbol());
    }

    @Test
    void getSymbolReturnsCorrectSymbolForComputerUnit() {
        Unit unit = new Unit("Копейщик", 100, 10, 3, 1, 50, 0, 0, false, castle);

        assertEquals('к', unit.getSymbol());
    }

    @Test
    void attackTargetOutOfRangePrintsErrorMessage() {
        Unit attacker = new Unit("Копейщик", 100, 10, 3, 1, 50, 0, 0, true, castle);
        Unit target = new Unit("Арбалетчик", 100, 8, 2, 3, 40, 2, 2, false, castle);

        GameMap map = new GameMap() {
            @Override
            public Unit getUnitAt(int x, int y) {
                return target;
            }
        };

        attacker.attack(2, 2, map);
        assertEquals(100, target.getHp());
    }

    @Test
    void attackTargetInRangeReducesTargetHp() {
        Unit attacker = new Unit("Копейщик", 100, 10, 3, 1, 50, 0, 0, true, castle);
        Unit target = new Unit("Арбалетчик", 100, 8, 2, 3, 40, 1, 0, false, castle);

        GameMap map = new GameMap() {
            @Override
            public Unit getUnitAt(int x, int y) {
                return target;
            }
        };

        attacker.attack(1, 0, map);
        assertEquals(90, target.getHp());
    }

    @Test
    void moveValidMoveUpdatesPositionAndSpendsSteps() {
        Unit unit = new Unit("Копейщик", 100, 10, 3, 1, 50, 0, 0, true, castle);
        GameMap map = createMockGameMap(true, 5, 1, 0);

        unit.move(1, 0, map, castle);
        assertEquals(1, unit.getX());
        assertEquals(0, unit.getY());
        assertEquals(5, castle.getSteps());
    }

    @Test
    void moveInsufficientStepsPrintsErrorMessage() {
        Castle castle = createMockCastle(0, 100, new ArrayList<>());
        Unit unit = new Unit("Копейщик", 100, 10, 3, 1, 50, 0, 0, true, castle);
        GameMap map = createMockGameMap(true, 5, 1, 0);

        unit.move(1, 0, map, castle);
        assertEquals(0, unit.getX());
        assertEquals(0, unit.getY());
    }

    @Test
    void takeDamageReducesHp() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Unit unit = new Unit("Копейщик", 100, 10, 3, 1, 50, 0, 0, true, castle);

        unit.takeDamage(20);
        assertEquals(80, unit.getHp());
    }

    @Test
    void isAliveReturnsFalseWhenHpIsZero() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Unit unit = new Unit("Копейщик", 100, 10, 3, 1, 50, 0, 0, true, castle);

        unit.takeDamage(100);
        assertFalse(unit.isAlive());
    }
}
