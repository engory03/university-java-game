package test.units;

import main.buildings.Castle;
import main.map.GameMap;
import main.units.Spearman;
import main.units.Unit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class SpearmanTest {

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
        Spearman spearman = new Spearman(0, 0, true, castle);

        assertEquals("Копейщик", spearman.getType());
        assertEquals(50, spearman.getHp());
        assertEquals(10, spearman.getAttack());
        assertEquals(2, spearman.getMovement());
        assertEquals(2, spearman.getRange());
        assertEquals(5, spearman.getCost());
        assertEquals(0, spearman.getX());
        assertEquals(0, spearman.getY());
        assertTrue(spearman.isPlayer());
    }

    @Test
    void getSymbolReturnsCorrectSymbolForPlayerSpearman() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Spearman spearman = new Spearman(0, 0, true, castle);

        assertEquals('К', spearman.getSymbol());
    }

    @Test
    void getSymbolReturnsCorrectSymbolForComputerSpearman() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Spearman spearman = new Spearman(0, 0, false, castle);

        assertEquals('к', spearman.getSymbol());
    }

    @Test
    void attack_targetInRangeReducesTargetHp() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Spearman spearman = new Spearman(0, 0, true, castle);
        Unit target = new Unit("Арбалетчик", 100, 8, 2, 3, 40, 1, 0, false, castle);

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

        spearman.attack(1, 0, map);
        assertEquals(90, target.getHp());
    }

    @Test
    void moveValidMoveUpdatesPositionAndSpendsSteps() {
        Castle castle = createMockCastle(10, 100, new ArrayList<>());
        Spearman spearman = new Spearman(0, 0, true, castle);
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

        spearman.move(1, 0, map, castle);
        assertEquals(1, spearman.getX());
        assertEquals(0, spearman.getY());
        assertEquals(5, castle.getSteps());
    }
}
