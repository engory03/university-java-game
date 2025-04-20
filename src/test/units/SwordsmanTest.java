package test.units;

import main.buildings.Castle;
import main.map.GameMap;
import main.units.Swordsman;
import main.units.Unit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;


class SwordsmanTest {

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
        Swordsman swordsman = new Swordsman(0, 0, true, castle);

        assertEquals("Мечник", swordsman.getType());
        assertEquals(70, swordsman.getHp());
        assertEquals(15, swordsman.getAttack());
        assertEquals(3, swordsman.getMovement());
        assertEquals(2, swordsman.getRange());
        assertEquals(10, swordsman.getCost());
        assertEquals(0, swordsman.getX());
        assertEquals(0, swordsman.getY());
        assertTrue(swordsman.isPlayer());
    }

    @Test
    void getSymbolReturnsCorrectSymbolForPlayerSwordsman() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Swordsman swordsman = new Swordsman(0, 0, true, castle);

        assertEquals('М', swordsman.getSymbol());
    }

    @Test
    void getSymbolReturnsCorrectSymbolForComputerSwordsman() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Swordsman swordsman = new Swordsman(0, 0, false, castle);

        assertEquals('м', swordsman.getSymbol());
    }

    @Test
    void attackTargetInRangeReducesTargetHp() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Swordsman swordsman = new Swordsman(0, 0, true, castle);
        Unit target = new Unit("Копейщик", 50, 10, 2, 2, 5, 1, 0, false, castle);

        GameMap map = new GameMap() {
            @Override
            public Unit getUnitAt(int x, int y) {
                return target;
            }
        };

        swordsman.attack(1, 0, map);
        assertEquals(35, target.getHp());
    }

    @Test
    void attackTargetOutOfRangePrintsErrorMessage() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Swordsman swordsman = new Swordsman(0, 0, true, castle);
        Unit target = new Unit("Копейщик", 50, 10, 2, 2, 5, 3, 0, false, castle);

        GameMap map = new GameMap() {
            @Override
            public Unit getUnitAt(int x, int y) {
                return target;
            }
        };

        swordsman.attack(3, 0, map);
        assertEquals(50, target.getHp());
    }

    @Test
    void moveValidMoveUpdatesPositionAndSpendsSteps() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Swordsman swordsman = new Swordsman(0, 0, true, castle);
        GameMap map = new GameMap() {
            @Override
            public boolean isWalkable(int x, int y) {
                return true;
            }

            @Override
            public int getPlayerMoveStep(int x, int y) {
                return 5;
            }
        };

        swordsman.move(2, 0, map, castle);
        assertEquals(2, swordsman.getX());
        assertEquals(0, swordsman.getY());
        assertEquals(5, castle.getSteps());
    }
}