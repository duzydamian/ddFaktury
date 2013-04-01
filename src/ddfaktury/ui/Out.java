/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ddfaktury.ui;

import java.text.SimpleDateFormat;

/**
 *
 * @author duzydamian
 */
public class Out {

    static SimpleDateFormat dataFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss \n");

    public static void println(String message){
        System.out.println(dataFormat.format(java.util.Calendar.getInstance().getTime())+message);
    }

    public static void println(Object message){
        System.out.println(dataFormat.format(java.util.Calendar.getInstance().getTime())+message);
    }
}
