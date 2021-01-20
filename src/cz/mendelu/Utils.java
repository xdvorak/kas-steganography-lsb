package cz.mendelu;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Pattern;


// pomocne metody
public class Utils {

    private final static Logger LOGGER = Logger.getLogger(Utils.class.getName());

    // normalizace ceskych stringu (odstraneni diakritiky)
    public static String normalizeString(final String string) {
        if (string == null) {
            return null;
        }
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String temp = string.trim();
        temp = Normalizer.normalize(temp, Normalizer.Form.NFD);
        temp = pattern.matcher(temp).replaceAll("");
        temp = temp.replaceAll("[\uFEFF-\uFFFF]", "");

        LOGGER.info("Normalized string: " + temp);
        return temp.toLowerCase();
    }

    // seznam delitelu cisla n
    public static ArrayList<Integer> getDivisors(int n){

        ArrayList<Integer> divisors = new ArrayList();
        for (int i = 1; i <= n; i++) {
            if (n % i == 0) {
                divisors.add(i);
            }
        }
        return divisors;
    }
}
