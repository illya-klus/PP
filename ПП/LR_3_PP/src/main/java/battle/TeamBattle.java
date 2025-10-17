package battle;

import droids.base.Droid;
import Design.Colors;
import workWithFiles.BattleRecorder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class TeamBattle {

    private ArrayList<Droid> teamOne;
    private ArrayList<Droid> teamTwo;
    private ArrayList<Droid> userTeam;
    private ArrayList<Droid> enemyTeam;

    private BattleRecorder recorder;
    public boolean isRecord = false;

    private Scanner sc = new Scanner(System.in);

    public TeamBattle(ArrayList<Droid> teamOne, ArrayList<Droid> teamTwo) {
        this.teamOne = teamOne;
        this.teamTwo = teamTwo;
    }

    public TeamBattle(ArrayList<Droid> teamOne, ArrayList<Droid> teamTwo, BattleRecorder battleRecorder) {
        this.teamOne = teamOne;
        this.teamTwo = teamTwo;

        this.recorder = recorder;
        this.isRecord = true;
    }

    public void startTeamBattle() {
        chooseUserTeam();
        System.out.println("Починається командний бій!");
        Random rand = new Random();
        int turnCounter = 0;

        while(isTeamAlive(userTeam) && isTeamAlive(enemyTeam)) {
            turnCounter++;

            int idealNumber = rand.nextInt(101); // ідеальне число 0-100
            System.out.println("\nІдеальне число для цього ходу: " + idealNumber);

            System.out.println("Введіть своє число (0-100): ");
            int userNum = sc.nextInt();
            sc.nextLine();

            int enemyNum = rand.nextInt(101);
            System.out.println("Ворог вибрав число: " + enemyNum);

            int distUser = Math.abs(idealNumber - userNum);
            int distEnemy = Math.abs(idealNumber - enemyNum);

            if(distUser == distEnemy) {
                System.out.println("Відстань однакова – хід пропускається.");
                continue;
            }

            boolean userTurn = distUser < distEnemy; // ближчий ходить

            Droid attacker = userTurn ? getFirstAlive(userTeam) : getFirstAlive(enemyTeam);
            Droid target = userTurn ? getRandomAlive(enemyTeam, rand) : getRandomAlive(userTeam, rand);

            if(attacker != null && target != null) {
                System.out.println((userTurn ? "Ваш хід: " : "Хід ворога: ") + attacker.getDroidName());

                // кожен третій хід заряджаємо супер-атаку
                if(turnCounter % 3 == 0) {
                    System.out.println(attacker.getDroidName() + " зарядив супер-атаку!");
                    attacker.superAttack(target);
                } else {
                    int move = 1 + rand.nextInt(10);
                    if(move <= 7) {
                        System.out.println(attacker.getColor() + attacker.getDroidName() + " атакує " + target.getDroidName() + Colors.RESET);
                        attacker.attack(target);
                    } else if(move <= 9) {
                        System.out.println(attacker.getColor() + attacker.getDroidName() + " лікується до " + attacker.getHealth() + Colors.RESET);
                        attacker.heal(5);
                    } else {
                        System.out.println(attacker.getColor() + attacker.getDroidName() + " використовує супер-атаку!" + Colors.RESET);
                        attacker.superAttack(target);
                    }
                }
            }
        }

        if(isTeamAlive(userTeam)) {
            System.out.println("🎉 Ваша команда перемогла!");
        } else {
            System.out.println("💀 Ворог переміг!");
        }

        // відновлюємо всі дроїди
        healAll(teamOne);
        healAll(teamTwo);
    }

    // --- Вибір команди ---
    private void chooseUserTeam() {
        showTeams();

        System.out.println("Оберіть команду за яку гратимете: 1 або 2");
        int userTeamIndex = sc.nextInt();
        sc.nextLine();

        if(userTeamIndex == 1) {
            userTeam = teamOne;
            enemyTeam = teamTwo;
        } else {
            userTeam = teamTwo;
            enemyTeam = teamOne;
        }
    }

    // --- Показ команд ---
    private void showTeams() {
        System.out.println("Команда 1:");
        teamOne.forEach(System.out::println);

        System.out.println("\nКоманда 2:");
        teamTwo.forEach(System.out::println);
    }

    // --- Допоміжні методи ---
    private boolean isTeamAlive(List<Droid> team) {
        return team.stream().anyMatch(d -> d.getHealth() > 0);
    }

    private Droid getFirstAlive(List<Droid> team) {
        return team.stream().filter(d -> d.getHealth() > 0).findFirst().orElse(null);
    }

    private Droid getRandomAlive(List<Droid> team, Random rand) {
        List<Droid> alive = team.stream().filter(d -> d.getHealth() > 0).toList();
        if(alive.isEmpty()) return null;
        return alive.get(rand.nextInt(alive.size()));
    }

    private void healAll(List<Droid> team) {
        team.forEach(d -> d.heal(100));
    }
}