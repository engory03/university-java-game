package main.units;

import main.buildings.Castle;

// Мечник (3 уровень)
public class Swordsman extends Unit {
    public Swordsman(int x, int y, boolean isPlayer, Castle castle) {
        super("Мечник", 70, 15, 3, 2, 10, x, y, isPlayer, castle);
    }
}