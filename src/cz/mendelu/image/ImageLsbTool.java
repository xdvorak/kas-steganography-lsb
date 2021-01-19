package cz.mendelu.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

public class ImageLsbTool {

    // precteni vstupniho textu (tzv "plaintext")
    private String loadText(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Zapište text vkládaný do obrázku:");
        String text = scanner.nextLine();
        System.out.println("Zadáno: " + text);
        return text;
    }

    // precteni vstupniho obrazku (tzv "covertext")
    private BufferedImage loadImage(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Zadejte cestu k obrázku.");
        String path = scanner.nextLine();
        System.out.println("Zadáno: " + path);
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void alterImage(){

        String plainText = loadText();
        BufferedImage inputBufferedImage = loadImage();

        // Ověř velikost obrázku, obrázek musí mít dostatečný počet pixelů odpovídající počtu bitů v textu.
        // Dodatečně je potřeba 1 byte na uložení koncového znaku 00000000;
        if ((plainText.length() * 8) >= (inputBufferedImage.getWidth() * inputBufferedImage.getHeight() - 8)) {
            System.out.println("Pro uložení tohoto textu je potřeba větší obrázek! Změnit text nebo obrázek?");
        } else {
            System.out.println("Probíhá vkládání...");
            BufferedImage alteredBufferedImage = embedTextIntoImage(inputBufferedImage, plainText);
            System.out.println("Text vložen do obrázku.");
            saveOutputImage(alteredBufferedImage);
            System.out.println("Výstupní obrázek uložen.");
        }
    }

    public void encryptImage(){

        BufferedImage inputBufferedImage = loadImage();

        System.out.println("Probíhá získávání textu z obrázku...");
        String extractedText = extractTextFromImage(inputBufferedImage);
        System.out.println("Text z obrázku získán.");
    }

    private void saveOutputImage(BufferedImage outputImage){
        try {
            File outputfile = new File("output" + new Date().getTime() + ".png");
            ImageIO.write(outputImage, "png", outputfile);
        } catch (IOException e) {

        }
    }

    private BufferedImage embedTextIntoImage(BufferedImage inputImage, String text){

        int x = 0, y = 0;
        int charAsciiValue;
        final int EXTRACTOR = 0x00000001;
        final int ZEROATLAST = 0xfffffffe;

        for (int i = 0; i <= text.length(); i++){
            if (i < text.length()){
                charAsciiValue = text.charAt(i);
            } else {
                charAsciiValue = 0;
            }
            for (int j = 0; j < 8; j++){
                int bitValue = charAsciiValue & EXTRACTOR;
                System.out.println("Old:" + Integer.toBinaryString(inputImage.getRGB(x, y)));
                if (bitValue == 1) {
                    inputImage.setRGB(x, y, inputImage.getRGB(x, y) | EXTRACTOR);
                } else {
                    inputImage.setRGB(x, y, inputImage.getRGB(x, y) & ZEROATLAST);
                }
                System.out.println("New:" + Integer.toBinaryString(inputImage.getRGB(x, y)));
                System.out.println("-----------------------");
                x++;

                if (x >= inputImage.getWidth()) {
                    x = 0;
                    y++;
                }
                charAsciiValue = charAsciiValue >> 1;
            }
        }
        return inputImage;
    }

    private String extractTextFromImage(BufferedImage image) {
        int x = 0, y = 0, bitValue;
        final int EXTRACTOR = 0x00000001;
        final int ONEATSTART=0x80;
        char chars;
        int asciiCode = 0;
        String output = "";
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; ; i++) {
            for (int j = 0; j < 8; j++) {
                bitValue = image.getRGB(x, y) & EXTRACTOR;//Extracts last bit from blue color.
                x++;

                if (x >= image.getWidth()) {
                    x = 0;
                    y++;
                }
                asciiCode = asciiCode >> 1;//Left shift to form the character moving the bits by one place and store a new bit.
                if (bitValue == 1) {
                    asciiCode = asciiCode | ONEATSTART;//Replaces bit value with 1
                }
            }
            if (asciiCode == 0) {
                break;
            }
            chars = (char) asciiCode;

            output = output + chars;
            stringBuilder.append(chars);
        }
        System.out.println(output);
        return stringBuilder.toString();
    }

}
