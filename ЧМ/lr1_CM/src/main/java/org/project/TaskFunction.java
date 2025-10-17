package org.project;

import java.util.Arrays;


public class TaskFunction {

    private double[] ArrayOfX = new double[3];
    private double[] ArrayOfRelativeErrors = new double[3];
    private double[] ArrayOfAbsoluteErrors = new double[3];
    private double funcAbsError = -1;
    private double funcRelError = -1;


    public TaskFunction(double ...args) {
        if(args.length == 0 || args.length > 3){
            throw new Error("Потрібен масив з 3 чисел!");
        }
        ArrayOfX = args;
    }

        //Обчислення значення функції
    public double CalculateFunction (double ...args){
        return 7 * args[0] * args[0] +
                5* args[1] * args[1] +
                6* args[2] * args[2] +
                6* args[0] * args[2] -
                4 * args[1] -
                5 * Math.exp(args[2] * args[1]);
    }

        //метод для обчислення частинної похідної
    private double PartialDerivative(int i){
        double h = 1e-8;

        double[] ArrayOfXPlus = this.ArrayOfX.clone();
        double[] ArrayOfXMinus = this.ArrayOfX.clone();

        ArrayOfXPlus[i] += h;
        ArrayOfXMinus[i] -= h;

        double fPlus = CalculateFunction(ArrayOfXPlus);
        double fMinus = CalculateFunction(ArrayOfXMinus);

        System.out.println("Успішно обчислено приріст функції відносно " + this.ArrayOfX[i]);
        return Math.abs((fPlus - fMinus) / (2*h));
    }

        //Обчислення відносної помилки іксів для завдання А
    public void CalculateRelativeErrors(){
        int i = 0;
        for(double x : ArrayOfX){
            ArrayOfRelativeErrors[i] = roundTo(CalcRelativeError(x), 10);
            i++;
        }
    }
    public double CalcRelativeError(double x){
        System.out.println("Успішно пораховано відносну похибку числа " + x);
        return ( 1.0 / (2 * FindFirstSignificantFigure(x)) *
                Math.pow( (0.1), CalcNumOfSignificantFigure(x) - 1)
        );
    }
    private int FindFirstSignificantFigure(double x){
        x = Math.abs(x);

        while (x < 1) x *= 10;
        while (x >= 10) x /= 10;

        System.out.println("Успішно знайдено першу значущу цифру числа " + x);
        return (int)x;
    }
    private int CalcNumOfSignificantFigure(double x){
        if(x == 0.0) return 0;

        String str = Double.toString(Math.abs(x));

        str = str.replace(".", "");
        str = str.replaceFirst("^0+", "");

        System.out.println("Успішно пораховано кількість значущих цифр числа " + x);
        return str.length();
    }

        //бчислення абсолютних помилок
    public void CalculateAbsoluteErrors(){
        if(ArrayOfRelativeErrors[0] == 0.0){
            System.out.println("Не достатньо інформації для обрахунку, щоб дізнатись інформацію викличте метод .getInfo()");
            return;
        }

        int i=0;
        for(double relativeError : ArrayOfRelativeErrors){
            ArrayOfAbsoluteErrors[i] = roundTo(relativeError * ArrayOfX[i], 10);
            i++;
        }
        System.out.println("Успішно пораховано абсолютні похибки!");
    }

        //Обчислення відносної та абсолютної похибки функції
    public double CalcFuncAbsoluteError(){
        double AbsError = 0.0;

        int i = 0;

        for(double AbsXError : ArrayOfAbsoluteErrors){
            AbsError += AbsXError * PartialDerivative(i);
            i++;
        }


        System.out.println("Абсолютну помилку функції успішно пораховано!");

        this.funcAbsError = AbsError;
        return AbsError;
    }
    public double CalcFuncRelativeError(){
        if(funcAbsError == -1){
            CalcFuncAbsoluteError();
        }
        this.funcRelError =  this.funcAbsError/ Math.abs(CalculateFunction(ArrayOfX));

        System.out.println("Відносну помилку функції успішно пораховано!");
        return this.funcRelError;
    }


        //гетери та сетери
    public double getXByNumber(int i) {
        return ArrayOfX[i - 1];
    }
    public void SetArrayOfAbsoluteErrors(double[] ArrayOfAbsoluteErrors){
        if(ArrayOfAbsoluteErrors.length > 3){
            System.out.println("ArrayOfAbsoluteErrors.length > 3");
            return;
        }
        this.ArrayOfAbsoluteErrors = ArrayOfAbsoluteErrors;
    }


        //вивід інформації
    public void getInfo(){
        System.out.println("Аргументи: " + Arrays.toString(this.ArrayOfX));
        if(ArrayOfRelativeErrors[0] != 0.0){
            System.out.println("Відносні похибки аргументів: " + Arrays.toString(this.ArrayOfRelativeErrors));
        }
        if(ArrayOfAbsoluteErrors[0] != 0.0){
            System.out.println("Абсолютні похибки аргументів: " + Arrays.toString(this.ArrayOfAbsoluteErrors));
        }
        if(funcRelError != -1){
            System.out.println("Відносна похибка функції: " + funcRelError);
        }
        if(funcAbsError != -1){
            System.out.println("Абсолютна похибка функції: " + funcAbsError);
        }

    }
    public static double roundTo(double value, int decimalPlaces) {
        return Math.round(value * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces);
    }
}
