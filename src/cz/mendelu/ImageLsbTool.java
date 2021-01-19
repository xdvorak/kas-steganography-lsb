package cz.mendelu;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ImageLsbTool {

    private final static Logger LOGGER = Logger.getLogger(ImageLsbTool.class.getName());

    // organizace procesu
    public void imageLsbProcessManager(int option){

        LOGGER.setLevel(Level.INFO);

        if (option == 1) {
            alterImage();
        } else if (option == 2) {
            extractFromImage();
        }
    }

    // normalizace ceskych stringu (odstraneni diakritiky)
    public String normalizeString(final String string) {
        if (string == null) {
            return null;
        }
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String temp = string.trim();
        temp = Normalizer.normalize(temp, Normalizer.Form.NFD);
        temp = pattern.matcher(temp).replaceAll("");
        temp = temp.replaceAll("[\uFEFF-\uFFFF]", "");

        return temp.toLowerCase();
    }

    // precteni vstupniho textu (tzv "plaintext")
    private String loadText() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Zapište text vkládaný do obrázku:");
        String text = normalizeString(scanner.nextLine());
        LOGGER.info("Entered: " + text);
        return text;
    }

    // precteni vstupniho obrazku (tzv "covertext")
    private BufferedImage loadImage() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Zadejte cestu k obrázku.");
        String path = scanner.nextLine();
        LOGGER.info("Entered: " + path);
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // metoda starajici se o vkladani textu do obrazku a kontrolu vstupu
    private void alterImage() {

        String plainText = loadText();
        BufferedImage inputBufferedImage = loadImage();

        // Over velikost obrazku, obrazek musi mit dostatecny pocet pixelu odpovidajici poctu bitu v textu.
        // Dodatecne je potreba 1 byte na ulozeni koncoveho znaku 00000000.
        if ((plainText.length() * 8) >= (inputBufferedImage.getWidth() * inputBufferedImage.getHeight() - 8)) {
            System.out.println("Pro uložení tohoto textu je potřeba větší obrázek!");
        } else {
            System.out.println("Probíhá vkládání...");
            BufferedImage alteredBufferedImage = insertTextIntoImage(inputBufferedImage, plainText);
            saveOutputImage(alteredBufferedImage);
        }
    }

    // metoda starajici se o ziskani textu z obrazku
    private void extractFromImage() {
        BufferedImage inputBufferedImage = loadImage();

        System.out.println("Probíhá získávání textu z obrázku...");
        String extractedText = extractTextFromImage(inputBufferedImage);
        System.out.println("Text získaný z obrázku:\n" + extractedText);
    }

    // Ulozeni vystupniho obrazku
    private void saveOutputImage(BufferedImage outputImage) {
        try {
            File outputfile = new File("output" + new Date().getTime() + ".png");
            ImageIO.write(outputImage, "png", outputfile);
            LOGGER.info("Altered image saved");
            System.out.println("Vkládání dokončeno.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metoda pro vlozeni textu do obrazku
    private BufferedImage insertTextIntoImage(BufferedImage inputImage, String text) {

        // init souradnice, ascii hodnota, bitova maska
        int x = 0, y = 0;
        int charAsciiValue;
        final int BITMASK = 0x00000001;

        // prochazeni jednotlivych znaku vstupniho textu
        for (int i = 0; i <= text.length(); i++) {

            // po poslednim znaku textu zapsat koncovy nulovy byte
            if (i < text.length()) {
                charAsciiValue = text.charAt(i);
            } else {
                charAsciiValue = Character.MIN_VALUE;
            }

            // prochazeni jednotlivych bitu ascii znaku
            for (int j = 0; j < 8; j++) {
                // posledni bit
                int bitValue = Math.floorMod(charAsciiValue, 2);
                LOGGER.info("Old value: " + Integer.toBinaryString(inputImage.getRGB(x, y)));
                // vlozeni aktualniho bitu do barevne slozky pixelu (do posledniho bitu modre barvy)
                if (bitValue == 1) {
                    inputImage.setRGB(x, y, inputImage.getRGB(x, y) | BITMASK);
                } else {
                    inputImage.setRGB(x, y, inputImage.getRGB(x, y) & ~BITMASK);
                }
                LOGGER.info("New value: " + Integer.toBinaryString(inputImage.getRGB(x, y)) + "\n---------");

                x++;
                // konec radku pixelu
                if (x >= inputImage.getWidth()) {
                    x = 0;
                    y++;
                }
                // bitovy posun
                charAsciiValue = charAsciiValue >> 1;
            }
        }

        LOGGER.info("Text embedded into image.");
        return inputImage;
    }

    // metoda pro ziskani textu z obrazku
    private String extractTextFromImage(BufferedImage image) {

        // init souradnice, bitova hodnota, bitova maska, extrahovany znak, ascii hodnota znaku, vystupni text
        int x = 0, y = 0, bitValue;
        final int BITMASK = 0x00000080;
        char character;
        int charAsciiValue;
        String output = "";

        // prochazeni az do situace precteni 8 nulovych bitu nebo projiti celeho obrazku
        for (int i = 0; ; i++) {
            charAsciiValue = Character.MIN_VALUE;
            System.out.println(Integer.toBinaryString(charAsciiValue));
            for (int j = 0; j < 8; j++) {

                // bitovy posun
                charAsciiValue = charAsciiValue >> 1;
                LOGGER.info("rgb value: " + Integer.toBinaryString(image.getRGB(x, y)));

                // ziskani hodnoty posledniho bitu z RGB hodnoty a jeho ulozeni do znaku podle pozice
                if (Math.floorMod(image.getRGB(x, y), 2) == 1) {
                    charAsciiValue = charAsciiValue | BITMASK;
                    LOGGER.info("char value:" + Integer.toBinaryString(charAsciiValue | BITMASK));
                }

                x++;
                // konec radku pixelu
                if (x >= image.getWidth()) {
                    x = 0;
                    y++;
                }
            }

            if (y >= image.getHeight() || charAsciiValue == Character.MIN_VALUE) {
                break;
            }
            character = (char) charAsciiValue;
            output = output + character;
        }

        LOGGER.info("Text extracted from image.");
        return output;
    }

}
