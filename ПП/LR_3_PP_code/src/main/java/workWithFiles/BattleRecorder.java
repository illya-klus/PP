package workWithFiles;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Random;


public class BattleRecorder {

    private boolean isLogging = false;
    private PrintStream consoleOut;
    private PrintStream fileOut;
    private String fileName;

    public BattleRecorder() {
        this.fileName ="D:\\battle_logs\\battle_" + idGenerate() + "_record.txt";
    }

    public void startRecording(){
        if(isLogging) return;


        consoleOut = System.out;

        try{
            fileOut = new PrintStream(new FileOutputStream(this.fileName), true, "UTF-8");

            System.setOut(new PrintStream(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                   consoleOut.write(b);
                   fileOut.write(b);
                }
            }));
            isLogging = true;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void stopRecording(){
        if(!isLogging) return;

        System.setOut(consoleOut);
        if(fileOut != null)
            fileOut.close();

        isLogging = false;
    }



    private long idGenerate(){
        long from = 10000000;
        Random rand = new Random();
        return from + rand.nextLong(Long.MAX_VALUE - from);
    }
}
