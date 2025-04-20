package main.units;

import main.buildings.Castle;

// Кавалерист (4 уровень)
public class Cavalryman extends Unit {
    public Cavalryman(int x, int y, boolean isPlayer, Castle castle) {
        super("Кавалерист", 60, 25, 5, 4, 15, x, y, isPlayer, castle);
    }
}