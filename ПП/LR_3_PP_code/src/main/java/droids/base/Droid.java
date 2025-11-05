package droids.base;


import Design.Colors;

public class Droid {
    protected String droidName;

    protected boolean isChoosed = false;

    //фіксовані спочатку величини
    protected String droidType;
    protected double health;
    protected double damage;
    protected String color;
    protected final int maxHealth = 100;



    public Droid () {
        this.health = maxHealth;
        this.damage = 5;

        this.droidType = "none";
        this.droidName = "none";

        Colors colors = new Colors();
        this.color = colors.getRandomColor();
    }


    public void superAttack (Droid... oponents){}

    public void heal(double points){
        if( (this.health += points) > 100 )
            this.health = 100;
    }
    public void attack(Droid oponent){
        oponent.takeDamage(this.damage);
    }

    public void takeDamage(double damage){
        if((this.health -= damage) <= 0){
            System.out.println(this.color + "Дроїд " + this.droidName + " отримав забагато дамагу та загинув." + Colors.RESET);
        }else{
            System.out.println(this.color + "Дроїд " + this.droidName + " отримав " + damage + " урону." + Colors.RESET);
        }
    }


    public int getMaxHealth(){return this.maxHealth;}
    public boolean getIsChoosed() {
        return this.isChoosed;
    }
    public String getDroidType(){
        return this.droidType;
    }
    public String getDroidName(){
        return this.droidName;
    }
    public String getColor(){ return this.color; }
    public double getHealth(){
        return this.health;
    }
    public double getDamage(){
        return this.damage;
    }

    public void setDroidName(String name){
        this.droidName = name;
    }
    public void setIsChoosed(boolean isChoosed){
        this.isChoosed = isChoosed;
    }


    @Override
    public String toString(){
        return String.format("Ім'я дроїда: %s,\nТип: %s\nЗдоров'я: %.2f\nУрон: %.2f\n",
                this.droidName, this.droidType, this.health, this.damage);
    }
}