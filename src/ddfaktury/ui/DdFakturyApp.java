/*
 * DdFakturyApp.java
 */

package ddfaktury.ui;

import ddfaktury.utils.HibernateUtil;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class DdFakturyApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new DdFakturyView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of DdFakturyApp
     */
    public static DdFakturyApp getApplication() {
        return Application.getInstance(DdFakturyApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
          String fakturyDir = "fakturyPDF";
          String copyDir = "dbCopy";
          File konfiguracja = new File("ddFaktury.conf");

        try {
                System.setErr(new PrintStream("err.log"));
                System.setOut(new PrintStream("out.log"));
            } catch (FileNotFoundException ex) {
                Error.println(ex);
            }

          boolean success = (
          new File(fakturyDir)).mkdir();
          if (success) {
          Out.println(" Folder '"+ fakturyDir + "' został stworzony.");
           }

          boolean success2 = (
          new File(copyDir)).mkdir();
          if (success) {
          Out.println(" Folder '"+ copyDir + "' został stworzony.");
           }

          if(konfiguracja.exists())
            Out.println("Konfiguracja istnieje");
          else{
              try {
                    konfiguracja.createNewFile();
                    PrintWriter wypelnij = new PrintWriter(konfiguracja);
                    if (System.getProperty("file.separator").compareTo("/")==0){
                        wypelnij.println("xdg-open ");
                        wypelnij.println("xdg-open ");
                    }else{
                        wypelnij.println("C:\\Program Files\\Adobe\\Reader 10.0\\Reader\\AcroRd32.exe ");
                        wypelnij.println("explorer ");
                    }wypelnij.close();
                    } catch (IOException ex) {
                        Error.println(ex);
                    }
              Out.println("Plik z konfiguracją został stworzony.");
              Out.println("Plik z konfiguracją został wypełniony danymi domyślnymi.");
          }
          try{
            final SplashScreen splash = SplashScreen.getSplashScreen();
            if (splash == null) {
                Out.println("SplashScreen.getSplashScreen() returned null");
            }
            Graphics2D g = splash.createGraphics();
            if (g == null) {
                Out.println("g is null");
            }
          }catch(Exception ex){
              Error.println(ex);
          }
          HibernateUtil.getSessionFactory();
          /*
          Out.println("double: "+MyDouble.doubleRet(1.2121212423423423));
          Out.println("Double: "+MyDouble.DoubleRet(1.2121212423423423));
          Out.println("String: "+MyDouble.StringRet(1.2121212423423423));

          Out.println("double: "+MyDouble.doubleRet(1.000000000));
          Out.println("Double: "+MyDouble.DoubleRet(1.000000));
          Out.println("String: "+MyDouble.StringRet(1.000000));

          Out.println("double: "+MyDouble.doubleRet(1.20000));
          Out.println("Double: "+MyDouble.DoubleRet(1.20000));
          Out.println("String: "+MyDouble.StringRet(1.20000));*/
        launch(DdFakturyApp.class, args);
    }
}