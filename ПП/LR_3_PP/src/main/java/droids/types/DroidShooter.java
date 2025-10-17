package droids.types;

import Design.Colors;
import droids.base.Droid;


public class DroidShooter extends Droid {

    public DroidShooter(String name) {
        this.droidType = "Дроїд-стрілець";
        this.droidName = name;
        this.damage = 7.5;
        this.health = 100;
    }

    @Override
    public void superAttack (Droid... oponents) {
        System.out.println(this.color + this.droidName + " використовує супер \"Супер постріл\"." + Colors.RESET);
        for (Droid d : oponents)
            d.takeDamage(this.damage);
    }
}
