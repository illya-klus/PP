package droids.types;

import Design.Colors;
import droids.base.Droid;

import java.util.Random;

public class BossDroid extends Droid {
    private double stunChance = 25; // 25% шанс оглушити
    private boolean stunned = false;
    private final Random rand = new Random();

    public BossDroid(String name) {
        this.droidType = "БОС";
        this.droidName = name;
        this.health = 500;
        this.damage = 20;
    }

    @Override
    public void attack(Droid oponent) {
        if (rand.nextInt(100) < stunChance) {
            System.out.println(Colors.PURPLE_BOLD + "💫 " + droidName + " оглушив " + oponent.getDroidName() + "!" + Colors.RESET);
            if (oponent instanceof BossDroid) return;
            oponent.takeDamage(this.damage / 2);
            stunned = true;
        } else {
            oponent.takeDamage(this.damage);
        }
    }

    @Override
    public void superAttack(Droid... oponents) {
        System.out.println(Colors.RED_BOLD + "🔥 " + droidName + " використовує супер атаку — 'Знищення лікування'!" + Colors.RESET);
        for (Droid d : oponents) {
            double dmg = this.damage * 1.5;
            d.takeDamage(dmg);
            System.out.println(Colors.RED_BOLD + "💀 " + d.getDroidName() + " отримує " + dmg + " урону і не може лікуватися наступний хід!" + Colors.RESET);
        }
    }

    public boolean isStunned() {
        return stunned;
    }

    public void resetStun() {
        stunned = false;
    }

    public void splashAttack(Iterable<Droid> opponents) {
        System.out.println(Colors.PURPLE_BOLD + this.droidName + " випускає ударну хвилю по всій команді!" + Colors.RESET);
        for (Droid d : opponents) {
            if (d.getHealth() > 0) {
                double dmg = 8 + Math.random() * 10; // випадковий урон
                d.takeDamage(dmg);
            }
        }
    }
}