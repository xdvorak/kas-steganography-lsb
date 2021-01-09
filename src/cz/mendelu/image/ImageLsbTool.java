package cz.mendelu.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ImageLsbTool {

    // precteni vstupniho obrazku
    public BufferedImage loadImage(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Zadejte cestu k obrázku.");
        String path = scanner.nextLine();
        System.out.println("Zadáno: " + path);
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void alterImage(BufferedImage bufferedImage){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Zapište text vkládaný do obrázku:");
        String text = scanner.nextLine();
        System.out.println("Zadáno: " + text);

        //Ověř velikost obrázku, text musí být menší než počet pixelů
        if (text.length() > (bufferedImage.getWidth() * bufferedImage.getHeight()))
        {
            System.out.println("A bigger image is required to store this data!");
        } else
        {
            System.out.println("Probíhá vkládání...");
            BufferedImage alteredBufferedImage = embedTextIntoImage(bufferedImage, text);
            //save
        }
    }

    private BufferedImage embedTextIntoImage(BufferedImage inputImage, String text){

        int x = 0;
        int y = 0;
        int asciiValue;//x= x coordinate, y=y coordinate of image starting from top left .
        final int EXTRACTOR = 0x00000001;//BitMask to extract last bit of character.
        final int ZEROATLAST = 0xfffffffe;

        for (int i = 0; i <= text.length(); i++){
            if (i < text.length()){
                asciiValue = text.charAt(i);
            } else {
                asciiValue = 0;// Will be used at the end to mark the end of text.
            }
            for (int j = 0; j < 8; j++){//8 bits forms a character.
                int bitValue = asciiValue & EXTRACTOR;//extracts single bit from the character
                if (bitValue == 1) {
                    inputImage.setRGB(x, y, inputImage.getRGB(x, y) | EXTRACTOR);//Replaces least significant value of the blue color of the pixel with 1.
                } else {
                    inputImage.setRGB(x, y, inputImage.getRGB(x, y) & ZEROATLAST);//Replaces least significant value of the blue color of the pixel with 0.
                }
                x++;

                if (x >= inputImage.getWidth()) {
                    x = 0;
                    y++;
                }
                asciiValue = asciiValue >> 1;
            }
        }
        return inputImage;
    }

}
