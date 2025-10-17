package Main;

import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private ArrayList<Double> CoefArray = new ArrayList<Double>();
    private ArrayList<Double> derivativeCoef = new ArrayList<Double>();
    private Scanner sc = new Scanner(System.in);
    private ArrayList<ArrayList<Double>> arrayOfAquation = new ArrayList<ArrayList<Double>>();
    private ArrayList<Double> Limits = new ArrayList<Double>();
    private  double epsilon = 1.0; // точність



    public static void main(String[] args) {
        (new Main()).start();
    }


    public void start(){
        fillCoefArray();      // введення коефіцієнтів
        findLimits();         // межі коренів за Коші
        System.out.println("Верхня та нижня межа коренів за властивістю Коші: [" + Limits.get(0) + "; "+ Limits.get(1) +  "]" );

        findDerivative();     // похідна
        buildSturmSequence(); // ряд Штурма

        // вивід ряду Штурма
        System.out.println("\n=== Ряд Штурма ===");
        for (int i = 0; i < arrayOfAquation.size(); i++) {
            System.out.print("P" + i + "(x) = ");
            printPolynomial(arrayOfAquation.get(i));
        }

        //таблиця знаків
        double left = Limits.get(0);
        double right = Limits.get(1);

        System.out.println("\n=== Таблиця Штурма ===");
        printSturmTable(left, right);

        //ізоляція коренів методом Штурма
        ArrayList<double[]> roots = new ArrayList<>();
        isolateRoots(Limits.get(0), Limits.get(1), roots);

        // вивід ізольованих проміжків
        System.out.println("\n=== Ізольовані проміжки коренів ===");
        for (double[] interval : roots) {
            System.out.printf("[%.3f , %.3f]\n", interval[0], interval[1]);
        }

        System.out.println("Виконав Клус Ілля Оі-21");
    }

    // Обчислює кількість змін знаків ряду Штурма у точці x
    private int sturmSignChanges(double x) {
        int changes = 0;
        double prev = 0;
        for (ArrayList<Double> poly : arrayOfAquation) {
            double val = evaluate(poly, x);
            if (val == 0) val = 1e-9; // уникнення нуля
            if (prev != 0 && val * prev < 0) changes++;
            prev = val;
        }
        return changes;
    }

    // Обчислює значення многочлена poly у точці x
    private double evaluate(ArrayList<Double> poly, double x) {
        double result = 0;
        for (int i = poly.size() - 1; i >= 0; i--) {
            result = result * x + poly.get(i);
        }
        return result;
    }

    // Рекурсивно ізолюємо корені на інтервалі [a, b]
    private void isolateRoots(double a, double b, ArrayList<double[]> roots) {
        int va = sturmSignChanges(a);
        int vb = sturmSignChanges(b);

        int numRoots = va - vb; // кількість коренів на інтервалі
        if (numRoots == 0) return; // немає коренів
        if (b - a <= epsilon) {
            roots.add(new double[]{a, b});
            return;
        }

        double mid = (a + b) / 2.0;
        isolateRoots(a, mid, roots);
        isolateRoots(mid, b, roots);
    }
    private void findLimits(){
        double biggestAbsCoef = 0;
        for(double coef : CoefArray)
            if(Math.abs(coef) > biggestAbsCoef)
                biggestAbsCoef = Math.abs(coef);

        Limits.add(-(biggestAbsCoef+1));
        Limits.add(biggestAbsCoef+1);
    }

    private void printPolynomial(ArrayList<Double> poly) {
        for (int i = poly.size() - 1; i >= 0; i--) {
            double coef = poly.get(i);
            if (Math.abs(coef) < 1e-9) continue;

            if (i == 0) System.out.printf("%.3f", coef);
            else if (i == 1) System.out.printf("%.3fx + ", coef);
            else System.out.printf("%.3fx^%d + ", coef, i);
        }
        System.out.println();
    }
    private void buildSturmSequence() {
        int index = 0;
        while (true) {
            ArrayList<Double> Pk = arrayOfAquation.get(index);
            ArrayList<Double> Pk1 = arrayOfAquation.get(index + 1);

            ArrayList<Double> remainder = dividePolynomials(Pk, Pk1);
            if (remainder.size() == 0) break; // решта = 0 → кінець

            arrayOfAquation.add(remainder);
            index++;
        }
    }

    private ArrayList<Double> dividePolynomials(ArrayList<Double> A, ArrayList<Double> B) {
        ArrayList<Double> dividend = new ArrayList<>(A);
        int degA = A.size() - 1;
        int degB = B.size() - 1;

        // Якщо степінь діленого менший — повертаємо -A
        if (degA < degB) {
            ArrayList<Double> res = new ArrayList<>();
            for (double c : A) res.add(-c);
            return res;
        }

        double leadB = B.get(degB);
        ArrayList<Double> remainder = new ArrayList<>(dividend);

        while (remainder.size() >= B.size()) {
            int shift = remainder.size() - B.size();
            double factor = remainder.get(remainder.size() - 1) / leadB;

            // Віднімаємо factor * B зсунуте на shift
            for (int i = 0; i < B.size(); i++) {
                int index = i + shift;
                double val = remainder.get(index) - factor * B.get(i);
                remainder.set(index, val);
            }

            // Прибираємо нулі з кінця
            while (remainder.size() > 0 && Math.abs(remainder.get(remainder.size() - 1)) < 1e-9)
                remainder.remove(remainder.size() - 1);
        }

        // Міняємо знак решти (метод Штурма)
        for (int i = 0; i < remainder.size(); i++) {
            remainder.set(i, -remainder.get(i));
        }

        return remainder;
    }
    private void findDerivative () {
        for (int i = 1; i < CoefArray.size(); i++) {
            derivativeCoef.add(CoefArray.get(i) * i);
        }
        arrayOfAquation.add(new ArrayList<>(derivativeCoef));
    }
    private void fillCoefArray(){
        System.out.print("Введіть найвищий степінь x: ");
        int x = sc.nextInt();
        sc.nextLine();

        for(int i = 0; i <= x; i++){
            System.out.print("Введіть коефіцієнт біля x**" + i + ": ");
            CoefArray.add(sc.nextDouble());
            sc.nextLine();
        }
        arrayOfAquation.add(CoefArray);
    }

    private void printSturmTable(double left, double right) {
        System.out.printf("%-10s %-10.3f %-10.3f %-10s\n", "Елемент", left, right, "Зміна знаку");
        String leftSign = "", rightSign = "";

        String marks[] = {"P(x)", "P'(x)", "R1(x)", "R2(x)", "R3(x)"};

        for(int i = 0; i < arrayOfAquation.size(); i++) {
            double resultForLeft = 0.0;
            double resultForRight = 0.0;


                ArrayList<Double> poly = arrayOfAquation.get(i);
                for(int c = 0; c < poly.size(); c++){
                    resultForLeft += poly.get(c) * Math.pow(left, poly.size() - 1 - c);
                    resultForRight += poly.get(c) * Math.pow(right, poly.size() - 1 - c);
                }

                leftSign = resultForLeft > 0 ? "+":"-";
                rightSign = resultForRight > 0 ? "+":"-";


            System.out.printf("%-10s %-10s %-10s %-10d\n", marks[i], leftSign, rightSign,
                    resultForLeft*resultForRight > 0 ? 0 : 1);

            resultForLeft = 0.0;
            resultForRight = 0.0;
        }


    }

}
