package main.units;

import main.buildings.Castle;

// Герой
public class Hero extends Unit {
    public Hero(int x, int y, boolean isPlayer, Castle castle) {
        super("Герой", 120, 40, 8, 2, 50, x, y, isPlayer, castle);
    }

}