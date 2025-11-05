package Design;

import java.util.ArrayList;
import java.util.Random;


public class Colors {

    public static int prevColor;
    public static int duration = 50;
    protected ArrayList<String> usedColors = new ArrayList<>();
    public static String RESET = "\u001B[0m";

    public static final String RED_BOLD = "\033[1;31m";
    public static final String GREEN_BOLD = "\033[1;32m";
    public static final String YELLOW_BOLD = "\033[1;33m";
    public static final String BLUE_BOLD = "\033[1;34m";
    public static final String PURPLE_BOLD = "\033[1;35m";
    public static final String CYAN_BOLD = "\033[1;36m";

    public String getRandomColor() {
        Random rand = new Random();

        if (usedColors.size() >= 256) {
            System.out.println("Досягнуто максимальної кількості унікальних кольорів.");
            usedColors.clear();
        }

        while (true) {
            int randColorCode = 16 + rand.nextInt(231); // 0–255
            randColorCode = (randColorCode + this.duration) % 231;

            String newColor = "\u001B[38;5;" + randColorCode + "m"; // текстовий колір 256

            if (!usedColors.contains(newColor)) {
                usedColors.add(newColor);
                return newColor;
            }
        }

    }
}