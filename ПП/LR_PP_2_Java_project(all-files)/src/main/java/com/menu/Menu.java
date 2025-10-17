package com.menu;

import com.TrainPackage.Train;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class Menu {
    private Scanner sc = new Scanner(System.in);
    private ArrayList<Train> trains = new ArrayList<>();

    public Menu (){
        fillTarinsArrayAutomaticly();
    }


    public void activateMenu(){
        while(true){
            System.out.println("1. Вивести список усіх потягів.");
            System.out.println("2. Додати запис з клавіатури.");
            System.out.println("3. Видалити запис.");
            System.out.println("4. Сортувати за місцем прибуття.");
            System.out.println("5. Сортувати за часом та місцем.");
            System.out.println("6. Сортувати за місцем призначення та вільними місцями.");

            int choose = sc.nextInt();
            sc.nextLine();

            switch (choose){
                case 1:
                    this.printTrains();
                    break;
                case 2:
                    this.fillArrayFromKeyboard();
                    break;
                case 3:
                    this.removeTrainByNumber();
                    break;
                case 4:
                    this.printArray(this.filterByDestination());
                    break;
                case 5:
                    this.printArray(this.filterByDestinationAndTime());
                    break;
                case 6:
                    this.printArray(this.filterByDestinationAndPlacesCount());
                    break;
                default:
                    return;
            }
            System.out.println("Натисніть ентер щоб йти далі.");
            sc.nextLine();
            this.clearConsole();
        }

    }

    //виведення масиву
    public void printTrains(){
        for (Train train : this.trains)
            System.out.println(train);
    }

    //введення даних автоматично
    public void fillTarinsArrayAutomaticly(){
        this.trains.add( new Train("Київ", 101, "08:00", 250));
        this.trains.add(new Train("Львів", 202, "12:30", 180));
        this.trains.add(new Train("Харків", 303, "15:45", 300));
        this.trains.add(new Train("Львів", 404, "19:20", 220));
        this.trains.add(new Train("Дніпро", 505, "21:00", 200));
        this.trains.add(new Train("Житомир", 334, "10:15", 300));
        this.trains.add(new Train("Львів", 893, "14:10", 0));
        this.trains.add(new Train("Дніпро", 984, "07:00", 200));
        this.trains.add(new Train("Одеса", 601, "06:10", 270));
        this.trains.add(new Train("Ужгород", 602, "09:25", 320));
        this.trains.add(new Train("Кривий Ріг", 603, "13:40", 190));
        this.trains.add(new Train("Чернігів", 604, "17:55", 210));
        this.trains.add(new Train("Полтава", 605, "20:30", 250));
        this.trains.add(new Train("Миколаїв", 606, "05:45", 280));
        this.trains.add(new Train("Тернопіль", 607, "08:15", 240));
        this.trains.add(new Train("Івано-Франківськ", 608, "11:35", 300));
        this.trains.add(new Train("Чернівці", 609, "15:20", 260));
        this.trains.add(new Train("Херсон", 610, "18:50", 230));

        this.trains.add(new Train("Суми", 611, "07:10", 220));
        this.trains.add(new Train("Луцьк", 612, "10:25", 210));
        this.trains.add(new Train("Рівне", 613, "13:05", 250));
        this.trains.add(new Train("Кропивницький", 614, "16:45", 270));
        this.trains.add(new Train("Черкаси", 615, "19:15", 240));
        this.trains.add(new Train("Запоріжжя", 616, "06:30", 280));
        this.trains.add(new Train("Хмельницький", 617, "09:00", 220));
        this.trains.add(new Train("Маріуполь", 618, "12:20", 310));
        this.trains.add(new Train("Біла Церква", 619, "14:40", 200));
        this.trains.add(new Train("Кременчук", 620, "21:10", 260));

        this.trains.add(new Train("Мукачево", 621, "05:20", 300));
        this.trains.add(new Train("Бровари", 622, "08:50", 190));
        this.trains.add(new Train("Кам’янець-Подільський", 623, "11:15", 280));
        this.trains.add(new Train("Олександрія", 624, "15:05", 230));
        this.trains.add(new Train("Бердянськ", 625, "17:25", 260));
        this.trains.add(new Train("Слов’янськ", 626, "20:00", 240));
        this.trains.add(new Train("Конотоп", 627, "07:35", 210));
        this.trains.add(new Train("Чорноморськ", 628, "10:10", 290));
        this.trains.add(new Train("Ізмаїл", 629, "13:55", 300));
        this.trains.add(new Train("Новоград-Волинський", 630, "18:30", 220));

        this.trains.add(new Train("Миргород", 631, "06:40", 210));
        this.trains.add(new Train("Дрогобич", 632, "09:55", 250));
        this.trains.add(new Train("Коростень", 633, "12:25", 230));
        this.trains.add(new Train("Ніжин", 634, "14:50", 220));
        this.trains.add(new Train("Шепетівка", 635, "19:40", 240));
        this.trains.add(new Train("Вінниця", 636, "05:55", 280));
        this.trains.add(new Train("Павлоград", 637, "08:35", 270));
        this.trains.add(new Train("Фастів", 638, "11:05", 200));
        this.trains.add(new Train("Лисичанськ", 639, "16:20", 310));
        this.trains.add(new Train("Тростянець", 640, "20:45", 230));

        this.trains.add(new Train("Чугуїв", 641, "07:20", 220));
        this.trains.add(new Train("Бахмут", 642, "10:50", 240));
        this.trains.add(new Train("Охтирка", 643, "13:30", 210));
        this.trains.add(new Train("Калуш", 644, "15:55", 250));
        this.trains.add(new Train("Стрий", 645, "18:10", 220));
        this.trains.add(new Train("Коломия", 646, "06:05", 270));
        this.trains.add(new Train("Чортків", 647, "09:40", 230));
        this.trains.add(new Train("Ковель", 648, "12:50", 280));
        this.trains.add(new Train("Умань", 649, "17:15", 260));
        this.trains.add(new Train("Жмеринка", 650, "21:30", 240));
        this.trains.add(new Train("Львів", 701, "06:30", 200));
        this.trains.add(new Train("Львів", 702, "09:15", 250));

        this.trains.add(new Train("Київ", 703, "07:45", 300));
        this.trains.add(new Train("Київ", 704, "12:10", 270));
        this.trains.add(new Train("Одеса", 705, "10:00", 280));
        this.trains.add(new Train("Одеса", 706, "18:20", 260));
        this.trains.add(new Train("Харків", 707, "08:40", 310));
        this.trains.add(new Train("Харків", 708, "16:55", 290));
        this.trains.add(new Train("Дніпро", 709, "11:35", 220));
        this.trains.add(new Train("Дніпро", 710, "20:25", 240));
        this.trains.add(new Train("Житомир", 711, "05:50", 210));
        this.trains.add(new Train("Житомир", 712, "14:30", 230));
        this.trains.add(new Train("Полтава", 713, "06:15", 250));
        this.trains.add(new Train("Полтава", 714, "19:45", 270));
        this.trains.add(new Train("Чернігів", 715, "09:05", 240));
        this.trains.add(new Train("Чернігів", 716, "17:30", 260));
        this.trains.add(new Train("Запоріжжя", 717, "08:25", 280));
        this.trains.add(new Train("Запоріжжя", 718, "21:00", 300));
        this.trains.add(new Train("Вінниця", 719, "07:10", 230));
        this.trains.add(new Train("Вінниця", 720, "15:50", 250));
    }

    //Введення даних з клавіатури
    public void fillArrayFromKeyboard(){
        Scanner sc = new Scanner(System.in);

        while(true){
            System.out.println("Хочете додати потяг? (1-так 0-ні): ");
            if(sc.nextLine().equals("1")){
                this.enterTrainInfo();
                System.out.println("Новий потяг усіпшно додано.");
            }else{
                System.out.println("Ввід завершено.");
                break;
            }
        }


    }
    private void enterTrainInfo(){
        Scanner sc = new Scanner(System.in);

        System.out.println("Введіть пункт призначення: ");
        String destination = sc.nextLine();

        sc.nextLine(); // очищення буферу

        System.out.println("Введіть номер потягу: ");
        int trainNumber = sc.nextInt();

        sc.nextLine(); // очищення буферу

        System.out.println("Введіть час відправлення: ");
        String departingTime = sc.nextLine();

        sc.nextLine(); // очищення буферу

        System.out.println("Введіть кількість місць: ");
        int placesCount = sc.nextInt();

        sc.nextLine(); // очищення буферу

        this.trains.add(new Train(destination, trainNumber, departingTime, placesCount));
    }


    //Виконання першого завдання (перебір за місцем прибуття)
    private ArrayList<Train> filterByDestination(){
        Scanner sc = new Scanner(System.in);

        System.out.println("За яким пунктом призначення бажаєте перебрати потяги?");
        String destinationAnswer = sc.nextLine();

        ArrayList<Train> trainsSortedByDestination = this.getTrainsByDestination(destinationAnswer);

        if(trainsSortedByDestination.isEmpty()){
            System.out.println("Не знайдено жодного потягу з таким місцем призначення.");
            return null;
        }

        return trainsSortedByDestination;
    }
    //отримуємо данні за пунктом призначення
    public ArrayList<Train> getTrainsByDestination(String destination){
        ArrayList<Train> result = new ArrayList<>();

        for(Train train : this.trains)
            if(train.getDestination().equals(destination))
                result.add(train);

        return result;
    }

    //Виконання другого завдання (Після заданої години та на певний пункт призначення)
    private ArrayList<Train> filterByDestinationAndTime(){
        ArrayList<Train> destinationSorted = this.filterByDestination();

        Scanner sc = new Scanner(System.in);

        System.out.println("Після якої години бажаєте відсортувати (формат ввдеення \"hh:mm\"): ");
        String departingTimeAnswer = sc.nextLine();

        ArrayList<Train> trainsSortedByDepartingTime = this.getTrainsByDepartingTime(departingTimeAnswer, destinationSorted);

        if(trainsSortedByDepartingTime.isEmpty()){
            System.out.println("Не знайдено жодного потягу з таким місцем призначення.");
            return null;
        }

        return trainsSortedByDepartingTime;
    }
    //Отримуємо дані за часом
    public ArrayList<Train> getTrainsByDepartingTime(String departingTime, ArrayList<Train> array){
        ArrayList<Train> result = new ArrayList<>();
        LocalTime t1 = LocalTime.parse(departingTime);

        for(Train train : array){
            LocalTime t2 = LocalTime.parse(train.getDepartingTime());
            if(t2.isAfter(t1))
                result.add(train);
        }

        return result;
    }


    //Виконання третього завдання
    private ArrayList<Train> filterByDestinationAndPlacesCount(){
        ArrayList<Train> destinationSorted = this.filterByDestination();

        ArrayList<Train> sortedByGeneralPlaces = getTrainsByGeneralPlaces(destinationSorted);

        if(sortedByGeneralPlaces.isEmpty()){
            System.out.println("Не знайдено жодного потягу з таким місцем призначення.");
            return null;
        }
        return  sortedByGeneralPlaces;
    }
    //Отримуємо дані за тим чи є загальні місця
    public ArrayList<Train> getTrainsByGeneralPlaces(ArrayList<Train> array){
        ArrayList<Train> result = new ArrayList<>();

        for(Train train : array)
            if(train.getPlacesCount() > 0)
                result.add(train);

        return result;
    }

    public void printArray(ArrayList<Train> array){
        for(Train train : array){
            System.out.println(train);
        }
    }

    public void clearConsole(){
        for(int i = 0; i < 52; i++)
            System.out.println('\n');
    }

    // Видалення потягів за пунктом призначення
    public void removeTrainsByDestination() {
        System.out.println("Введіть пункт призначення, який хочете видалити: ");
        String destination = sc.nextLine();

        boolean removed = this.trains.removeIf(train -> train.getDestination().equalsIgnoreCase(destination));

        if (removed) {
            System.out.println("Потяги до \"" + destination + "\" успішно видалено.");
        } else {
            System.out.println("Потяги до \"" + destination + "\" не знайдено.");
        }
    }
    // Видалення потяга за номером
    public void removeTrainByNumber() {
        System.out.println("Введіть номер потяга, який хочете видалити: ");
        int trainNumber = sc.nextInt();
        sc.nextLine(); // очищення буферу після nextInt()

        boolean removed = this.trains.removeIf(train -> train.getTrainNumber() == trainNumber);

        if (removed) {
            System.out.println("Потяг №" + trainNumber + " успішно видалено.");
        } else {
            System.out.println("Потяг №" + trainNumber + " не знайдено.");
        }
    }

}
