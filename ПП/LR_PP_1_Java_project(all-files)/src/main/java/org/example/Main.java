package org.example;

import java.util.ArrayList;
import java.util.Scanner;


class Main {

    public static void main(String[] args) {
        System.out.println("\nПам'ятайте, що індексація починається з 0!");
        Scanner scan = new Scanner(System.in);

        System.out.println("Введіть кількість чисел Люка: ");
        short num = scan.nextShort();

        LukaNumber[] ArrayOfLukaNums = getLukaNumbersToNum(num);

        int i = 0;
        for(LukaNumber n : ArrayOfLukaNums){
            if(i % 10 == 0) System.out.println("\n");
            System.out.print(n.getNum() + " ");
            i++;
        }
        System.out.println("\n");

        LukaNumber[] result = showOneSmallerThanSquere(ArrayOfLukaNums);

        if(result == null) {
            System.out.println("На цьоме проміжку нема таких чисел.");
            return;
        }

        System.out.println("Числа що підходять формулі (x**2)-1 (є на один менше ніж повний квадрат):");
        for(LukaNumber lukaNum: result)
            System.out.println(lukaNum.getNum() + ", ");

    }


    public static LukaNumber[] getLukaNumbersToNum(int num) {
        LukaNumber[] ArrayOfLukaNums = new LukaNumber[num];

        for (short i = 0; i < num; i++) {
            LukaNumber number = new LukaNumber(i);
            ArrayOfLukaNums[i] = number;
        }

        return ArrayOfLukaNums;
    }

    public static LukaNumber[] showOneSmallerThanSquere(LukaNumber[] ArrayOfLukaNums) {
        ArrayList<LukaNumber> result = new ArrayList<>();

        for(LukaNumber num: ArrayOfLukaNums) {
            long number = num.getNum();
            double sqrt = Math.sqrt(number + 1) ;

            if(sqrt == Math.floor(sqrt)) {
                result.add(num);
            }
        }

        if (result.isEmpty()) return null;

        return result.toArray(new LukaNumber[0]);
    }
}
