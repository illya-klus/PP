package droids.types;

import Design.Colors;
import droids.base.Droid;

public class DroidMag extends Droid {

    protected double superDamagePoints;
    protected int maxHealth = 80;

    public DroidMag(String droidName) {
        this.droidType = "Дроїд-маг";
        this.droidName = droidName;
        this.health = maxHealth;
        this.damage = 10;
        this.superDamagePoints = 20;
    }


    @Override
    public void superAttack(Droid... oponent) {
        System.out.println(this.color + this.droidName + " використовує супер \"Вогняна куля\"." + Colors.RESET);
        oponent[0].takeDamage(this.superDamagePoints);
    }

}
