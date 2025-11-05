package workWithFiles;

import Design.Colors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.List;

public class BattleReader {
    private final String path;
    private List<String> fileAsString;

    public BattleReader(String path) {
        this.path = path;
        try {
            fileAsString = Files.readAllLines(Path.of(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println(Colors.RED_BOLD + "❌ Не вдалося зчитати файл запису: " + path + Colors.RESET);
            e.printStackTrace();
        }
    }

    public void printToConsole() {
        if (fileAsString == null || fileAsString.isEmpty()) {
            System.out.println(Colors.YELLOW_BOLD + "⚠️ Файл порожній або не знайдено запис бою." + Colors.RESET);
            return;
        }

        System.out.println(Colors.CYAN_BOLD + "\n🎬 ВІДТВОРЕННЯ ЗАПИСУ БОЮ:" + Colors.RESET);
        System.out.println(Colors.PURPLE_BOLD + "──────────────────────────────────────\n" + Colors.RESET);

        for (String str : fileAsString) {
            try {
                Thread.sleep(15); // невелика затримка для ефекту
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(str);
        }

        System.out.println(Colors.PURPLE_BOLD + "\n──────────────────────────────────────" + Colors.RESET);
        System.out.println(Colors.GREEN_BOLD + "🏁 Кінець запису." + Colors.RESET);
    }
}