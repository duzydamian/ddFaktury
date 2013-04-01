/**
 * Weryfikator numerów NIP, PESEL, REGON, IBAN
 *
 * @author Klaudiusz Kulik <kulikk(at)monstrum.org>
 */

package ddfaktury.ui;

public abstract class Validator {
    
    /**
     * Weryfikacja numeru REGON
     * @param regon
     * @return true jeśli prawidłowy
     */
    public static boolean isValidRegon(String regon) {
        regon = trimInput(regon);
        int rsize = regon.length();
        if (!((rsize == 9) || (rsize == 14))) {
            return false;
        }
        int[] weights = {8,9,2,3,4,5,6,7};
        int j = 0, sum = 0, control = 0;
        int csum = new Integer(regon.substring(rsize - 1)).intValue();
        for (int i = 0; i < rsize - 1; i++) {
            char c = regon.charAt(i);
            j = new Integer(String.valueOf(c)).intValue();
            sum += j * weights[i];
        }
        control = sum % 11;
        if (control == 10) {
            control = 0;
        }
        return (control == csum);
    }
    
    /**
     * Weryfikacja numeru REGON
     * @param regon
     * @return true jeśli prawidłowy
     */
    public static boolean isValidRegon(long regon) {
        return isValidRegon(new Long(regon).toString());
    }
    
    /**
     * Weryfikacja numeru NIP
     * @param nip
     * @return true jeśli prawidłowy
     */
    public static boolean isValidNip(String nip) {
        nip = trimInput(nip);
        int nsize = nip.length();
        if (nsize != 10) {
            return false;
        }
        int[] weights = {6,5,7,2,3,4,5,6,7};
        int j = 0, sum = 0, control = 0;
        int csum = new Integer(nip.substring(nsize - 1)).intValue();
        if (csum == 0) {
            csum = 10;
        }
        for (int i = 0; i < nsize - 1; i++) {
            char c = nip.charAt(i);
            j = new Integer(String.valueOf(c)).intValue();
            sum += j * weights[i];
        }
        control = sum % 11;
        return (control == csum);
    }
    
    /**
     * Weryfikacje numeru NIP
     * @param nip
     * @return true jeśli prawidłowy
     */
    public static boolean isValidNip(long nip) {
        return isValidNip(new Long(nip).toString());
    }
    
    /**
     * Weryfikacja numeru PESEL
     * @param pesel
     * @return true jeśli prawidłowy
     */
    public static boolean isValidPesel(String pesel) {
        pesel = trimInput(pesel);
        int psize = pesel.length();
        if (psize != 11) {
            return false;
        }
        int[] weights = {1,3,7,9,1,3,7,9,1,3};
        int j = 0, sum = 0, control = 0;
        int csum = new Integer(pesel.substring(psize - 1)).intValue();
        for (int i = 0; i < psize - 1; i++) {
            char c = pesel.charAt(i);
            j = new Integer(String.valueOf(c)).intValue();
            sum += j * weights[i];
        }
        control = 10 - (sum % 10);
        if (control == 10) {
            control = 0;
        }
        return (control == csum);
    }
    
    /**
     * Weryfikacja numeru PESEL
     * @param pesel
     * @return true jeśli prawidłowy
     */
    public static boolean isValidPesel(long pesel) {
        return isValidPesel(new Long(pesel).toString());
    }
    
    /**
     * Weryfikacje numeru IBAN
     * @param iban
     * @return true jeśli prawidłowy
     */
    public static boolean isValidIban(String iban) {
        // Algorytm (c)  R.J.Żyłła 2000-2004 */
        // 0. Zamiana na wielkie litery i usunięcie śmieci
        // i spacji
        iban = iban.toUpperCase().replaceAll("[\\p{Punct}\\p{Space}]*","");
        if (!iban.matches("^[A-Z]{2}[0-9]{12,}"))
            return false;
//		if (iban.length() < 16)
//			return false;
        // 1. Pierwsze 4 znaki na koniec
        iban = iban.substring(4, iban.length()) + iban.substring(0 ,4);
        // 2. Litery na cyfry
        for (int i = 0; i < iban.length(); i++) {
            char c = iban.charAt(i);
            if (Character.isUpperCase(c)) {
                int code = Character.getNumericValue(c);
                iban = iban.substring(0, i) + code
                        + iban.substring(i + 1, iban.length());
            }
        }
        // 3. Modulo 97
        int mod = 0;
        int isize = iban.length();
        for (int i = 0; i < isize; i = i + 6) {
            try {
                mod = Integer.parseInt("" + mod + iban.substring(i, i + 6), 10) % 97;
            } catch (StringIndexOutOfBoundsException e) {
                return false;
            }
        }
        return (mod == 1);
    }
    
    /**
     * Weryfikacja numeru IBAN
     * @param iban
     * @return true jeśli prawidłowy
     */
    public static boolean isValidIban(long iban) {
        return isValidIban(new Long(iban).toString());
    }
    
    /**
     * Usuwa wszelkie znaki nie będące cyframi
     * @param input
     * @return oczyszczony string
     */
    private static String trimInput(String input) {
        return input.replaceAll("\\D*","");
    }
    
    public static void main(String[] args) {
        if (isValidRegon("123456785")) Out.println("Regon OK");
    }
}