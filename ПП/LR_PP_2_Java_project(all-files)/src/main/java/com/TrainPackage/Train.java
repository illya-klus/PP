package com.TrainPackage;

import java.util.Map;
import java.util.HashMap;



public class Train {
    private String destination;
    private int trainNumber;
    private String departingTime;

    private int placesCount;
    private int countOfKupe;
    private int countOfPlatzkart;
    private int countOfLuxe;



    //конструктор
    public Train(String destination, int trainNumber, String departingTime, int placesCount){

        if(trainNumber < 0)
            throw new Error("Введений недопустимий номер потягу.");

        if(placesCount < 0)
            throw new Error("Введена недопустима кількість місць.");

        this.trainNumber = trainNumber;
        this.departingTime = departingTime;
        this.destination = destination;
        this.placesCount = placesCount;

        this.splitTikets();
    }


    //допоміжні методи
    private void splitTikets(){
        int persentKupe = 60;
        int persentPlatzkart = 30;
        int persentLuxe = 10;

        double kupe = this.placesCount * (persentKupe / 100.0);
        double platzkart = this.placesCount * (persentPlatzkart / 100.0);
        double luxe = this.placesCount * (persentLuxe / 100.0);

        this.countOfKupe = (int)Math.round(kupe);
        this.countOfPlatzkart = (int)Math.round(platzkart);
        this.countOfLuxe = this.placesCount - (this.countOfPlatzkart + this.countOfKupe);
    }



    //сетери
    public void setDestination(String destination){
        this.destination = destination;
    }
    public void setTrainNumber(int trainNumber){
        if(trainNumber <= 0)
            throw new Error("Введений недопустимий номер потягу.");
        this.trainNumber = trainNumber;
    }
    public void setDepartingTime(String departingTime){
        this.departingTime = departingTime;
    }
    public void setPlacesCount(int placesCount){
        if(placesCount <= 0)
            throw new Error("Введена недопустима кількість місць.");
        this.placesCount = placesCount;
    }



    //гетери
    public String getDestination(){
        return destination;
    }
    public int getTrainNumber(){
        return trainNumber;
    }
    public String getDepartingTime(){
        return departingTime;
    }
    public int getPlacesCount(){
        return placesCount;
    }
    public Map<String, Integer> getPlacesInfo(){
        Map<String, Integer> placesInfo = new HashMap<>();
        placesInfo.put("Kupe", countOfKupe);
        placesInfo.put("Platzkart", countOfPlatzkart);
        placesInfo.put("Luxe", countOfLuxe);

        return placesInfo;
    }



    //Вивід усієї інформиції
    @Override
    public String toString() {
        String result = "\nЧас прибуття: "
                + this.departingTime
                + "\nНомер потягу: "
                + this.trainNumber
                + "\nПункт призначення: "
                + this.destination
                + "\nКількість місць: "
                + this.placesCount
                + "\nМісця купе: "
                + this.countOfKupe
                + "\nМісця плацкарт: "
                + this.countOfPlatzkart
                + "\nМісця люкс: "
                + this.countOfLuxe;
        return result;
    }

}
