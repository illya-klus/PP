package battle;

import java.util.Random;

import Design.Colors;
import droids.base.Droid;
import workWithFiles.BattleRecorder;


public class Duel {
    protected Droid oponentOne;
    protected Droid oponentTwo;
    protected BattleRecorder recorder;
    protected boolean isRecord = false;



    public Duel(Droid one, Droid two, BattleRecorder recorder){
        this.oponentOne = one;
        this.oponentTwo = two;

        this.recorder = recorder;
        this.isRecord = true;
    }

    public Duel(Droid one, Droid two) {
        this.oponentOne = one;
        this.oponentTwo = two;
    }



    public void startAutoBattle() {
        if(this.recorder != null) recorder.startRecording();

        System.out.println("Бій починається!");
        Random rand = new Random();

        int first = rand.nextInt(2);


        Droid currentMover  = oponentOne;
        Droid nextMover = oponentTwo;

        if(first == 1){
            currentMover = oponentTwo;
            nextMover = oponentOne;
        }

        System.out.println("Першим ходить "+ currentMover.getDroidName());

        while(currentMover.getHealth() > 0 && nextMover.getHealth() > 0) {

            System.out.println('\n');
            System.out.println("Кидаємо кубик...");
            try {
                Thread.sleep(1000); // затримка 1000 мс = 1 секунда
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int move = 1 + rand.nextInt(10);

            System.out.println(currentMover.getColor() + currentMover.getDroidName() + "\'у випадає цифра " + move + Colors.RESET);

            switch(move) {
                case 1, 2, 3, 4, 5, 6, 7:
                    System.out.println(currentMover.getColor() + "Дроїд " + currentMover.getDroidName() + " атакує " + nextMover.getDroidName() + Colors.RESET);
                    currentMover.attack(nextMover);
                    break;
                case 8, 9:
                    System.out.println(currentMover.getColor() + "Дроїд " + currentMover.getDroidName() +" вилікувався до " + currentMover.getHealth() + " одиниць здоров'я." + Colors.RESET);
                    currentMover.heal(5);
                    break;
                case 10:
                      currentMover.superAttack(nextMover);
                      break;
            }

            Droid temp =  currentMover;
            currentMover = nextMover;
            nextMover = temp;
        }

        System.out.println(nextMover.getColor() + nextMover.getDroidName() + " перемагає у боротьбі." + Colors.RESET);
        if(this.recorder != null) recorder.stopRecording();

        currentMover.setIsChoosed(false);
        nextMover.setIsChoosed(false);

        currentMover.heal(100);
        nextMover.heal(100);
    }

}
