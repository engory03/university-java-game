package main.map;

public class Tile {
    public enum Type {
        GRASS,      // Обычная земля
        ROAD,       // Дорога
        OBSTACLE,   // Препятствие
        CASTLE_PLAYER,
        CASTLE_COMP, // Замок
        PLAYER_ZONE,    // Зона игрока
        COMP_ZONE        // Зона компьютера
    }

    private Type type;

    public Tile(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) { // Позволяет менять тип клетки (например, дорога → замок)
        this.type = type;
    }

    @Override
    public String toString() {
        return switch (type) {
            case GRASS -> ".";
            case ROAD -> "=";
            case OBSTACLE -> "#";
            case CASTLE_PLAYER -> "🏰";
            case CASTLE_COMP -> "🏯";
            case PLAYER_ZONE -> "+"; // Зона игрока
            case COMP_ZONE -> "-";    // Зона компьютера
        };
    }
}
