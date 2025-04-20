package main.map;

import main.units.Unit;

import java.util.*;

public class GameMap {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;
    private Tile[][] grid;
    private List<Unit> units;

    // Зоны игрока и компьютера
    public static final int PLAYER_ZONE_SIZE = 5;
    public static final int COMP_ZONE_SIZE = 5;

    public GameMap() {
        this(false);
    }

    public GameMap(boolean empty) {
        grid = new Tile[HEIGHT][WIDTH];
        units = new ArrayList<>();
        if (empty) {
            generateEmptyMap();
        } else {
            generateMap();
        }
    }

    private void generateEmptyMap() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                grid[y][x] = new Tile(Tile.Type.GRASS);
            }
        }
    }

    private void generateMap() {
        Random random = new Random();

        // Заполняем карту травой
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                grid[y][x] = new Tile(Tile.Type.GRASS);
            }
        }

        // Устанавливаем замки
        grid[0][0].setType(Tile.Type.CASTLE_PLAYER);               // Замок игрока (верхний левый угол)
        grid[HEIGHT - 1][WIDTH - 1].setType(Tile.Type.CASTLE_COMP); // Замок компьютера (нижний правый угол)

        // Создаём диагональную дорогу
        for (int i = 0; i < Math.min(WIDTH, HEIGHT); i++) {
            if (grid[i][i].getType() != Tile.Type.CASTLE_PLAYER && grid[i][i].getType() != Tile.Type.CASTLE_COMP) { // Если клетка не замок, делаем её дорогой
                grid[i][i].setType(Tile.Type.ROAD);
            }
        }

        // Добавляем зону игрока
        for (int y = 0; y < PLAYER_ZONE_SIZE; y++) {
            for (int x = 0; x < PLAYER_ZONE_SIZE; x++) {
                if (grid[y][x].getType() == Tile.Type.GRASS) {
                    grid[y][x].setType(Tile.Type.PLAYER_ZONE);
                }
            }
        }

        // Добавляем зону компьютера
        for (int y = HEIGHT - COMP_ZONE_SIZE; y < HEIGHT; y++) {
            for (int x = WIDTH - COMP_ZONE_SIZE; x < WIDTH; x++) {
                if (grid[y][x].getType() == Tile.Type.GRASS) {
                    grid[y][x].setType(Tile.Type.COMP_ZONE);
                }
            }
        }

        // Добавляем случайные препятствия (не заменяя дорогу и замки)
        for (int i = 0; i < 6; i++) {
            int x, y;
            do {
                x = random.nextInt(WIDTH);
                y = random.nextInt(HEIGHT);
            } while (grid[y][x].getType() != Tile.Type.GRASS); // Только на траве

            grid[y][x].setType(Tile.Type.OBSTACLE);
        }
    }

    // Проверка, находится ли клетка в зоне игрока
    public boolean isPlayerZone(int x, int y) {
        return x >= 0 && x < PLAYER_ZONE_SIZE && y >= 0 && y < PLAYER_ZONE_SIZE;
    }

    // Проверка, находится ли клетка в зоне компьютера
    public boolean isCompZone(int x, int y) {
        return x >= WIDTH - COMP_ZONE_SIZE && x < WIDTH && y >= HEIGHT - COMP_ZONE_SIZE && y < HEIGHT;
    }

    // Проверка, находится ли клетка на дороге
    public boolean isRoad(int x, int y) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            return false;
        }
        return grid[y][x].getType() == Tile.Type.ROAD;
    }

    // Получение стоимости перемещения для игрока
    public int getMoveCost(int x, int y) {
        if (isRoad(x, y)) {
            Random random = new Random();
            return random.nextInt(21); // Случайное число от 0 до 20
        } else {
            return 0;
        }
    }

    // Получение стоимости перемещения для игрока
    public int getPlayerMoveStep(int x, int y) {
        if (isRoad(x, y)) {
            return 0; // На своей зоне
        } else if (isCompZone(x, y)) {
            return 10; // На зоне компьютера
        } else if (isPlayerZone(x, y)) {
            return 5; // На дороге
        } else {
            return 1; // На нейтральной зоне
        }
    }

    // Получение стоимости перемещения для игрока
    public int getCompMoveStep(int x, int y) {
        if (isRoad(x, y)) {
            return 0; // На своей зоне
        } else if (isCompZone(x, y)) {
            return 5; // На зоне компьютера
        } else if (isPlayerZone(x, y)) {
            return 10; // На дороге
        } else {
            return 1; // На нейтральной зоне
        }
    }

    public boolean addUnit(Unit unit) {
        if (unit.getX() < 0 || unit.getY() < 0 || unit.getX() >= WIDTH || unit.getY() >= HEIGHT) {
            return false;
        }

        units.add(unit);
        return true;
    }

    public Unit getUnitAt(int x, int y) {
        for (Unit unit : units) {
            if (unit.getX() == x && unit.getY() == y) {
                return unit;
            }
        }
        return null;
    }

//    public boolean isWalkable(int x, int y) {
//        String tileSymbol = grid[y][x].toString();
//        return x >= 0 && x < WIDTH
//                && y >= 0 && y < HEIGHT
//                && grid[y][x].getType() != Tile.Type.OBSTACLE
//                && !"КкАаМмВвПпГг".contains(tileSymbol);
//    }

    public boolean isWalkable(int x, int y) {
        if ((x >= 0 && x < WIDTH)
                && (y >= 0 && y < HEIGHT)) {
            String tileSymbol = grid[y][x].toString();
            return ((grid[y][x].getType() != Tile.Type.OBSTACLE)
                    && (!"КкАаМмВвПпГг".contains(tileSymbol)));
        } else {
            return false;
        }
    }


    public void printMap() {
//        for (int y = 0; y < HEIGHT; y++) {
//            for (int x = 0; x < WIDTH; x++) {
//                System.out.print(grid[y][x] + " ");
//            }
//            System.out.println();
//        }

        char[][] display = new char[HEIGHT][WIDTH];
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                display[y][x] = grid[y][x].toString().charAt(0);
            }
        }
        for (Unit unit : units) {
            display[unit.getY()][unit.getX()] = unit.getSymbol();
        }
        for (char[] row : display) {
            System.out.println(new String(row));
        }
    }

    public void setTile(int x, int y, Tile.Type type) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            grid[y][x].setType(type);
        }
    }

    public Tile getTile(int x, int y) {
        return grid[y][x];
    }
}
