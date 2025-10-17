package Menu;

import battle.Duel;
import battle.TeamBattle;
import droids.base.Droid;
import droids.types.DroidHealer;
import droids.types.DroidMag;
import droids.types.DroidShooter;
import droids.types.DroidSwarder;
import workWithFiles.BattleReader;
import workWithFiles.BattleRecorder;


import java.util.ArrayList;
import java.util.Scanner;


public class Menu {
    protected ArrayList<Droid> arrayOfCreatedDroids = new ArrayList<>();
    private Scanner sc = new Scanner(System.in);

    private boolean isRecording = false;

    private ArrayList<ArrayList<Droid>> teamsList = new ArrayList<>();
    private ArrayList<ArrayList<Droid>> choosenTeams = new ArrayList<>(2);
    private int countOfChoosenTeams = 0;



    public void startMenu () {
        while(true){
            this.clearConsole();

            System.out.println("Вас вітає гра про дроїдів! Оберіть команду щоб почати:");
            System.out.println("1. Створити нового дроїда.");
            System.out.println("2. Вивести список дроїдів.");
            System.out.println("3. Запустити дуель.");
            System.out.println("4. Записати бій.");
            System.out.println("5. Відтворити бій.");
            System.out.println("6. Запустити командний бій.");
            System.out.println("7. Вибрати команди.");



            int select = this.sc.nextInt();
            this.sc.nextLine();

            this.clearConsole();
            switch (select) {
                case 1:
                    this.createNewDroidMenu();
                    break;
                case 2:
                    this.printListOfDroids();
                    break;
                case 3:
                    this.startNewDuelFight();
                    break;
                case 4:
                    this.startRecording();
                    break;
                case 5:
                    this.showRecord();
                    break;
                case 6:
                    this.startComandBattle();
                    break;
                case 7:
                    this.chooseTeam();
                    break;
                case 8:
                    this.createComand();
                    break;
                default:
                    int code = this.endProgram();
                    sc.nextLine();
                    if(code == 1) return;
            }
            System.out.print("Натисніть ENTER щоб рухатись далі.");
            sc.nextLine();
        }
    }

    //командний бій
    public void startComandBattle() {
        if (arrayOfCreatedDroids.size() < 6) {
            System.out.println("Потрібно щонайменше 2 команди дроїдів для командного бою.");
            return;
        }

        TeamBattle battle;
        
        ArrayList<Droid> teamOne = choosenTeams.get(0);
        ArrayList<Droid> teamTwo = choosenTeams.get(1);

        if(isRecording) {
            battle = new TeamBattle(teamOne, teamTwo, new BattleRecorder());
        } else {
            battle = new TeamBattle(teamOne, teamTwo);
        }

        // Запускаємо бій
        battle.startTeamBattle();
    }


    //махінації зі списком кманд
    private void discardTeam(){
        this.printAllTeams();

            System.out.println("Виберіть команду з наведених вище : (введіть номер)");

            int index = sc.nextInt();
            sc.nextLine();

            if(countOfChoosenTeams == 0) {
                System.out.println("Не обрано жодної команди.");
                return;
            }

            if(index > this.teamsList.size() -1 ) {
                System.out.println("Елемента з таким індексом не існує.");
                return;
            }

            this.choosenTeams.remove(index-1);
            countOfChoosenTeams++;

    }
    private void chooseTeam(){
        this.printAllTeams();
        while(true){
            System.out.println("Виберіть команду з наведених вище : (введіть номер)");

            int index = sc.nextInt();
            sc.nextLine();

            if(countOfChoosenTeams == 2) {
                System.out.println("Обидві команди вибрано.");
                return;
            }

            if(index > this.teamsList.size()-1) {
                System.out.println("Елемента з таким індексом не існує.");
                return;
            }

            this.choosenTeams.add(this.teamsList.get(index-1));
            countOfChoosenTeams++;
        }


    }

    //створити команду
    private ArrayList<Droid> createComand() {
        ArrayList<Droid> droids = new ArrayList<>();

        if(this.arrayOfCreatedDroids.size() < 3){
            System.out.println("Замало створених дроїдів для командного бою. Створіть ще " + (3 - this.arrayOfCreatedDroids.size()) + " дроїдів.");
            return null;
        }

        System.out.println("Почніть вибір команди з 3 гравців : ");
        for(int i = 0; i < 3; i++)
            droids.add(chooseDroid());

        this.teamsList.add(droids);

        return droids;
    }

    //відтворити бій
    public void showRecord() {
        System.out.println("Введіть точне розташування файлу.");

        String path = this.sc.nextLine();
        BattleReader reader = new BattleReader(path);

        reader.printToConsole();
    }

    //почати запис
    public void startRecording(){
        this.isRecording = true;

        System.out.println("Оберіть режим гри: \n1. Дуель\n2. Командний бій");
        int select = this.sc.nextInt();
        sc.nextLine();

        if(select == 1){
            this.startNewDuelFight();
        }else if(select == 2){
            this.startTeamFight();
        }

        this.isRecording = false;
    }

    //створити новго дроїда
    protected Droid createNewDroidMenu(){
        System.out.println("Дроїда якого типу бажаєте створити?\n1. Мечник\n2. Лікар\n3. Снайпер\n4. Маг\nНатисніть 0 щоб повернутись назад.\n");

        int select = this.sc.nextInt();
        this.sc.nextLine();

        if(select < 0 || select > 4){
            System.out.println("Команди з таким номером не існує. Спробуйте ще раз.");
            return null;
        }

        System.out.print("Введіть ім'я нового дроїда: ");
        String name = this.sc.nextLine();

        Droid newDroid = this.createDroid(select, name);

        if(newDroid != null)
            arrayOfCreatedDroids.add(newDroid);

        System.out.println(newDroid.getDroidType() + " успішно створений.");
        return newDroid;
    }
    private Droid createDroid(int select, String name){
        switch (select){
            case 1:
                return new DroidSwarder(name);
            case 2:
                return new DroidHealer(name);
            case 3:
                return new DroidShooter(name);
            case 4:
                return new DroidMag(name);
            default:
                return null;
        }
    }

    //ввести список дроїдів
    protected void printListOfDroids(){
        for(Droid droid : arrayOfCreatedDroids)
            System.out.println(droid);
    }

    //почати дуель
    protected void startNewDuelFight(){
        if(this.arrayOfCreatedDroids.size() < 2){
            System.out.println("Створіть більше дроїдів щоб запустити цей режим.");
            return;
        }

        Droid[] oponents = selectTwoDroids();

        Duel newFight;
        if(isRecording){
            newFight = new Duel(oponents[0], oponents[1], new BattleRecorder());
        }else{
            newFight = new Duel(oponents[0], oponents[1]);
        }

        newFight.startAutoBattle();

        System.out.println("Дуель завершено.");
        sc.nextLine();
    }
    private Droid[] selectTwoDroids(){
        Droid[] droids = new Droid[2];

        droids[0] = chooseDroid();
        droids[0].setIsChoosed(true);

        droids[1] = chooseDroid();
        droids[1].setIsChoosed(true);

        return droids;
    }
    private Droid chooseDroid(){
        while (true){

            System.out.print("Введіть ім'я дроїда : ");
            String name = sc.nextLine();

            Droid choosed = this.findDroid(name);
            if(choosed == null){
                System.out.println("Спробуйте ще раз.");
                System.out.println("Натисніть ENTER щоб рухатись далі.");

                continue;
            }

            return choosed;
        }
    }
    private Droid findDroid (String name){
        for(Droid d : this.arrayOfCreatedDroids)
            if(d.getDroidName().equals(name)){
                if(d.getIsChoosed() == true){
                    System.out.println("Дроїда уже обрано.");
                    return null;
                }else {
                    return d;
                }
            }

        System.out.println("Дроїда з таким ім'ям не знайдено.");
        return null;
    }


    //командний бій
    public void startTeamFight(){
        return;
    }

    //допоміжні функції
    public void clearConsole() {
        for(int i = 0; i <= 52; i++){
            System.out.println("\n");
        }
    }
    private int endProgram(){
        System.out.println("Ви впевнені, що хочете завершити виконання програми? 1-так ");
        return sc.nextInt();
    }
    public void printAllTeams(){
        int i = 1;

        for(ArrayList<Droid> team :  this.teamsList){
            System.out.println("\n\nКоманда " + i);
            for(Droid droid : team){
                System.out.println('\n');
                System.out.println(droid);
            }
            i++;
        }
    }

}
