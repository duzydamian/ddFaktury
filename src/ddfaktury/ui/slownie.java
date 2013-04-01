package ddfaktury.ui;

/**
 *
 * @author gmmobile.pl
 */

import java.text.*;

public class slownie {
     /**
     * @param args the command line arguments
      * Wartość obliczana
     */
    public static void main(String[] args) {
        // TODO code application logic here
       try
       {
           {
               String kwota = (args[0]);
               double dkwota = 0;
               //Odczyt parametrów plików

               int argumenty = args.length;
               if (argumenty == 0)
               {
                Out.println("Nie podałeś kwoty !");
                return;
               }
                try
                {
                   dkwota =  Double.parseDouble(kwota);
                }
                catch (Exception ex)
                {
                    Error.println(ex);
                    return;
                }
                String kwotaSlownie = "";
                kwotaSlownie = procKwotaSlownie (dkwota);
                Out.println(kwotaSlownie);
           }
       }
       catch (Exception ex)
       {
            //Wyjątek błędu aplikacji
            Error.println(ex);
       }
    }

    // Tblica opisów wartości jednostek.
    static String[] Units =
    {
        "Zero", "Jeden", "Dwa", "Trzy", "Cztery", "Pięć", "Sześć",
        "Siedem", "Osiem", "Dziewięć", "Dziesięć", "Jedenaście",
        "Dwanaście", "Trzynaście", "Czternaście", "Piętnaście",
        "Szesnaście", "Siedemnaście", "Osiemnaście", "Dziewiętnaście"
    };

    //Tablica opisów dziesiątek
    static String[] Tens =
    {
        "Dwadzieścia", "Trzydzieści", "Czterdzieści", "Pięćdziesiąt",
        "Sześćdziesiąt", "Siedemdziesiąt", "Osiemdziesiąt",
        "Dziewięćdziesiąt"
    };

    //Tablica obisów setek
    static String[] Hundreds =
    {
        "", "Sto", "Dwieście", "Trzysta", "Czterysta", "Pięćset",
        "Sześćset", "Siedemset", "Osiemset", "Dziewięćset"
    };

    //Dwuwymiarowa tablica tysięcy,milionów,miliarów ...
    static String[][] OtherUnits =
    {
        { "Tysiąc", "Tysiące", "Tysięcy"     },
        { "Milion", "Miliony", "Milionów"    },
        { "Miliard", "Miliardy", "Miliardów" }
    };

    //Konwersja małych liczb ....
    static String SmallValueToWords(int n)
    {
        if (n == 0)
        {
            return null;
        }

        StringBuilder valueInWords = new StringBuilder();

        // Konwertuj setki.

        int temp = n / 100;
        if (temp > 0)
        {
            valueInWords.append(Hundreds[temp]);
            n -= temp * 100;
        }

        // Konwertuj dziesiątki i jedności.

        if (n > 0)
        {
            if (valueInWords.length()> 0)
            {
                valueInWords.append(" ");
            }

            if (n < 20)
            {
                //  Liczby poniżej 20 przekonwertuj na podstawie
                //  tablicy jedności.

                valueInWords.append(Units[n]);
            }
            else
            {
                //  Większe liczby przekonwertuj łącząc nazwy
                //  krotności dziesiątek z nazwami jedności.
                valueInWords.append(Tens[(n / 10) - 2]);
                int lastDigit = n % 10;

                if (lastDigit > 0)
                {
                    valueInWords.append(" ");
                    valueInWords.append(Units[lastDigit]);
                }
            }
        }
        return valueInWords.toString();
    }

    //Obliczenia dla dużych liczb ... i odmiana prawidłowa ich wartości..
    static int GetBigUnitIndex(long n)
    {
        int lastDigit = (int)n % 10;

        if ((n >= 10 && (n <= 20 || lastDigit == 0)) ||
            (lastDigit > 4))
        {
            return 2;
        }
        return (lastDigit == 1) ? 0 : 1;
    }

    static long ToWords(StringBuilder valueInWords, long n, int level)
    {
        int smallValue = 0;
        //long divisor = (long)Math.pow(10000, (long)level + 1);
        long divisor = (long)Math.pow(1000, (long)level + 1);

        if (divisor <= n)
        {
            //  Jeśli liczbę da się podzielić przez najbliższą
            //  potęgę 1000, kontynuuj rekurencję.

            n = ToWords(valueInWords, n, level + 1);
            smallValue = (int)(n / divisor);

            if (valueInWords.length() > 0)
            {
                valueInWords.append(" ");
            }

            if (smallValue > 1)
            {
                valueInWords.append(SmallValueToWords(smallValue));
                valueInWords.append(" ");
            }
            valueInWords.append( OtherUnits[level][GetBigUnitIndex(smallValue)]);
        }

        return n - smallValue * divisor;
    }

    static String ToWords(long value)
    {
        if (value == 0)
        {
            // Zero.
            return Units[0];
        }
        StringBuilder valueInWords = new StringBuilder();
        int smallValue = (int)ToWords(valueInWords, value, 0);
        if (smallValue > 0)
        {
            if (valueInWords.length() > 0)
            {
                valueInWords.append(" ");
            }
            valueInWords.append(SmallValueToWords(smallValue));
        }
        return valueInWords.toString();
    }

    static long liczba_zlotych(double kwota)
    {
        DecimalFormat dfx =  new DecimalFormat("0");
        String szlote = dfx.format(kwota);
        Long dzlote = new Long(szlote);
        return (long) dzlote;
    }

    static long liczba_groszy(double grosze)
    {
        //Tworzę format zmiennych aby uzyskać liczbę w frmie tekstowej
        DecimalFormat dfx =  new DecimalFormat("0.00");
        String szlote = dfx.format(grosze);
        //Odcinam grosze
        String bgzlote = (String)szlote.substring(0,szlote.length()-3);
        Double dzlote = new Double(bgzlote);
        //Od kowty z groszami odejmuję kwotę bez.
        Long groszy  = (long)(grosze*100 - dzlote*100);
        return groszy;
    }

    static String procKwotaSlownie (double kwota)
    {
        //Generalna funkcja ptzetworzenia zmiennej
        if (kwota < 0)
        {
            kwota = kwota * -1;
        }
        String strKwotaSl = "";
        strKwotaSl = ToWords(liczba_zlotych(kwota))+ " zł "+ ToWords(liczba_groszy(kwota))+" gr.";
        return strKwotaSl;
    }
}