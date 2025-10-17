package com.MainPackage;


import com.menu.Menu;

public class Main {


    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }


    public void start() {
        Menu menu = new Menu();
        menu.activateMenu();
    }

}
