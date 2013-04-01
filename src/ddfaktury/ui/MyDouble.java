/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ddfaktury.ui;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 *
 * @author duzydamian
 */
public class MyDouble {    

    static NumberFormat nf = NumberFormat.getInstance();
    
    public static double doubleRet(double value){
        return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
   }

    public static double doubleRet(String value){
        return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
   }

    public static Double DoubleRet(double value){
        return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }

    public static Double DoubleRet(String value){
        return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }

    public static String StringRet(double value){
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        return nf.format(new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue()).replace(".", ",");
    }

    public static String StringRet(String value){
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        return nf.format(new BigDecimal(value.replace(',', '.')).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue()).replace(".", ",");
    }
}
