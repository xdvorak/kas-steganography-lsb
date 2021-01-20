package cz.mendelu;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TranspositionCipher {

    private final static Logger LOGGER = Logger.getLogger(TranspositionCipher.class.getName());

    // symetricke sifrovani pomoci jednoduche transpozicni sifry s klicem
    public String encodeText(String input){

        String key = keyInput(input.length());
        // kontrolni ocisteni
        key = key.toLowerCase().strip();
        input = input.toLowerCase().strip();
        LOGGER.info("Input values for encoding are correct.");

        ArrayList<Integer> keyIndexArray = getIndexSequence(key);

        // postupne zprehazeni vsech znaku
        StringBuilder stringBuilder = new StringBuilder(input);
        for (int i = 0; i < input.length(); i = i + key.length()) {
            for (int j = 0; j < key.length(); j++) {
                LOGGER.fine(stringBuilder.charAt(i+j) + " -> " + input.charAt(i + keyIndexArray.get(j)));
                stringBuilder.setCharAt(i + j, input.charAt(i + keyIndexArray.get(j)));
            }
        }
        LOGGER.info("Transposed text: " + stringBuilder);

        // serazeni po "sloupcich"
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < key.length(); i++){
            for (int j = 0; j < stringBuilder.length(); j = j + key.length()) {
                output.append(stringBuilder.charAt(i + j));
            }
        }

        System.out.println("Zašifrovaný text: " + output);
        return output.toString();
    }

    public String decodeText(String encryptedText){

        String key = keyInput(encryptedText.length());
        // kontrolni ocisteni
        key = key.toLowerCase().strip();
        encryptedText = encryptedText.toLowerCase().strip();
        LOGGER.info("Input values for decoding are correct.");

        ArrayList<Integer> keyIndexArray = getIndexSequence(key);
        int numberOfLines = encryptedText.length() / key.length();

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            for (int j = 0; j < encryptedText.length(); j = j + numberOfLines) {
                stringBuilder.append(encryptedText.charAt(i + j));
            }
        }

        // postupne zprehazeni vsech znaku
        StringBuilder output = new StringBuilder(stringBuilder);
        for (int i = 0; i < (stringBuilder.length()); i = i + key.length()) {
            for (int j = 0; j < key.length(); j++) {
                LOGGER.fine(j + ". krok -> " + stringBuilder.charAt(i + keyIndexArray.indexOf(j)));
                output.setCharAt(i + j, stringBuilder.charAt(i + keyIndexArray.indexOf(j)));
            }
        }

        System.out.println("Dešifrovaný text: " + output);
        return output.toString();
    }

    // ziskani klice vhodne delky a s vhodnymi znaky
    private String keyInput(int length){

        boolean correctInput = false;
        Scanner scanner = new Scanner(System.in);
        String key = "";

        while (!correctInput) {
            System.out.println("Zadejte klíč. Klíč musí splňovat následující podmínky:");
            System.out.println("-musí obsahovat pouze neopakujici se znaky abecedy (malá nebo velká písmena)");
            System.out.println("-nesmí obsahovat mezery, čísla, diakritiku, speciální znaky");
            System.out.println("-musí být dělitelem délky textu (" + length + "). Možné délky klíče:");
            System.out.println(Utils.getDivisors(length));

            String enteredText = Utils.normalizeString(scanner.nextLine());

            if (Utils.getDivisors(length).stream().anyMatch(integer -> integer.equals(enteredText.length()))) {
                correctInput = true;
                key = enteredText;
            } else {
                System.out.println("Vložen nevhodný klíč, zkuste to znovu.");
            }
        }
        System.out.println("Vložen vhodný klíč. (" + key + ")");
        return key;
    }

    // ziskani pole indexu vstupniho textu podle abecedniho poradi znaku
    private ArrayList<Integer> getIndexSequence(String key){

        ArrayList<Integer> chars = new ArrayList<>(
                key.chars()
                        .boxed()
                        .collect(Collectors.toList())
        );
        chars.replaceAll(c -> c - 96);

        int targetIndex = 0;
        for (int i = 1; i <= 27; i++) {
            if (chars.contains(i)) {
                chars.set(chars.indexOf(i), targetIndex);
                targetIndex++;
            }
        }

        LOGGER.info("Key index array calculated." + chars);
        return chars;
    }

}
