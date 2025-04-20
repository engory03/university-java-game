package main.ui;

public class Menu {
    static public void showMainMenu() {
        System.out.println("Ход игрока. Выберите действие:");
        System.out.println("1. Двигаться");
        System.out.println("2. Нанять юнита");
        System.out.println("3. Пропустить ход");
        System.out.println("4. Атаковать");
        System.out.println("5. Построить здание");
        System.out.println("6. Двигаться и атаковать");
        System.out.println("7. Сохранить игру");
        System.out.println("8. Загрузить игру");
        System.out.println("9. Отель «У погибшего альпиниста»");
        System.out.println("10. Кафе «Сырники от тети Глаши»");
        System.out.println("11. Парикмахерская «Отрезанное ухо»");
    }

    static public void showUnitsMenu() {
        System.out.println("Ход игрока. Выберите действие:");
        System.out.println("1. Копейщик");
        System.out.println("2. Арбалетчик");
        System.out.println("3. Мечник");
        System.out.println("4. Кавалерист");
        System.out.println("5. Паладин");
        System.out.println("6. Герой");
    }

    static public void showBuildingsMenu() {
        System.out.println("Ход игрока. Выберите действие:");
        System.out.println("1. Таверна");
        System.out.println("2. Конюшня");
        System.out.println("3. Сторожевой пост");
        System.out.println("4. Башня арбалетчиков");
        System.out.println("5. Оружейная");
        System.out.println("6. Арена");
        System.out.println("7. Собор");
    }

    static public void printTurnSeparator() {
        System.out.println();
        System.out.println("================================");
        System.out.println();
    }
}
