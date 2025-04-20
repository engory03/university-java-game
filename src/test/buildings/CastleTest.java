package test.buildings;

import main.buildings.Castle;
import main.units.Hero;
import main.units.Spearman;
import main.units.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CastleTest {

    private Castle castle;

    @BeforeEach
    void setUp() {
        castle = new Castle("Игрок", 0, 0);
    }

    @Test
    void constructorInitializesFieldsCorrectly() {
        assertEquals("Игрок", castle.getOwner());
        assertEquals(0, castle.getX());
        assertEquals(0, castle.getY());
        assertEquals(100, castle.getGold());
        assertEquals(10, castle.getSteps());
        assertTrue(castle.getBuildings().contains("Сторожевой пост"));
        assertTrue(castle.getUnits().isEmpty());
    }

    @Test
    void addGoldIncreasesGold() {
        castle.addGold(50);
        assertEquals(150, castle.getGold());
    }

    @Test
    void spendGoldDecreasesGold() {
        castle.spendGold(50);
        assertEquals(50, castle.getGold());
    }

    @Test
    void spendGoldDoesNotAllowNegativeGold() {
        castle.spendGold(150);
        assertEquals(100, castle.getGold());
    }

    @Test
    void buyUnitAddsUnitIfEnoughGoldAndBuildingExists() {
        Unit spearman = new Spearman(0, 0, true, castle);

        assertTrue(castle.buyUnit(spearman));
        assertEquals(95, castle.getGold());
        assertTrue(castle.getUnits().contains(spearman));
    }

    @Test
    void buyUnitFailsIfNotEnoughGold() {
        castle.spendGold(96); // Leave only 4 gold
        Unit spearman = new Spearman(0, 0, true, castle);

        assertFalse(castle.buyUnit(spearman));
        assertEquals(4, castle.getGold());
        assertFalse(castle.getUnits().contains(spearman));
    }

    @Test
    void buyUnitFailsIfRequiredBuildingMissing() {
        Unit hero = new Hero(0, 0, true, castle);

        assertFalse(castle.buyUnit(hero));
        assertEquals(100, castle.getGold());
        assertFalse(castle.getUnits().contains(hero));
    }

    @Test
    void buildAddsBuildingIfEnoughGold() {
        assertTrue(castle.build("Таверна"));
        assertEquals(95, castle.getGold());
        assertTrue(castle.getBuildings().contains("Таверна"));
    }

    @Test
    void buildFailsIfNotEnoughGold() {
        castle.spendGold(96);
        assertFalse(castle.build("Таверна"));
        assertEquals(4, castle.getGold());
        assertFalse(castle.getBuildings().contains("Таверна"));
    }

    @Test
    void buildFailsIfBuildingAlreadyExists() {
        assertTrue(castle.build("Таверна"));
        assertFalse(castle.build("Таверна"));
        assertEquals(95, castle.getGold());
    }

    @Test
    void checkBuildingReturnsTrueIfBuildingExists() {
        assertTrue(castle.checkBuilding("Копейщик"));
    }

    @Test
    void checkBuildingReturnsFalseIfBuildingMissing() {
        assertFalse(castle.checkBuilding("Герой"));
    }

    @Test
    void getBuildingForUnitReturnsCorrectBuilding() {
        assertEquals("Сторожевой пост", castle.getBuildingForUnit("Копейщик"));
        assertEquals("Таверна", castle.getBuildingForUnit("Герой"));
    }

    @Test
    void removeUnitRemovesUnitFromList() {
        Unit spearman = new Spearman(0, 0, true, castle);
        castle.buyUnit(spearman);

        castle.removeUnit(spearman);
        assertFalse(castle.getUnits().contains(spearman));
    }

    @Test
    void spendStepsDecreasesSteps() {
        castle.spendSteps(5);
        assertEquals(5, castle.getSteps());
    }

    @Test
    void resetStepsResetsStepsToDefault() {
        castle.spendSteps(5);
        castle.resetSteps();
        assertEquals(10, castle.getSteps());
    }
}
