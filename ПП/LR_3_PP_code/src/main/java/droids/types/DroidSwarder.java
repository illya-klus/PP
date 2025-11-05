package droids.types;

import Design.Colors;
import droids.base.Droid;

import java.util.Random;


public class DroidSwarder extends Droid {

    public double critDamage = 20;
    private double chanse = 12.5;
    protected int maxHealth = 100;

    public DroidSwarder(String name){
        this.droidType = "Дроїд-мечник";
        this.droidName = name;
        this.damage = 9.2;
        this.health = maxHealth;
    }

    @Override
    public void attack(Droid oponent){
        Random rand = new Random();
        if(rand.nextInt(100) <= chanse){
            oponent.takeDamage(this.critDamage);
        }
        oponent.takeDamage(this.damage);
    }

    @Override
    public void superAttack(Droid... droidTeam){
        System.out.println(this.color + this.droidName + " використовує супер \"Спін\"." + Colors.RESET);
        for(Droid d:droidTeam)
            d.takeDamage(this.damage);
    }
}
