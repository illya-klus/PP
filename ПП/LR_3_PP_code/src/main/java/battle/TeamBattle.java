package battle;

import Design.Colors;
import droids.base.Droid;
import workWithFiles.BattleRecorder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TeamBattle {
    private final ArrayList<Droid> teamOne;
    private final ArrayList<Droid> teamTwo;
    private final BattleRecorder recorder;
    private final boolean isRecord;
    private final Random rand = new Random();

    public TeamBattle(ArrayList<Droid> teamOne, ArrayList<Droid> teamTwo) {
        this.teamOne = teamOne;
        this.teamTwo = teamTwo;
        this.recorder = null;
        this.isRecord = false;
    }

    public TeamBattle(ArrayList<Droid> teamOne, ArrayList<Droid> teamTwo, BattleRecorder recorder) {
        this.teamOne = teamOne;
        this.teamTwo = teamTwo;
        this.recorder = recorder;
        this.isRecord = true;
    }

    public void startTeamBattle() {
        if (isRecord && recorder != null)
            recorder.startRecording();

        System.out.println(Colors.RESET + "\n⚔️  ПОЧИНАЄТЬСЯ КОМАНДНИЙ БІЙ! ⚔️");

        printTeams();

        int round = 1;

        while (isTeamAlive(teamOne) && isTeamAlive(teamTwo)) {
            System.out.println(Colors.RESET + "\n===== РАУНД " + round++ + " =====");

            boolean teamOneTurn = rand.nextBoolean();
            ArrayList<Droid> activeTeam = teamOneTurn ? teamOne : teamTwo;
            ArrayList<Droid> targetTeam = teamOneTurn ? teamTwo : teamOne;

            Droid attacker = getRandomAlive(activeTeam);
            Droid target = getRandomAlive(targetTeam);

            if (attacker == null || target == null) continue;

            int moveType = rand.nextInt(100); // 0–99

            // 70% — атака, 20% — лікування, 10% — супер
            if (moveType < 70) {
                System.out.println(attacker.getColor() + "🗡️  " + attacker.getDroidName() +
                        " атакує " + target.getDroidName() + Colors.RESET);
                attacker.attack(target);
            } else if (moveType < 90) {
                System.out.println(attacker.getColor() + "💊 " + attacker.getDroidName() +
                        " лікується." + Colors.RESET);
                attacker.heal(5 + rand.nextInt(10));
            } else {
                System.out.println(attacker.getColor() + "💥 " + attacker.getDroidName() +
                        " використовує супер-атаку!" + Colors.RESET);
                attacker.superAttack(target);
            }

            sleep(800);
        }

        announceWinner();

        // 👇 Новий блок: підсумкова статистика
        printBattleStats();

        if (isRecord && recorder != null)
            recorder.stopRecording();

        healAll(teamOne);
        healAll(teamTwo);
    }

    // ---------------- Допоміжні методи ----------------

    private void printTeams() {
        System.out.println("\nКоманда 1:");
        teamOne.forEach(d ->
                System.out.println("  - " + d.getDroidName() + " (" + d.getDroidType() + ")"));

        System.out.println("\nКоманда 2:");
        teamTwo.forEach(d ->
                System.out.println("  - " + d.getDroidName() + " (" + d.getDroidType() + ")"));
    }

    private void announceWinner() {
        if (isTeamAlive(teamOne))
            System.out.println(Colors.GREEN_BOLD + "\n🎉 Перемогла команда 1!" + Colors.RESET);
        else
            System.out.println(Colors.RED_BOLD + "\n💀 Перемогла команда 2!" + Colors.RESET);
    }

    /** Показує таблицю HP і станів дроїдів після бою */
    private void printBattleStats() {
        System.out.println(Colors.RESET + "\n📊 ПІДСУМКОВА СТАТИСТИКА БОЮ:");
        System.out.println("────────────────────────────────────────");

        printTeamStats("Команда 1", teamOne);
        printTeamStats("Команда 2", teamTwo);
    }

    private void printTeamStats(String title, List<Droid> team) {
        System.out.println("\n" + Colors.CYAN_BOLD + title + Colors.RESET);

        for (Droid d : team) {
            String status;
            if (d.getHealth() <= 0) {
                status = Colors.RED_BOLD + "💀 Мертвий" + Colors.RESET;
            } else if (d.getHealth() < d.getMaxHealth() / 3) {
                status = Colors.YELLOW_BOLD + "⚠️ Поранений" + Colors.RESET;
            } else {
                status = Colors.GREEN_BOLD + "💚 Живий" + Colors.RESET;
            }

            // 👇 кастимо double → int, щоб printf не валився
            System.out.printf("  %-15s (%-10s) | HP: %3d/%3d | %s%n",
                    d.getDroidName(),
                    d.getDroidType(),
                    (int) d.getHealth(),
                    (int) d.getMaxHealth(),
                    status);
        }
    }

    private boolean isTeamAlive(List<Droid> team) {
        return team.stream().anyMatch(d -> d.getHealth() > 0);
    }

    private Droid getRandomAlive(List<Droid> team) {
        List<Droid> alive = team.stream().filter(d -> d.getHealth() > 0).toList();
        if (alive.isEmpty()) return null;
        return alive.get(rand.nextInt(alive.size()));
    }

    private void healAll(List<Droid> team) {
        team.forEach(d -> d.heal(100));
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }
}