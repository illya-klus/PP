package battle;

import Design.Colors;
import droids.base.Droid;
import droids.types.BossDroid;
import workWithFiles.BattleRecorder;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class BossBattle {
    private final ArrayList<Droid> team;
    private final BossDroid boss;
    private final BattleRecorder recorder;
    private final boolean isRecord;
    private final Random rand = new Random();
    private final Scanner sc = new Scanner(System.in);

    public BossBattle(ArrayList<Droid> team, boolean isRecord, BattleRecorder recorder) {
        this.team = team;
        this.boss = new BossDroid("МЕГА-ДРОЇД ОМЕГА");
        this.isRecord = isRecord;
        this.recorder = recorder;
    }

    public void startBattle(boolean tactical) {
        if (isRecord && recorder != null)
            recorder.startRecording();

        System.out.println(Colors.RED_BOLD + "\n⚠️  БІЙ З БОСОМ ПОЧИНАЄТЬСЯ! ⚠️" + Colors.RESET);
        printTeams();

        int round = 1;
        while (isTeamAlive(team) && boss.getHealth() > 0) {
            System.out.println(Colors.RESET + "\n===== РАУНД " + round++ + " =====");

            // Дії команди
            for (Droid d : team) {
                if (d.getHealth() <= 0) continue;
                if (tactical) {
                    doTacticalMove(d);
                } else {
                    doRandomMove(d);
                }
                sleep(700);
            }

            // Дія боса
            if (boss.getHealth() > 0) {
                int action = rand.nextInt(100);

                if (action < 15) {
                    System.out.println(Colors.PURPLE_BOLD + "\n💥 БОС використовує вибухову сплеш-атаку!" + Colors.RESET);
                    boss.splashAttack(team);
                } else if (action < 30) {
                    System.out.println(Colors.RED_BOLD + "\n💀 БОС готує СУПЕР-АТАКУ!" + Colors.RESET);
                    boss.superAttack(team.toArray(new Droid[0]));
                } else {
                    Droid target = getRandomAlive(team);
                    if (target != null) {
                        System.out.println(Colors.YELLOW_BOLD + "\n🗡️  БОС атакує " + target.getDroidName() + "!" + Colors.RESET);
                        boss.attack(target);
                    }
                }
            }

            sleep(1200);
        }

        // 🏁 Кінець бою
        if (boss.getHealth() <= 0)
            System.out.println(Colors.GREEN_BOLD + "\n🎉 Ви перемогли БОСА!" + Colors.RESET);
        else
            System.out.println(Colors.RED_BOLD + "\n💀 БОС знищив вашу команду!" + Colors.RESET);

        // 📊 ПІДСУМКОВА СТАТИСТИКА
        printBattleStats();

        if (isRecord && recorder != null)
            recorder.stopRecording();

        healAll(team);
    }

    // ---------------------- Допоміжні методи ----------------------

    private void doTacticalMove(Droid d) {
        System.out.println("\n" + d.getColor() + d.getDroidName() + " (" + d.getDroidType() + "):");
        System.out.println("1. Атакувати босса");
        System.out.println("2. Вилікуватися (+10 HP)");
        System.out.println("3. Супер-атака");

        int choice = readInt("Ваш вибір: ");
        switch (choice) {
            case 1 -> d.attack(boss);
            case 2 -> {
                d.heal(10);
                System.out.println(Colors.GREEN_BOLD + d.getDroidName() + " лікується до " + (int) d.getHealth() + Colors.RESET);
            }
            case 3 -> d.superAttack(boss);
            default -> System.out.println("❌ Невірний вибір. Пропуск ходу.");
        }
    }

    private void doRandomMove(Droid d) {
        int move = rand.nextInt(100);
        if (move < 70)
            d.attack(boss);
        else if (move < 90)
            d.heal(10);
        else
            d.superAttack(boss);
    }

    private boolean isTeamAlive(ArrayList<Droid> team) {
        return team.stream().anyMatch(d -> d.getHealth() > 0);
    }

    private Droid getRandomAlive(ArrayList<Droid> team) {
        ArrayList<Droid> alive = new ArrayList<>();
        for (Droid d : team) if (d.getHealth() > 0) alive.add(d);
        return alive.isEmpty() ? null : alive.get(rand.nextInt(alive.size()));
    }

    private void healAll(ArrayList<Droid> team) {
        for (Droid d : team) d.heal(100);
    }

    private void printTeams() {
        System.out.println("\nКоманда гравця:");
        for (Droid d : team)
            System.out.println("  - " + d.getDroidName() + " (" + d.getDroidType() + ")");
        System.out.println("\nПроти: " + Colors.RED_BOLD + boss.getDroidName() + Colors.RESET);
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
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

    private void printBattleStats() {
        System.out.println(Colors.CYAN_BOLD + "\n📊 ПІДСУМКОВА СТАТИСТИКА БОЮ:" + Colors.RESET);
        System.out.println("────────────────────────────────────────");

        // Статистика команди
        System.out.println(Colors.BLUE_BOLD + "\nКоманда гравця:" + Colors.RESET);
        for (Droid d : team) {
            String status;
            if (d.getHealth() <= 0)
                status = Colors.RED_BOLD + "💀 Мертвий" + Colors.RESET;
            else if (d.getHealth() < d.getMaxHealth() / 3)
                status = Colors.YELLOW_BOLD + "⚠️ Поранений" + Colors.RESET;
            else
                status = Colors.GREEN_BOLD + "💚 Живий" + Colors.RESET;

            System.out.printf("  %-15s (%-10s) | HP: %3d/%3d | %s%n",
                    d.getDroidName(),
                    d.getDroidType(),
                    (int) d.getHealth(),
                    d.getMaxHealth(),
                    status);
        }

        // Статистика босса
        System.out.println(Colors.RED_BOLD + "\nБос:" + Colors.RESET);
        String bossStatus;
        if (boss.getHealth() <= 0)
            bossStatus = Colors.GREEN_BOLD + "☠️ Переможений" + Colors.RESET;
        else if (boss.getHealth() < boss.getMaxHealth() / 3)
            bossStatus = Colors.YELLOW_BOLD + "⚠️ Послаблений" + Colors.RESET;
        else
            bossStatus = Colors.RED_BOLD + "💀 Активний" + Colors.RESET;

        System.out.printf("  %-15s | HP: %4d/%4d | %s%n",
                boss.getDroidName(),
                (int) boss.getHealth(),
                boss.getMaxHealth(),
                bossStatus);
    }
}