package cz.mendelu;

import java.awt.*;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {

    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args){

        // globalni nastaveni logovani
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.INFO);
        for (Handler h : rootLogger.getHandlers()) {
            h.setLevel(Level.INFO);
        }

        // zakomentovaná část slouží pro vyvolání příkazové řádky v případě spustitelného jar souboru

        Console console = System.console();
        if(console == null && !GraphicsEnvironment.isHeadless()) {
            String filename = Main.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
            try {
                File batch = new File("Launcher.bat");
                if(!batch.exists()){
                    batch.createNewFile();
                    PrintWriter writer = new PrintWriter(batch);
                    writer.println("@echo off");
                    writer.println("java -jar "+filename);
                    writer.println("exit");
                    writer.flush();
                }
                Runtime.getRuntime().exec("cmd /c start \"\" "+batch.getPath());
            } catch(IOException e) {
                e.printStackTrace();
            }
        } else {

            ImageLsbTool imageLsbTool = new ImageLsbTool();
            Scanner scanner = new Scanner(System.in);
            System.out.println("Přejete si:\n1 - Ukrýt text v obrázku\n2 - Získat text ukrytý v obrázku\n(zadejte číslo volby)");
            int option = scanner.nextInt();
            LOGGER.info("Entered: " + option);
            imageLsbTool.imageLsbProcessManager(option);

            System.out.println("Stiskněte klávesu Enter pro ukončení aplikace...");
            new Scanner(System.in).nextLine();

        }
    }

}
