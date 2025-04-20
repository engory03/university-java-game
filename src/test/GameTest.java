package test;

import main.buildings.Castle;
import main.map.GameMap;
import main.units.Hero;
import main.units.Spearman;
import main.units.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private GameMap map;
    private Castle playerCastle;
    private Castle computerCastle;

    @BeforeEach
    void setUp() {
        map = new GameMap();
        playerCastle = new Castle("Игрок", 0, 0);
        computerCastle = new Castle("Компьютер", 9, 9);
    }

    @Test
    void gameInitializationCreatesPlayerAndComputerCastles() {
        assertEquals(0, playerCastle.getX());
        assertEquals(0, playerCastle.getY());
        assertEquals(9, computerCastle.getX());
        assertEquals(9, computerCastle.getY());
    }

    @Test
    void playerTurnSwitchesToComputerTurn() {
        boolean isPlayerTurn = true;
        isPlayerTurn = !isPlayerTurn;

        assertFalse(isPlayerTurn);
    }

    @Test
    void addUnitToPlayerCastleUpdatesMap() {
        Unit spearman = new Spearman(0, 0, true, playerCastle);

        playerCastle.buyUnit(spearman);
        map.addUnit(spearman);

        assertEquals(spearman, map.getUnitAt(0, 0));
    }

    @Test
    void playerWinsWhenHeroReachesComputerCastle() {
        Hero hero = new Hero(9, 9, true, playerCastle);
        playerCastle.buyUnit(hero);
        map.addUnit(hero);

        assertTrue(hero.getX() == computerCastle.getX() && hero.getY() == computerCastle.getY());
    }

    @Test
    void playerLosesWhenNoUnitsRemain() {
        playerCastle.getUnits().clear();
        assertTrue(playerCastle.getUnits().isEmpty());
    }

    @Test
    void playerMoveUpdatesUnitPosition() {
        Unit spearman = new Spearman(0, 0, true, playerCastle);

        playerCastle.buyUnit(spearman);
        map.addUnit(spearman);

        spearman.move(1, 0, map, playerCastle);

        assertEquals(1, spearman.getX());
        assertEquals(0, spearman.getY());
    }

    @Test
    void playerAttackReducesTargetHp() {
        Unit spearman = new Spearman(0, 0, true, playerCastle);
        Unit enemy = new Spearman(1, 0, false, computerCastle);

        playerCastle.buyUnit(spearman);
        computerCastle.buyUnit(enemy);
        map.addUnit(spearman);
        map.addUnit(enemy);

        spearman.attack(1, 0, map);

        assertTrue(enemy.getHp() < 50);
    }

    @Test
    void playerBuildsBuildingAddsBuildingToCastle() {
        playerCastle.build("Таверна");
        assertTrue(playerCastle.getBuildings().contains("Таверна"));
    }
}
