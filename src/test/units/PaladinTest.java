package test.units;

import main.buildings.Castle;
import main.map.GameMap;
import main.units.Paladin;
import main.units.Unit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class PaladinTest {

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
        Paladin paladin = new Paladin(0, 0, true, castle);

        assertEquals("Паладин", paladin.getType());
        assertEquals(100, paladin.getHp());
        assertEquals(30, paladin.getAttack());
        assertEquals(4, paladin.getMovement());
        assertEquals(2, paladin.getRange());
        assertEquals(25, paladin.getCost());
        assertEquals(0, paladin.getX());
        assertEquals(0, paladin.getY());
        assertTrue(paladin.isPlayer());
    }

    @Test
    void getSymbolReturnsCorrectSymbolForPlayerPaladin() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Paladin paladin = new Paladin(0, 0, true, castle);

        assertEquals('П', paladin.getSymbol());
    }

    @Test
    void getSymbolReturnsCorrectSymbolForComputerPaladin() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Paladin paladin = new Paladin(0, 0, false, castle);

        assertEquals('п', paladin.getSymbol());
    }

    @Test
    void attackTargetInRangeReducesTargetHp() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Paladin paladin = new Paladin(0, 0, true, castle);
        Unit target = new Unit("Копейщик", 50, 10, 2, 2, 5, 1, 0, false, castle);

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

        paladin.attack(1, 0, map);
        assertEquals(20, target.getHp());
    }

    @Test
    void attackTargetOutOfRange_printsErrorMessage() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Paladin paladin = new Paladin(0, 0, true, castle);
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

        paladin.attack(3, 0, map);
        assertEquals(50, target.getHp());
    }

    @Test
    void moveValidMoveUpdatesPositionAndSpendsSteps() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Paladin paladin = new Paladin(0, 0, true, castle);
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

        paladin.move(2, 0, map, castle);
        assertEquals(2, paladin.getX());
        assertEquals(0, paladin.getY());
        assertEquals(5, castle.getSteps());
    }
}
