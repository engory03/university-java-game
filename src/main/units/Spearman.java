package main.units;

import main.buildings.Castle;

// Копейщик (1 уровень)
public class Spearman extends Unit {
    public Spearman(int x, int y, boolean isPlayer, Castle castle) {
        super("Копейщик", 50, 10, 2, 2, 5, x, y, isPlayer, castle);
    }
}