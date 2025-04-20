package main.units;

import main.buildings.Castle;

// Паладин (5 уровень)
public class Paladin extends Unit {
    public Paladin(int x, int y, boolean isPlayer, Castle castle) {
        super("Паладин", 100, 30, 4, 2, 25, x, y, isPlayer, castle);
    }
}