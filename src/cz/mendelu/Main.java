package cz.mendelu;

import java.awt.*;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {


    public static void main(String[] args){

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

            System.out.println("Press any key to exit application...");
            System.out.println(new Scanner(System.in).nextLine());
        }

    }
}
