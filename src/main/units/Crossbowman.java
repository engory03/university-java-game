package main.units;

import main.buildings.Castle;

// Арбалетчик (2 уровень)
public class Crossbowman extends Unit {
    public Crossbowman(int x, int y, boolean isPlayer, Castle castle) {
        super("Арбалетчик", 30, 20, 2, 8, 8, x, y, isPlayer, castle);
    }
}