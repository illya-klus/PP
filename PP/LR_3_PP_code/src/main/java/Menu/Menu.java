package Menu;

import battle.BossBattle;
import battle.Duel;
import battle.TeamBattle;
import droids.base.Droid;
import droids.types.*;
import workWithFiles.BattleReader;
import workWithFiles.BattleRecorder;

import java.util.ArrayList;
import java.util.Scanner;

public class Menu {
    protected ArrayList<Droid> arrayOfCreatedDroids = new ArrayList<>();
    private final Scanner sc = new Scanner(System.in);

    private boolean isRecording = false;

    private final ArrayList<ArrayList<Droid>> teamsList = new ArrayList<>();
    private final ArrayList<ArrayList<Droid>> chosenTeams = new ArrayList<>(2);

    public void startMenu() {
        while (true) {
            clearConsole();

            System.out.println("⚙️ ВАС ВІТАЄ ГРА ПРО ДРОЇДІВ ⚙️");
            System.out.println("1. Створити нового дроїда");
            System.out.println("2. Вивести список дроїдів");
            System.out.println("3. Запустити дуель");
            System.out.println("4. Записати бій");
            System.out.println("5. Відтворити бій");
            System.out.println("6. Створити команду");
            System.out.println("7. Показати всі команди");
            System.out.println("8. Вибрати команди для бою");
            System.out.println("9. Почати командний бій");
            System.out.println("10. Почати бій з босом");
            System.out.println("11. Почати бій з босом (тактичний режим)");
            System.out.println("0. Вийти");

            int select = readInt("Ваш вибір: ");

            clearConsole();
            switch (select) {
                case 1 -> createNewDroidMenu();
                case 2 -> printListOfDroids();
                case 3 -> startNewDuelFight();
                case 4 -> startRecording();
                case 5 -> showRecord();
                case 6 -> createTeam();
                case 7 -> printAllTeams();
                case 8 -> chooseTeamsForBattle();
                case 9 -> startTeamBattle();
                case 10 -> startBossBattle(false);
                case 11 -> startBossBattle(true);
                case 0 -> {
                    if (endProgram()) return;
                }
                default -> System.out.println("❌ Невірний вибір, спробуйте ще раз!");
            }
            System.out.print("\nНатисніть ENTER, щоб продовжити...");
            sc.nextLine();
        }
    }

    // ======== Формування команд ========

    private void createTeam() {
        if (arrayOfCreatedDroids.size() < 3) {
            System.out.println("❗ Створіть хоча б 3 дроїди перед формуванням команди.");
            return;
        }

        ArrayList<Droid> newTeam = new ArrayList<>();

        System.out.println("Почніть формування команди з 3 дроїдів:");
        for (int i = 0; i < 3; i++) {
            Droid chosen = chooseDroid();
            if (chosen != null) {
                newTeam.add(chosen);
                chosen.setIsChoosed(true);
            } else {
                i--; // повторити вибір, якщо помилка
            }
        }

        teamsList.add(newTeam);
        System.out.println("✅ Команду успішно створено! Її номер: " + teamsList.size());
    }
    private void chooseTeamsForBattle() {
        if (teamsList.size() < 2) {
            System.out.println("❗ Спочатку створіть хоча б дві команди.");
            return;
        }

        printAllTeams();
        chosenTeams.clear();

        for (int i = 1; i <= 2; i++) {
            int index = readInt("Оберіть команду №" + i + ": ");
            if (index < 1 || index > teamsList.size()) {
                System.out.println("❌ Команди з таким номером не існує!");
                i--;
                continue;
            }
            chosenTeams.add(teamsList.get(index - 1));
        }

        System.out.println("✅ Для бою вибрано команди №" +
                (teamsList.indexOf(chosenTeams.get(0)) + 1) + " і №" +
                (teamsList.indexOf(chosenTeams.get(1)) + 1));
    }

    // ======== Запуск бою ========

    private void startTeamBattle() {
        if (chosenTeams.size() < 2) {
            System.out.println("❗ Спочатку оберіть 2 команди для бою!");
            return;
        }

        ArrayList<Droid> teamOne = chosenTeams.get(0);
        ArrayList<Droid> teamTwo = chosenTeams.get(1);

        TeamBattle battle;
        if (isRecording) {
            battle = new TeamBattle(teamOne, teamTwo, new BattleRecorder());
        } else {
            battle = new TeamBattle(teamOne, teamTwo);
        }

        battle.startTeamBattle();
    }

    // ======== Створення дроїда ========

    private Droid createNewDroidMenu() {
        System.out.println("Оберіть тип дроїда:");
        System.out.println("1. Мечник\n2. Лікар\n3. Снайпер\n4. Маг");

        int select = readInt("Ваш вибір: ");
        if (select < 1 || select > 4) {
            System.out.println("❌ Невірний вибір!");
            return null;
        }

        System.out.print("Введіть ім'я нового дроїда: ");
        String name = sc.nextLine();

        Droid newDroid = switch (select) {
            case 1 -> new DroidSwarder(name);
            case 2 -> new DroidHealer(name);
            case 3 -> new DroidShooter(name);
            case 4 -> new DroidMag(name);
            default -> null;
        };

        arrayOfCreatedDroids.add(newDroid);
        System.out.println("✅ " + newDroid.getDroidType() + " '" + newDroid.getDroidName() + "' створено!");
        return newDroid;
    }
    private Droid chooseDroid() {
        printListOfDroids();
        System.out.print("Введіть ім'я дроїда: ");
        String name = sc.nextLine();

        for (Droid d : arrayOfCreatedDroids) {
            if (d.getDroidName().equalsIgnoreCase(name)) {
                if (d.getIsChoosed()) {
                    System.out.println("❗ Цей дроїд уже у команді.");
                    return null;
                }
                return d;
            }
        }

        System.out.println("❌ Дроїда не знайдено.");
        return null;
    }

    // ======== Дуель ========

    private void startNewDuelFight() {
        if (arrayOfCreatedDroids.size() < 2) {
            System.out.println("❗ Потрібно хоча б 2 дроїди для дуелі.");
            return;
        }

        Droid[] opponents = selectTwoDroids();

        Duel duel = isRecording ?
                new Duel(opponents[0], opponents[1], new BattleRecorder()) :
                new Duel(opponents[0], opponents[1]);

        duel.startAutoBattle();
        System.out.println("🏁 Дуель завершена.");
    }
    private Droid[] selectTwoDroids() {
        Droid[] droids = new Droid[2];
        droids[0] = chooseDroid();
        if (droids[0] == null) return droids;
        droids[0].setIsChoosed(true);

        droids[1] = chooseDroid();
        if (droids[1] == null) return droids;
        droids[1].setIsChoosed(true);

        return droids;
    }

    // ======== Запис і відтворення ========

    private void startRecording() {
        isRecording = true;
        System.out.println("Оберіть режим гри для запису:");
        System.out.println("1. Дуель\n2. Командний бій\n3. Бій з боссом");

        int select = readInt("Ваш вибір: ");
        if (select == 1)
            startNewDuelFight();
        else if (select == 2)
            startTeamBattle();
        else if (select == 3){
            System.out.println("Бажаєте зіграти автоматично(1), чи вручну(2)?:");

            int choose = sc.nextInt();

            if(choose == 1){
                startBossBattle(false);
            }else if(choose == 2){
                startBossBattle(true);
            }else{
                System.out.println("Даного режиму не існує");
            }
        }

        isRecording = false;
    }
    private void showRecord() {
        System.out.print("Введіть шлях до файлу запису: ");
        String path = sc.nextLine();

        BattleReader reader = new BattleReader(path);
        reader.printToConsole();
    }

    // ======== Сервісні методи ========

    private void printListOfDroids() {
        if (arrayOfCreatedDroids.isEmpty()) {
            System.out.println("❗ Список дроїдів порожній.");
            return;
        }
        arrayOfCreatedDroids.forEach(d -> System.out.println("\n" + d));
    }
    private void printAllTeams() {
        if (teamsList.isEmpty()) {
            System.out.println("❗ Немає створених команд.");
            return;
        }

        int i = 1;
        for (ArrayList<Droid> team : teamsList) {
            System.out.println("\nКоманда " + i++ + ":");
            for (Droid droid : team) {
                System.out.println("  - " + droid.getDroidName() + " (" + droid.getDroidType() + ")");
            }
        }
    }
    private boolean endProgram() {
        int choice = readInt("Ви впевнені, що хочете вийти? (1 - так, 0 - ні): ");
        return choice == 1;
    }
    private void clearConsole() {
        for (int i = 0; i < 50; i++) System.out.println();
    }
    private int readInt(String message) {
        System.out.print(message);
        while (!sc.hasNextInt()) {
            System.out.print("❌ Введіть число: ");
            sc.next();
        }
        int val = sc.nextInt();
        sc.nextLine();
        return val;
    }

    // ======== Битва з босом ========

    private void startBossBattle(boolean tactical) {
        if (arrayOfCreatedDroids.size() < 3) {
            System.out.println("❗ Створи принаймні 3 дроїди для команди!");
            return;
        }

        ArrayList<Droid> playerTeam = new ArrayList<>();
        System.out.println("Вибери 3 дроїди для битви з Боссом:");
        for (int i = 0; i < 3; i++) {
            Droid d = chooseDroid();
            if (d != null) {
                playerTeam.add(d);
                d.setIsChoosed(true);
            } else {
                i--;
            }
        }

        BattleRecorder rec = isRecording ? new BattleRecorder() : null;
        BossBattle bossBattle = new BossBattle(playerTeam, isRecording, rec);
        bossBattle.startBattle(tactical);
    }
}