package org.project;

public class Main {
    public static void main(String[] args) {
        double x1 = 0.1053, x2 = 1.1053, x3 = 2.1053;
        TaskFunction func = new TaskFunction(x1, x2, x3);

        //TaskA(func);
        TaskB(func);

        System.out.println("Клус Ілля Артурович ОІ-21 10 варіант");
    }

    public static void TaskA(TaskFunction func){
        func.CalculateRelativeErrors();
        func.CalculateAbsoluteErrors();

        func.CalcFuncRelativeError();
        func.CalcFuncAbsoluteError();

        System.out.println("");
        func.getInfo();
    }

    public static void TaskB(TaskFunction func){
        func.CalculateRelativeErrors();

        double[] absErrors = {0.01, 0.01, 0.01};
        func.SetArrayOfAbsoluteErrors(absErrors);

        func.CalcFuncRelativeError();
        func.CalcFuncAbsoluteError();

        System.out.println("");
        func.getInfo();
    }
}
