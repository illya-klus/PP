package workWithFiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.List;

public class BattleReader {
    String path;
    List<String> fileAsString;

    public BattleReader(String path){
        this.path = path;
        try{
            fileAsString = Files.readAllLines(Path.of(path),  StandardCharsets.UTF_8);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void printToConsole(){
        System.out.println("Запис бою: ");

        for(String str : fileAsString){
            try {
                Thread.sleep(10); // затримка 1000 мс = 1 секунда
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(str);
        }


        System.out.println("Кінець запису.");
    }
}
