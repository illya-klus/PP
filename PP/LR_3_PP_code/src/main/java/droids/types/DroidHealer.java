package droids.types;

import Design.Colors;
import droids.base.Droid;


public class DroidHealer extends Droid {
    protected double healPoints;
    protected double superDamagePoints;


    public DroidHealer(String droidName) {
        this.droidType = "Дроїд-лікар";
        this.droidName = droidName;
        this.health = 100;
        this.damage = 1;
        this.healPoints = 1;
        this.superDamagePoints = 10;
    }


    void healSomeone(Droid droid) {
        System.out.println("Лікую " + droid.getDroidName());
        droid.heal(this.healPoints);
    }

    @Override
    public void superAttack(Droid... teammates){
        System.out.println(this.color + this.droidName + " використовує супер \"Повний відхіл\"." + Colors.RESET);

        if(teammates == null ){ this.healSomeone(this); }
        for (Droid d:teammates){
            d.heal(this.healPoints);
        }

    }

}
