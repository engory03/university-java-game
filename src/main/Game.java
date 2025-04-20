package main;

import main.buildings.Barbershop;
import main.buildings.Cafe;
import main.buildings.Castle;
import main.buildings.Hotel;
import main.map.GameMap;
import main.map.Tile;
import main.ui.Menu;
import main.units.*;

import java.io.*;
import java.util.*;

public class Game {
    private Scanner scanner;
    private Random random;
    public GameMap map;
    public Castle playerCastle;
    private Castle computerCastle;
    private boolean isPlayerTurn;
    private boolean castleCaptured;
    public String username;
    private String mapName;
    private final Hotel hotel = new Hotel();
    private final Cafe cafe = new Cafe();
    private final Barbershop barbershop = new Barbershop();
    private final List<Npc> npcs = new ArrayList<>();

    private void initNpcs() {
        for (int i = 0; i < 10; i++) {
            Npc npc = new Npc(i, hotel, cafe, barbershop);
            new Thread(npc).start();
            npcs.add(npc);
        }
    }

    public Game() {
        initNpcs();
        initializeGame();
    }

    private void initializeGame() {
        scanner = new Scanner(System.in);
        random = new Random();
        askForUsername();

        boolean proceed = false;
        while (!proceed) {
            proceed = showMapMenu();
        }

        // Find castle positions from loaded map
        int[] playerCastlePos = findCastlePosition(Tile.Type.CASTLE_PLAYER);
        int[] computerCastlePos = findCastlePosition(Tile.Type.CASTLE_COMP);

        // Use default positions if not found
        if (playerCastlePos == null) {
            playerCastlePos = new int[]{0, 0};
        }
        if (computerCastlePos == null) {
            computerCastlePos = new int[]{GameMap.WIDTH - 1, GameMap.HEIGHT - 1};
        }

        playerCastle = new Castle(username, playerCastlePos[0], playerCastlePos[1]);
        computerCastle = new Castle("Компьютер", computerCastlePos[0], computerCastlePos[1]);
        initializeStartingUnits();

        isPlayerTurn = true;
        castleCaptured = false;
    }

    private void askForUsername() {
        System.out.println("Добро пожаловать в игру!");
        System.out.print("Пожалуйста, введите ваше имя: ");
        username = scanner.nextLine().trim();

        System.out.println("Привет, " + username + "! Ваше имя сохранено.");
        displayBestScores();
        System.out.println("Начинаем игру...");
    }

    public void savePlayerRating() {
        File ratingFile = new File("rating.csv");
        boolean fileExists = ratingFile.exists();

        try (FileWriter fw = new FileWriter("rating.csv", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            // Write header if file is new
            if (!fileExists) {
                out.println("username;points;map");
            }

            out.println(username + ";" + playerCastle.getPoints() + ";" + mapName);

        } catch (IOException e) {
            System.out.println("Не удалось сохранить рейтинг: " + e.getMessage());
        }
    }

    private int[] findCastlePosition(Tile.Type castleType) {
        for (int y = 0; y < GameMap.HEIGHT; y++) {
            for (int x = 0; x < GameMap.WIDTH; x++) {
                if (map.getTile(x, y).getType() == castleType) {
                    return new int[]{x, y};
                }
            }
        }
        return null;
    }

    private void initializeStartingUnits() {
        Spearman spearman1 = new Spearman(playerCastle.getX(), playerCastle.getY(), true, playerCastle);
        playerCastle.buyUnit(spearman1);
        map.addUnit(spearman1);

        Spearman spearman2 = new Spearman(computerCastle.getX(), computerCastle.getY(), false, computerCastle);
        computerCastle.buyUnit(spearman2);
        map.addUnit(spearman2);
    }

    public void start() {
        try {
            while (true) {
                updateGameState();

                if (checkGameEndConditions()) {
                    savePlayerRating();
                    break;
                }

                if (isPlayerTurn) {
                    playerTurn();
                } else {
                    computerTurn();
                }

                isPlayerTurn = !isPlayerTurn;
                Menu.printTurnSeparator();
            }
        } finally {
            scanner.close();
        }
    }

    public void displayBestScores() {
        File ratingFile = new File("rating.csv");
        if (!ratingFile.exists()) {
            System.out.println("Рейтинговая таблица пока пуста.");
            return;
        }

        Map<String, Integer> bestScores = new HashMap<>();

        try (Scanner fileScanner = new Scanner(ratingFile)) {
            // Skip header line
            if (fileScanner.hasNextLine()) {
                fileScanner.nextLine();
            }

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(";");
                if (parts.length >= 2) {
                    String username = parts[0];
                    try {
                        int points = Integer.parseInt(parts[1]);
                        // Update best score for this user
                        bestScores.merge(username, points, Math::max);
                    } catch (NumberFormatException e) {
                        System.err.println("Ошибка формата очков в строке: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Не удалось прочитать рейтинговую таблицу: " + e.getMessage());
            return;
        }

        if (bestScores.isEmpty()) {
            System.out.println("Рейтинговая таблица пока пуста.");
            return;
        }

        System.out.println("\nЛучшие результаты игроков:");
        bestScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
        System.out.println();
    }

    private void updateGameState() {
        map.printMap();
        playerCastle.printCastleInfo();
        computerCastle.printCastleInfo();
    }

    private boolean checkGameEndConditions() {
        return checkPlayerVictoryConditions() ||
                checkPlayerDefeatConditions() ||
                checkCastleCapturedCondition();
    }

    private boolean checkPlayerVictoryConditions() {
        // Check if player's hero reached enemy castle
        for (Unit unit : playerCastle.getUnits()) {
            if (unit instanceof Hero &&
                    unit.getX() == computerCastle.getX() &&
                    unit.getY() == computerCastle.getY()) {
                playerCastle.addPoints(20);
                System.out.println("🎉 Герой игрока достиг вражеского замка! Победа!");
                System.out.println("+ 20 очков за победу!");
                return true;
            }
        }

        // Check if all enemy units are destroyed
        if (computerCastle.getUnits().isEmpty()) {
            playerCastle.addPoints(15);
            System.out.println("🎉 Все вражеские юниты уничтожены! Победа!");
            System.out.println("+ 15 очков за победу!");
            return true;
        }

        return false;
    }

    private boolean checkPlayerDefeatConditions() {
        // Check if player has no units
        if (playerCastle.getUnits().isEmpty()) {
            System.out.println("💀 Все ваши юниты уничтожены! Поражение...");
            savePlayerRating();
            return true;
        }

        // Check if enemy hero reached player's castle
        for (Unit unit : computerCastle.getUnits()) {
            if (unit instanceof Hero &&
                    unit.getX() == playerCastle.getX() &&
                    unit.getY() == playerCastle.getY()) {
                System.out.println("💀 Вражеский герой достиг вашего замка! Поражение...");
                savePlayerRating();
                return true;
            }
        }

        return false;
    }

    private boolean checkCastleCapturedCondition() {
        if (castleCaptured) {
            computerCastle.resetSteps();
            performCapturedCastleTurn();
            System.out.println("🎉 Победа! Замок противника захвачен!");
            return true;
        }
        return false;
    }

    // =========== Меню игрока ===========

    private void playerTurn() {
        Menu.showMainMenu();
        playerCastle.resetSteps();
        System.out.print("Ваш выбор: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                moveUnits(playerCastle);
                break;
            case 2:
                recruitUnit(playerCastle, true);
                break;
            case 3:
                System.out.println("Игрок пропускает ход.");
                break;
            case 4:
                attackWithUnits(playerCastle);
                break;
            case 5:
                buildBuilding(playerCastle);
                break;
            case 6:
                moveAndAttackUnits(playerCastle);
                break;
            case 7:
                saveGameState();
                break;
            case 8:
                loadGame();
                break;
            case 9:
                visitHotel();
                break;
            case 10:
                visitCafe();
                break;
            case 11:
                 visitBarbershop();
                break;
            default:
                System.out.println("Неверный ввод, попробуйте снова.");
        }
    }

    // =========== Меню игрока ===========

    private void computerTurn() {
        computerCastle.resetSteps();
        int aiChoice = random.nextInt(5) + 1;
        System.out.println("Ход компьютера. Компьютер выбирает: " + aiChoice);

        switch (aiChoice) {
            case 1:
                moveUnits(computerCastle);
                break;
            case 2:
                recruitUnit(computerCastle, false);
                break;
            case 3:
                System.out.println("Компьютер пропускает ход.");
                break;
            case 4:
                attackWithUnits(computerCastle);
                break;
            case 5:
                buildBuilding(computerCastle);
                break;
        }
    }

    private void moveUnits(Castle castle) {
        if (castle.getUnits().isEmpty()) {
            System.out.println("Нет юнитов для перемещения!");
            return;
        }

        for (Unit unit : castle.getUnits()) {
            int newX, newY;
            if (castle == playerCastle) {
                System.out.println("Выберите новую позицию для " + unit.getSymbol());
                System.out.print("Введите X: ");
                newX = scanner.nextInt();
                System.out.print("Введите Y: ");
                newY = scanner.nextInt();
            } else {
                newX = random.nextInt(GameMap.WIDTH);
                newY = random.nextInt(GameMap.HEIGHT);
                System.out.println("Компьютер вводит X: " + newX + ", Y: " + newY);
            }
            unit.move(newX, newY, map, castle);
        }

        // Remove units with zero HP
        castle.getUnits().removeIf(unit -> unit.hp == 0);
    }

    private void recruitUnit(Castle castle, boolean isPlayer) {
        Unit unit;
        if (isPlayer) {
            Menu.showUnitsMenu();
            System.out.print("Ваш выбор: ");
            int choiceUnit = scanner.nextInt();
            unit = createPlayerUnit(choiceUnit, castle);
        } else {
            int aiChoiceUnit = random.nextInt(6) + 1;
            unit = createComputerUnit(aiChoiceUnit, castle);
        }

        if (unit != null) {
            boolean ok = castle.buyUnit(unit);
            if (ok) {
                map.addUnit(unit);
                if (isPlayer) saveGameState();
            }
        }
    }

    private Unit createPlayerUnit(int choice, Castle castle) {
        return switch (choice) {
            case 1 -> new Spearman(castle.getX(), castle.getY(), true, castle);
            case 2 -> new Crossbowman(castle.getX(), castle.getY(), true, castle);
            case 3 -> new Swordsman(castle.getX(), castle.getY(), true, castle);
            case 4 -> new Cavalryman(castle.getX(), castle.getY(), true, castle);
            case 5 -> new Paladin(castle.getX(), castle.getY(), true, castle);
            case 6 -> new Hero(castle.getX(), castle.getY(), true, castle);
            default -> {
                System.out.println("Неверный ввод, попробуйте снова.");
                yield null;
            }
        };
    }

    private Unit createComputerUnit(int choice, Castle castle) {
        return switch (choice) {
            case 1 -> new Spearman(castle.getX(), castle.getY(), false, castle);
            case 2 -> new Crossbowman(castle.getX(), castle.getY(), false, castle);
            case 3 -> new Swordsman(castle.getX(), castle.getY(), false, castle);
            case 4 -> new Cavalryman(castle.getX(), castle.getY(), false, castle);
            case 5 -> new Paladin(castle.getX(), castle.getY(), false, castle);
            case 6 -> new Hero(castle.getX(), castle.getY(), false, castle);
            default -> null;
        };
    }

    private void attackWithUnits(Castle castle) {
        if (castle.getUnits().isEmpty()) {
            System.out.println("Нет юнитов!");
            return;
        }

        for (Unit unit : castle.getUnits()) {
            int newX, newY;
            if (castle == playerCastle) {
                System.out.println("Выберите новую позицию для " + unit.getSymbol());
                System.out.print("Введите X: ");
                newX = scanner.nextInt();
                System.out.print("Введите Y: ");
                newY = scanner.nextInt();
            } else {
                newX = random.nextInt(GameMap.WIDTH);
                newY = random.nextInt(GameMap.HEIGHT);
                System.out.println("Компьютер вводит X: " + newX + ", Y: " + newY);
            }
            unit.attack(newX, newY, map);
        }
    }

    private void moveAndAttackUnits(Castle castle) {
        if (castle.getUnits().isEmpty()) {
            System.out.println("Нет юнитов для перемещения!");
            return;
        }

        for (Unit unit : castle.getUnits()) {
            System.out.println("Выберите новую позицию для " + unit.getSymbol());
            System.out.print("Введите X: ");
            int newX = scanner.nextInt();
            System.out.print("Введите Y: ");
            int newY = scanner.nextInt();
            unit.move(newX, newY, map, castle);

            System.out.print("Введите X для атаки: ");
            int targetX = scanner.nextInt();
            System.out.print("Введите Y для атаки: ");
            int targetY = scanner.nextInt();
            unit.attack(targetX, targetY, map);
        }
    }

    private void buildBuilding(Castle castle) {
        String building;
        if (castle == playerCastle) {
            Menu.showBuildingsMenu();
            System.out.print("Ваш выбор: ");
            int choiceBuilding = scanner.nextInt();
            building = getBuildingName(choiceBuilding);
        } else {
            int aiChoiceBuilding = random.nextInt(7) + 1;
            building = getBuildingName(aiChoiceBuilding);
        }

        if (building != null) {
            if (castle.build(building)) {
                if (building.equals("Таверна") && castle == playerCastle) {
                    playDrunkardGame();
                }
                if (castle == playerCastle) saveGameState();
            }
        }
    }

    private String getBuildingName(int choice) {
        return switch (choice) {
            case 1 -> "Таверна";
            case 2 -> "Конюшня";
            case 3 -> "Сторожевой пост";
            case 4 -> "Башня арбалетчиков";
            case 5 -> "Оружейная";
            case 6 -> "Арена";
            case 7 -> "Собор";
            default -> {
                System.out.println("Неверный ввод, попробуйте снова.");
                yield null;
            }
        };
    }

    private void performCapturedCastleTurn() {
        int aiChoice = random.nextInt(5) + 1;
        System.out.println("Ход компьютера. Компьютер выбирает: " + aiChoice);

        switch (aiChoice) {
            case 1:
                moveUnits(computerCastle);
                break;
            case 2:
                recruitUnit(computerCastle, false);
                break;
            case 3:
                System.out.println("Компьютер пропускает ход.");
                break;
            case 4:
                attackWithUnits(computerCastle);
                break;
            case 5:
                buildBuilding(computerCastle);
                break;
        }
    }

    private boolean showMapMenu() {
        System.out.println("\nМеню карт:");
        System.out.println("1. Выберите карту");
        System.out.println("2. Создать карту");
//        System.out.println("3. Начать игру (стандартная карта)");
        System.out.print("Ваш выбор: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        return switch (choice) {
            case 1 -> selectMapFromList(); // Changed to return selection result
            case 2 -> {
                createNewMap();
                yield false;  // Stay in map menu after creating map
            }
            default -> {
                System.out.println("Неверный ввод, попробуйте снова.");
                yield showMapMenu();
            }
        };
    }

    private boolean selectMapFromList() {
        System.out.println("\nДоступные карты:");

        try {
            File mapsDir = new File("maps");
            if (!mapsDir.exists() || !mapsDir.isDirectory()) {
                System.out.println("Папка с картами не найдена.");
                showMapMenu();
                return false;
            }

            // Get all CSV files from the maps directory
            File[] mapFiles = mapsDir.listFiles((_, name) -> name.toLowerCase().endsWith(".csv"));

            if (mapFiles == null || mapFiles.length == 0) {
                System.out.println("Пока нет созданных карт.");
                showMapMenu();
                return false;
            }

            // Display available maps
            for (int i = 0; i < mapFiles.length; i++) {
                // Remove the .csv extension for display
                String mapName = mapFiles[i].getName();
                mapName = mapName.substring(0, mapName.length() - 4); // Remove .csv
                System.out.println((i + 1) + ". " + mapName);
            }

            System.out.print("Выберите карту (номер) или 0 для возврата: ");
            int mapChoice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (mapChoice == 0) {
                showMapMenu();
                return false;
            }

            if (mapChoice < 1 || mapChoice > mapFiles.length) {
                System.out.println("Неверный выбор.");
                selectMapFromList();
                return false;
            }

            String selectedMapFile = mapFiles[mapChoice - 1].getName();
            String selectedMap = selectedMapFile.substring(0, selectedMapFile.length() - 4);
            System.out.println("Выбрана карта: " + selectedMap + "\n");

            // Load the selected map
            this.map = loadMapFromFile(selectedMap);
            this.mapName = selectedMap;
            return this.map != null;  // Return true only if map loaded successfully

        } catch (Exception e) {
            System.out.println("Ошибка при чтении списка карт: " + e.getMessage());
            return false;
        }
    }

    private GameMap loadMapFromFile(String mapName) {
        File mapFile = new File("maps/" + mapName + ".csv");
        if (!mapFile.exists()) {
            System.out.println("Файл карты не найден!");
            return null;
        }

        try (Scanner fileScanner = new Scanner(mapFile)) {
            GameMap newMap = new GameMap(true); // Create empty map

            int y = 0;
            while (fileScanner.hasNextLine() && y < GameMap.HEIGHT) {
                String line = fileScanner.nextLine();
                String[] tiles = line.split(";");

                for (int x = 0; x < tiles.length && x < GameMap.WIDTH; x++) {
                    try {
                        Tile.Type type = Tile.Type.valueOf(tiles[x]);
                        newMap.setTile(x, y, type);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Неверный тип клетки в файле карты: " + tiles[x]);
                    }
                }
                y++;
            }

            return newMap;
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке карты: " + e.getMessage());
            return null;
        }
    }

    // =========== Редакток карт ===========

    private void createNewMap() {
        System.out.println("\nРедактор карт");

        // Create default 10x10 grass map
        GameMap newMap = new GameMap(true); // We'll modify GameMap constructor

        // Ask for map name
        System.out.print("Введите название карты: ");
        String mapName = scanner.nextLine().trim();

        // Start editor loop
        boolean editing = true;
        while (editing) {
            editing = showMapEditorMenu(newMap, mapName);
        }
    }

    private boolean showMapEditorMenu(GameMap map, String mapName) {
        System.out.println("\nМеню редактора карты:");
        System.out.println("1. Добавить дорогу");
        System.out.println("2. Добавить препятствие");
        System.out.println("3. Добавить замок игрока");
        System.out.println("4. Добавить замок компьютера");
        System.out.println("5. Сохранить и выйти");
        System.out.print("Ваш выбор: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                addTileToMap(map, Tile.Type.ROAD);
                break;
            case 2:
                addTileToMap(map, Tile.Type.OBSTACLE);
                break;
            case 3:
                addTileToMap(map, Tile.Type.CASTLE_PLAYER);
                break;
            case 4:
                addTileToMap(map, Tile.Type.CASTLE_COMP);
                break;
            case 5:
                saveMap(map, mapName);
                return false;
            default:
                System.out.println("Неверный выбор, попробуйте снова.");
        }

        // Show current map state
        map.printMap();
        return true;
    }

    private void addTileToMap(GameMap map, Tile.Type tileType) {
        System.out.print("Введите координату X (0-9): ");
        int x = scanner.nextInt();
        System.out.print("Введите координату Y (0-9): ");
        int y = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (x >= 0 && x < GameMap.WIDTH && y >= 0 && y < GameMap.HEIGHT) {
            map.setTile(x, y, tileType);
            System.out.println("Тип клетки изменен на: " + tileType);
        } else {
            System.out.println("Неверные координаты!");
        }
    }

    // =========== Редакток карт ===========

    // =========== Сохранение карты ===========

    private void saveMap(GameMap map, String mapName) {
        File mapsDir = new File("maps");
        if (!mapsDir.exists()) {
            mapsDir.mkdir();
        }

        try (FileWriter writer = new FileWriter("maps/" + mapName + ".csv")) {
            for (int y = 0; y < GameMap.HEIGHT; y++) {
                for (int x = 0; x < GameMap.WIDTH; x++) {
                    writer.write(map.getTile(x, y).getType().name());
                    if (x < GameMap.WIDTH - 1) writer.write(";");
                }
                writer.write("\n");
            }
            System.out.println("Карта успешно сохранена!");
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении карты: " + e.getMessage());
        }
    }

    public void saveGameState() {
        new File("saves").mkdirs(); // Create saves directory if not exists
        String filename = "saves/game." + username + ".csv";

        try (FileWriter writer = new FileWriter(filename)) {
            // Save castles state
            writer.write("[Castles]\n");
            saveCastleState(writer, playerCastle);
            saveCastleState(writer, computerCastle);

            // Save units state
            writer.write("\n[Units]\n");
            saveUnitsState(writer, playerCastle);
            saveUnitsState(writer, computerCastle);

            // Save map state
            writer.write("\n[Map]\n");
            for (int y = 0; y < GameMap.HEIGHT; y++) {
                for (int x = 0; x < GameMap.WIDTH; x++) {
                    writer.write(map.getTile(x, y).getType().name());
                    if (x < GameMap.WIDTH - 1) writer.write(";");
                }
                writer.write("\n");
            }

            System.out.println("Игра успешно сохранена: " + filename);
        } catch (IOException e) {
            System.out.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    private void saveCastleState(FileWriter writer, Castle castle) throws IOException {
        writer.write(String.format("%s;%d;%d;%d;%d;%s;%d;%d\n",
                castle.getOwner(),
                castle.getGold(),
                castle.getPoints(),
                castle.getX(),
                castle.getY(),
                String.join(",", castle.getBuildings()),
                castle.getSteps(),
                castle == playerCastle ? 1 : 0
        ));
    }

    private void saveUnitsState(FileWriter writer, Castle castle) throws IOException {
        for (Unit unit : castle.getUnits()) {
            writer.write(String.format("%s;%d;%d;%d;%s\n",
                    unit.getClass().getSimpleName(),
                    unit.getX(),
                    unit.getY(),
                    unit.hp,
                    castle.getOwner()
            ));
        }
    }

    // =========== Сохранение карты ===========

    // =========== Загрузка карты ===========

    public void loadGame() {
        String filename = "saves/game." + username + ".csv";
        File saveFile = new File(filename);

        if (!saveFile.exists()) {
            System.out.println("Сохранение не найдено!");
            return;
        }

        try (Scanner fileScanner = new Scanner(saveFile)) {
            Castle loadedPlayerCastle = null;
            Castle loadedComputerCastle = null;
            GameMap loadedMap = null;
            List<Unit> loadedUnits = new ArrayList<>();

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();

                if (line.equals("[Castles]")) {
                    // Parse castles
                    while (fileScanner.hasNextLine()) {
                        line = fileScanner.nextLine().trim();
                        if (line.isEmpty()) break;

                        String[] parts = line.split(";");
                        if (parts.length < 8) continue;

                        String owner = parts[0];
                        int gold = Integer.parseInt(parts[1]);
                        int points = Integer.parseInt(parts[2]);
                        int x = Integer.parseInt(parts[3]);
                        int y = Integer.parseInt(parts[4]);
                        List<String> buildings = new ArrayList<>(Arrays.asList(parts[5].split(",")));
                        int steps = Integer.parseInt(parts[6]);
                        boolean isPlayer = parts[7].equals("1");

                        Castle castle = new Castle(owner, x, y);
                        castle.setGold(gold);
                        castle.setPoints(points);
                        castle.getBuildings().addAll(buildings);
                        castle.setSteps(steps);

                        if (isPlayer) {
                            loadedPlayerCastle = castle;
                        } else {
                            loadedComputerCastle = castle;
                        }
                    }
                } else if (line.equals("[Units]")) {
                    // Parse units
                    while (fileScanner.hasNextLine()) {
                        line = fileScanner.nextLine().trim();
                        if (line.isEmpty()) break;

                        String[] parts = line.split(";");
                        if (parts.length < 5) continue;

                        String className = parts[0];
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        int hp = Integer.parseInt(parts[3]);
                        String unitOwner = parts[4];

                        Castle ownerCastle = unitOwner.equals(loadedPlayerCastle.getOwner()) ?
                                loadedPlayerCastle : loadedComputerCastle;
                        Unit unit = createUnitFromClassName(className, x, y,
                                ownerCastle == loadedPlayerCastle, ownerCastle);

                        if (unit != null) {
                            unit.hp = hp;
                            ownerCastle.getUnits().add(unit);
                            loadedUnits.add(unit);
                        }
                    }
                } else if (line.equals("[Map]")) {
                    // Parse map
                    loadedMap = new GameMap(true);
                    int yCoord = 0;
                    while (fileScanner.hasNextLine() && yCoord < GameMap.HEIGHT) {
                        line = fileScanner.nextLine().trim();
                        if (line.isEmpty()) break;

                        String[] tiles = line.split(";");
                        for (int x = 0; x < tiles.length && x < GameMap.WIDTH; x++) {
                            try {
                                Tile.Type type = Tile.Type.valueOf(tiles[x]);
                                loadedMap.setTile(x, yCoord, type);
                            } catch (IllegalArgumentException e) {
                                System.out.println("Неверный тип клетки при загрузке: " + tiles[x]);
                            }
                        }
                        yCoord++;
                    }
                }
            }

            // Update game state
            if (loadedPlayerCastle != null && loadedComputerCastle != null && loadedMap != null) {
                this.playerCastle = loadedPlayerCastle;
                this.computerCastle = loadedComputerCastle;
                this.map = loadedMap;

                // Add loaded units to the map
                for (Unit unit : loadedUnits) {
                    this.map.addUnit(unit);
                }

                System.out.println("Игра успешно загружена!");
                updateGameState();
            } else {
                System.out.println("Ошибка загрузки: не все данные найдены");
            }
        } catch (IOException e) {
            System.out.println("Ошибка загрузки: " + e.getMessage());
        }
    }

    // =========== Загрузка карты ===========

    private Unit createUnitFromClassName(String className, int x, int y, boolean isPlayer, Castle castle) {
        return switch (className) {
            case "Spearman" -> new Spearman(x, y, isPlayer, castle);
            case "Crossbowman" -> new Crossbowman(x, y, isPlayer, castle);
            case "Swordsman" -> new Swordsman(x, y, isPlayer, castle);
            case "Cavalryman" -> new Cavalryman(x, y, isPlayer, castle);
            case "Paladin" -> new Paladin(x, y, isPlayer, castle);
            case "Hero" -> new Hero(x, y, isPlayer, castle);
            default -> {
                System.out.println("Неизвестный тип юнита: " + className);
                yield null;
            }
        };
    }

    // =========== Пьяница ===========

    private void playDrunkardGame() {
        Scanner gameScanner = new Scanner(System.in);
        System.out.println("\n=== Мини-игра 'Пьяница' ===");

        // Меню ставок
        int bet = 0;
        while (true) {
            System.out.println("Ваше золото: " + playerCastle.getGold());
            System.out.print("Введите ставку (0 - выход): ");
            bet = gameScanner.nextInt();

            if (bet == 0) {
                System.out.println("Игра отменена");
                return;
            }

            if (bet > 0 && bet <= playerCastle.getGold()) break;
            System.out.println("Некорректная ставка!");
        }

        // Подготовка колод
        List<Integer> playerDeck = new ArrayList<>();
        List<Integer> computerDeck = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            playerDeck.add(i);
            computerDeck.add(i);
        }
        Collections.shuffle(playerDeck);
        Collections.shuffle(computerDeck);

        // Игровой процесс
        int playerWins = 0;
        int computerWins = 0;

        for (int round = 0; round < 10; round++) {
            System.out.println("\nРаунд " + (round + 1));
            System.out.println("1. Открыть карту");
            System.out.println("2. Сдаться");
            System.out.print("Выбор: ");

            int choice = gameScanner.nextInt();
            if (choice == 2) break;

            int playerCard = playerDeck.get(round);
            int computerCard = computerDeck.get(round);

            System.out.println("Ваша карта: " + playerCard);
            System.out.println("Карта компьютера: " + computerCard);

            if (playerCard > computerCard) {
                playerWins++;
                System.out.println("Вы выиграли раунд!");
            } else if (computerCard > playerCard) {
                computerWins++;
                System.out.println("Компьютер выиграл раунд!");
            } else {
                System.out.println("Ничья!");
            }
        }

        // Определение результата
        System.out.println("\nРезультат:");
        System.out.println("Ваши победы: " + playerWins);
        System.out.println("Победы компьютера: " + computerWins);

        if (playerWins > computerWins) {
            int winAmount = bet * 2;
            playerCastle.setGold(playerCastle.getGold() + winAmount);
            System.out.println("Победа! Вы получаете " + winAmount + " золота!");
        } else if (computerWins > playerWins) {
            playerCastle.setGold(playerCastle.getGold() - bet);
            System.out.println("Поражение! Вы теряете " + bet + " золота.");
        } else {
            playerCastle.setGold(playerCastle.getGold());
            System.out.println("Ничья! Ставка возвращена.");
        }
    }

    // =========== Пьяница ===========

    // =========== Отель ===========

    private void visitHotel() {
        System.out.println("\nТекущая занятость отеля:");
        System.out.println("Свободных номеров: " + hotel.getAvailableRooms());

        List<Long> times = hotel.getRemainingTimes();
        if (times.isEmpty()) {
            System.out.println("Нет активных посетителей");
        } else {
            times.forEach(t ->
                    System.out.printf("- Осталось времени: %d мс%n", t)
            );
        }

        if (hotel.getAvailableRooms() > 0) {
            handlePlayerHotelChoice();
        } else {
            handleHotelFull();
        }
    }

    private void handlePlayerHotelChoice() {
        System.out.println("\nВыберите услугу:");
        System.out.println("1. Короткий отдых (+2 здоровья, 1 день)");
        System.out.println("2. Длинный отдых (+3 здоровья, 3 дня)");
        System.out.print("Ваш выбор: ");

        int choice = new Scanner(System.in).nextInt();
        if (choice < 1 || choice > 2) return;

        int bonus = choice == 1 ? 2 : 3;
        long duration = choice == 1 ? 100 : 300;

        if (hotel.tryEnter()) {
            new Thread(() -> {
                hotel.addVisitDuration(duration);
                try {
                    Thread.sleep(duration);
                    applyHealthBonus(bonus);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    hotel.leave();
                }
            }).start();
            System.out.println("Начался отдых! Ожидайте " + duration + "мс.");
        }
    }

    private void applyHealthBonus(int bonus) {
        playerCastle.getUnits().forEach(u -> u.addHealth(bonus));
        System.out.println("Применен бонус +" + bonus + " к здоровью!");
    }

    private void handleHotelFull() {
        System.out.print("Все номера заняты. Ждать? (y/n): ");
        String answer = new Scanner(System.in).next();

        if (answer.equalsIgnoreCase("y")) {
            new Thread(this::waitForRoom).start();
        }
    }

    private void waitForRoom() {
        while (hotel.getAvailableRooms() == 0) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Номер освободился!");
        visitHotel();
    }

    // =========== Отель ===========

    // =========== Кафе ===========

    private void visitCafe() {
        System.out.println("\nТекущая загрузка кафе:");
        System.out.println("Свободных мест: " + cafe.getAvailableSlots());

        List<Long> times = cafe.getActiveDurations();
        if (times.isEmpty()) {
            System.out.println("Нет активных заказов");
        } else {
            times.forEach(t ->
                    System.out.printf("- Осталось времени: %d мс%n", t)
            );
        }

        if (cafe.getAvailableSlots() > 0) {
            handlePlayerCafeChoice();
        } else {
            handleCafeFull();
        }
    }

    private void handlePlayerCafeChoice() {
        System.out.println("\nВыберите услугу:");
        System.out.println("1. Просто перекус (+2 перемещение, 15 мин)");
        System.out.println("2. Плотный обед (+3 перемещение, 30 мин)");
        System.out.print("Ваш выбор: ");

        int choice = new Scanner(System.in).nextInt();
        if (choice < 1 || choice > 2) return;

        int bonus = choice == 1 ? 2 : 3;
        long duration = choice == 1 ? 100 * 15 : 100 * 30; // 1.5 и 3 минуты

        if (cafe.tryEnter()) {
            new Thread(() -> {
                cafe.startVisit(duration);
                try {
                    Thread.sleep(duration);
                    applyMovementBonus(bonus);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    cafe.endVisit();
                }
            }).start();
            System.out.println("Заказ принят! Ожидайте " + duration + "мс.");
        }
    }

    private void applyMovementBonus(int bonus) {
        playerCastle.getUnits().forEach(u ->
                u.setMovement(u.getMovement() + bonus)
        );
        System.out.println("Бонус к перемещению +" + bonus + " применен!");
    }

    private void handleCafeFull() {
        System.out.print("Все места заняты. Ждать? (y/n): ");
        String answer = new Scanner(System.in).next();

        if (answer.equalsIgnoreCase("y")) {
            new Thread(() -> {
                while (cafe.getAvailableSlots() == 0) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                visitCafe();
            }).start();
        }
    }

    // =========== Кафе ===========

    // =========== Парикмахерская ===========

    public void visitBarbershop() {
        System.out.println("\nТекущая загрузка парикмахерской:");
        System.out.println("Свободных мест: " + barbershop.getAvailableSeats());

        List<String> services = barbershop.getActiveServices();
        if (services.isEmpty()) {
            System.out.println("Нет активных клиентов");
        } else {
            services.forEach(s -> System.out.println("- " + s));
        }

        if (barbershop.getAvailableSeats() > 0) {
            handlePlayerBarberChoice();
        } else {
            handleBarberFull();
        }
    }

    public void handlePlayerBarberChoice() {
        System.out.println("\nВыберите услугу:");
        System.out.println("1. Простая стрижка (без бонуса, 10 мин)");
        System.out.println("2. Модная стрижка (сокращение захвата замка, 30 мин)");
        System.out.print("Ваш выбор: ");

        int choice = new Scanner(System.in).nextInt();
        if (choice < 1 || choice > 2) return;

        long duration = choice == 1 ? 1000 : 3000;
        boolean isFashion = choice == 2;

        if (barbershop.tryEnter()) {
            new Thread(() -> {
                barbershop.startService(isFashion);
                try {
                    Thread.sleep(duration);
                    if (isFashion) applyCastleCaptureBonus();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    barbershop.endService();
                }
            }).start();
            System.out.println("Стрижка началась! Ожидайте " + duration + "мс.");
        }
    }

    public void applyCastleCaptureBonus() {
        // Пример реализации бонуса
        playerCastle.setCaptureTimeReduction(true);
        System.out.println("Время захвата замка сокращено до 1 хода!");

        // Запланировать снятие бонуса
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                playerCastle.setCaptureTimeReduction(false);
                System.out.println("Бонус сокращения времени захвата закончился");
            }
        }, 3000); // 3000ms = 30 минут игрового времени
    }

    public void handleBarberFull() {
        System.out.print("Все места заняты. Ждать? (y/n): ");
        String answer = new Scanner(System.in).next();

        if (answer.equalsIgnoreCase("y")) {
            new Thread(() -> {
                while (barbershop.getAvailableSeats() == 0) {
                    try { Thread.sleep(500); }
                    catch (InterruptedException e) { break; }
                }
                visitBarbershop();
            }).start();
        }
    }

    // =========== Парикмахерская ===========

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}
