package test.units;

import main.buildings.Castle;
import main.units.Cavalryman;
import main.map.GameMap;
import main.units.Unit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class CavalrymanTest {

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
        Cavalryman cavalryman = new Cavalryman(0, 0, true, castle);

        assertEquals("Кавалерист", cavalryman.getType());
        assertEquals(60, cavalryman.getHp());
        assertEquals(25, cavalryman.getAttack());
        assertEquals(5, cavalryman.getMovement());
        assertEquals(4, cavalryman.getRange());
        assertEquals(15, cavalryman.getCost());
        assertEquals(0, cavalryman.getX());
        assertEquals(0, cavalryman.getY());
        assertTrue(cavalryman.isPlayer());
    }

    @Test
    void getSymbolReturnsCorrectSymbolForPlayerCavalryman() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Cavalryman cavalryman = new Cavalryman(0, 0, true, castle);

        assertEquals('В', cavalryman.getSymbol());
    }

    @Test
    void getSymbolReturnsCorrectSymbolForComputerCavalryman() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Cavalryman cavalryman = new Cavalryman(0, 0, false, castle);

        assertEquals('в', cavalryman.getSymbol());
    }

    @Test
    void attackTargetInRangeReducesTargetHp() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Cavalryman cavalryman = new Cavalryman(0, 0, true, castle);
        Unit target = new Unit("Копейщик", 50, 10, 2, 2, 5, 3, 0, false, castle);

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

        cavalryman.attack(3, 0, map);
        assertEquals(25, target.getHp());
    }

    @Test
    void attackTargetOutOfRangePrintsErrorMessage() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Cavalryman cavalryman = new Cavalryman(0, 0, true, castle);
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

        cavalryman.attack(5, 0, map);
        assertEquals(50, target.getHp());
    }

    @Test
    void moveValidMoveUpdatesPositionAndSpendsSteps() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Cavalryman cavalryman = new Cavalryman(0, 0, true, castle);
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

        cavalryman.move(2, 0, map, castle);
        assertEquals(2, cavalryman.getX());
        assertEquals(0, cavalryman.getY());
        assertEquals(5, castle.getSteps());
    }
}
