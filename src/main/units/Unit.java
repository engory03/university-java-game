package main.units;

import main.buildings.Castle;
import main.map.GameMap;

import java.util.*;

public class Unit {
    private final String type;      // Тип юнита (Копейщик, Арбалетчик и т. д.)
    public int hp;           // Здоровье
    private final int attack;       // Атака
    public int movement;     // Дальность перемещения
    private final int range;        // Дальность атаки (0 – ближний бой)
    private final int cost;         // Стоимость найма
    private int x;
    private int y;
    private final boolean isPlayer;
    private Castle castle;

    public Unit(String type, int hp, int attack, int movement, int range, int cost, int x, int y, boolean isPlayer, Castle castle) {
        this.type = type;
        this.hp = hp;
        this.attack = attack;
        this.movement = movement;
        this.range = range;
        this.cost = cost;
        this.x = x;
        this.y = y;
        this.isPlayer = isPlayer;
        this.castle = castle;
    }

    public char getSymbol() {
        return switch (type) {
            case "Копейщик" -> isPlayer ? 'К' : 'к';
            case "Арбалетчик" -> isPlayer ? 'А' : 'а';
            case "Мечник" -> isPlayer ? 'М' : 'м';
            case "Кавалерист" -> isPlayer ? 'В' : 'в';
            case "Паладин" -> isPlayer ? 'П' : 'п';
            case "Герой" -> isPlayer ? 'Г' : 'г';
            default -> '?';
        };
    }

    public void attack(int targetX, int targetY, GameMap map) {
        Unit target = map.getUnitAt(targetX, targetY);

        if (target == null) {
            System.out.println("На этой клетке нет врага!");
            return;
        }

        int distance = Math.abs(this.x - targetX) + Math.abs(this.y - targetY);

        if (distance > this.range) {
            System.out.println("Цель слишком далека для атаки!");
            return;
        }

        System.out.println(this.type + " атакует " + target.getType() + " нанося " + this.attack + " урона!");
        target.takeDamage(this.attack);

        if (!target.isAlive()) {
            target.castle.removeUnit(target);
            System.out.println(target.getType() + " погиб!");

            // Add points for killing enemy units
            if (this.isPlayer && !target.isPlayer()) {
                int pointsEarned = 0;
                switch (target.getType()) {
                    case "Копейщик" -> pointsEarned = 5;
                    case "Арбалетчик" -> pointsEarned = 7;
                    case "Мечник" -> pointsEarned = 10;
                    case "Кавалерист" -> pointsEarned = 12;
                    case "Паладин" -> pointsEarned = 15;
                    case "Герой" -> pointsEarned = 20;
                }
                this.castle.addPoints(pointsEarned);
                System.out.println("+ " + pointsEarned + " очков за убийство " + target.getType());
            }
        }
    }

    public void move(int newX, int newY, GameMap map, Castle castle) {
        if (!map.isWalkable(newX, newY)) {
            System.out.println("Нельзя ходить в препятствие!");
            return;
        }

        int distance = Math.abs(this.x - newX) + Math.abs(this.y - newY);

        if (distance > movement) {
            System.out.println("Слишком далеко! Максимальная длина хода: " + movement);
            return;
        }

        // Определяем стоимость перемещения в зависимости от зоны
        int stepCost;
        if (isPlayer) {
            stepCost = map.getPlayerMoveStep(newX, newY); // Для игрока
        } else {
            stepCost = map.getCompMoveStep(newX, newY); // Для компьютера
        }

        // Проверяем, достаточно ли шагов для перемещения
        if (castle.getSteps() >= stepCost) {
            // Перемещаем юнита
            this.x = newX;
            this.y = newY;
            castle.spendSteps(stepCost);// Снимаем шаги

            // Тратим золото за перемещение, если это платная дорога
            int goldCost = map.getMoveCost(newX, newY);
            if (goldCost > 0) {
                if (castle.getGold() >= goldCost) {
                    castle.spendGold(goldCost);
                    System.out.println("Платная дорога!");
                } else {
                    // Если денег нет, забираем юнита
                    System.out.println("Недостаточно золота для оплаты дороги!");
//                    castle.removeUnit(this);
                    this.hp = 0;
                    System.out.println("Юнит " + this.getType() + " удален из-за отсутствия золота.");

                    // Если юнитов больше нет, забираем замок
                    if (castle.getUnits().isEmpty() || (castle.getUnits().size() == 1 && castle.getUnits().getFirst().hp == 0)) {
                        System.out.println("У " + castle.getOwner() + " больше нет юнитов. Замок захвачен!");
                    }
                    return;
                }
            }

            System.out.println((isPlayer ? "Игрок" : "Компьютер") + " переместился на (" + newX + ", " + newY + "). Потрачено шагов: " + stepCost + ", золота: " + goldCost);

            // Если это герой, случайно добавляем золото
            if (this.type.equals("Герой")) {
                Random random = new Random();
                int goldFound = random.nextInt(21); // Случайное число от 0 до 20
                castle.addGold(goldFound);
                int pointsEarned = goldFound / 2; // 1 очко за 2 найденных золотых
                castle.addPoints(pointsEarned);
                System.out.println("Герой нашел " + goldFound + " золота!");
                System.out.println("+ " + pointsEarned + " очков за находку!");
            }
        } else {
            System.out.println("Недостаточно шагов для перемещения!");
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public String getType() {
        return type;
    }

    public int getHp() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public int getMovement() {
        return movement;
    }

    public int getRange() {
        return range;
    }

    public int getCost() {
        return cost;
    }

    // Получение урона
    public void takeDamage(int damage) {
        this.hp -= damage;
        if (hp < 0) hp = 0;
    }

    // Проверка, жив ли юнит
    public boolean isAlive() {
        return hp > 0;
    }

    // Вывод информации о юните
    public void printInfo() {
        System.out.println(type + " | HP: " + hp + " | Атака: " + attack + " | Перемещение: " + movement + " | Дальность атаки: " + range);
    }

    public void addHealth(int bonus) {
        this.hp += bonus;
    }

    public void setMovement(int movement) {
        this.movement = movement;
    }
}
