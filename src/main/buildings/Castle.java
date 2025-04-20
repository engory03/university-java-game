package main.buildings;

import main.units.Unit;

import java.util.*;

public class Castle {
    private final String owner; // Владелец замка (Игрок или Компьютер)
    private final int x, y; // Координаты замка
    private int gold; // Золото в замке
    private int points; // Очки игрока
    private int steps; // Общее количество шагов за каждый ход
    private final List<Unit> units; // Армия в замке
    private final List<String> buildings; // Список построек
    private boolean captureTimeReduction; // Сокращение захвата замка

    // Стоимость зданий
    private static final Map<String, Integer> BUILDING_COSTS = new HashMap<>();

    // Связь между зданиями и юнитами
    private static final Map<String, String> BUILDING_TO_UNIT = new HashMap<>();

    static {
        BUILDING_COSTS.put("Таверна", 5);
        BUILDING_COSTS.put("Конюшня", 10);
        BUILDING_COSTS.put("Сторожевой пост", 15);
        BUILDING_COSTS.put("Башня арбалетчиков", 20);
        BUILDING_COSTS.put("Оружейная", 25);
        BUILDING_COSTS.put("Арена", 30);
        BUILDING_COSTS.put("Собор", 35);

        // Связь зданий и юнитов
        BUILDING_TO_UNIT.put("Таверна", "Герой");
        BUILDING_TO_UNIT.put("Сторожевой пост", "Копейщик");
        BUILDING_TO_UNIT.put("Башня арбалетчиков", "Арбалетчик");
        BUILDING_TO_UNIT.put("Оружейная", "Мечник");
        BUILDING_TO_UNIT.put("Арена", "Кавалерист");
        BUILDING_TO_UNIT.put("Собор", "Паладин");
    }

    public Castle(String owner, int x, int y) {
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.gold = 100; // Начальное золото
        this.steps = 10; // Начальное количество шагов
        this.points = 0;
        this.units = new ArrayList<>();
        this.buildings = new ArrayList<>();
        this.buildings.add("Сторожевой пост"); // Минимально необходимая постройка
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getOwner() {
        return owner;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getGold() {
        return gold;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public List<String> getBuildings() {
        return buildings;
    }

    public int getSteps() {
        return steps;
    }

    public int getPoints() {
        return points;
    }

    public void spendSteps(int steps) {
        this.steps -= steps;
    }

    public void resetSteps() {
        this.steps = 10;
    }

    // Добавление золота
    public void addGold(int amount) {
        gold += amount;
    }

    // Трата золота
    public void spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
        } else {
            System.out.println("Недостаточно золота!");
        }
    }

    // Метод для удаления юнита из списка
    public void removeUnit(Unit unit) {
        units.remove(unit);
    }

    // Покупка юнита
    public boolean buyUnit(Unit unit) {
        // Проверяем, есть ли нужное здание для найма юнита
        if (!checkBuilding(unit.getType())) {
            String requiredBuilding = getBuildingForUnit(unit.getType());
            System.out.println("Для найма " + unit.getType() + " необходимо построить " + requiredBuilding);
            return false;
        }

        if (gold >= unit.getCost()) {
            units.add(unit);
            gold -= unit.getCost();
            System.out.println(owner + " нанял " + unit.getType());
            return true;
        } else {
            System.out.println("Недостаточно золота!");
            return false;
        }
    }

    // Постройка здания
    public boolean build(String building) {
        // Удаляем лишние пробелы и приводим к нижнему регистру
        String normalizedBuilding = building.trim().toLowerCase();

        // Проверяем, есть ли здание в списке
        for (Map.Entry<String, Integer> entry : BUILDING_COSTS.entrySet()) {
            if (entry.getKey().toLowerCase().equals(normalizedBuilding)) {
                if (buildings.contains(entry.getKey())) {
                    System.out.println(entry.getKey() + " уже построено!");
                    return false;
                }

                int cost = entry.getValue();
                if (gold >= cost) {
                    buildings.add(entry.getKey());
                    gold -= cost;
                    int pointsEarned = cost / 5; // 1 очко за 5 золотых
                    addPoints(pointsEarned);
                    System.out.println(owner + " построил " + entry.getKey() + " за " + cost + " золота.");
                    System.out.println("+ " + pointsEarned + " очков за постройку!");
                    return true;
                } else {
                    System.out.println("Недостаточно золота для постройки " + entry.getKey() + "!");
                    return false;
                }
            }
        }

        System.out.println("Неизвестное здание: " + building);
        return false;
    }

    // Проверка наличия здания для найма юнита
    public boolean checkBuilding(String unitType) {
        // Находим здание, которое отвечает за найм юнита
        for (Map.Entry<String, String> entry : BUILDING_TO_UNIT.entrySet()) {
            if (entry.getValue().equals(unitType)) {
                // Проверяем, есть ли это здание у игрока
                return buildings.contains(entry.getKey());
            }
        }
        return false; // Если здание не найдено
    }

    // Получение здания по типу юнита
    public String getBuildingForUnit(String unitType) {
        for (Map.Entry<String, String> entry : BUILDING_TO_UNIT.entrySet()) {
            if (entry.getValue().equals(unitType)) {
                return entry.getKey();
            }
        }
        return "Неизвестное здание";
    }

    // Вывод информации о замке
    public void printCastleInfo() {
        System.out.println();
        System.out.println("🔹 Замок " + owner + " | Золото: " + gold + " | Очки: " + points);
        System.out.println("📜 Постройки: " + buildings);
        System.out.println("🛡 Юниты: " + (units.isEmpty() ? "Пусто" : ""));
        for (Unit unit : units) {
            System.out.println(" - " + unit.getType() + " (HP: " + unit.getHp() + ", Атака: " + unit.getAttack() + ")");
        }
        System.out.println();
    }

    public void addPoints(int amount) {
        points += amount;
    }

    public boolean isCaptureTimeReduced() {
        return captureTimeReduction;
    }

    public void setCaptureTimeReduction(boolean value) {
        captureTimeReduction = value;
    }

    public int getCaptureTime() {
        return isCaptureTimeReduced() ? 1 : 2;
    }
}
