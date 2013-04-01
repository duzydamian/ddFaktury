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
public class Error {

    static SimpleDateFormat dataFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss \n");

    public static void println(Exception ex){
        System.err.println(dataFormat.format(java.util.Calendar.getInstance().getTime())+DdFakturyApp.class.getName()+" "+ex.toString()+": "+ex.getMessage());
    }
}
