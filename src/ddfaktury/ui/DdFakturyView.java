/*
 * DdFakturyView.java
 */

package ddfaktury.ui;

import ddfaktury.entity.Faktury;
import ddfaktury.entity.Klienci;
import ddfaktury.entity.Produkty;
import ddfaktury.entity.Sprzedawca;
import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * The application's main frame.
 */
public class DdFakturyView extends FrameView {

    public DdFakturyView(SingleFrameApplication app) {
        super(app);

        initComponents();

        try {
            try{
                konfiguracja = new File("ddFaktury.conf");
                odczytKonfiguracji = new FileReader(konfiguracja);
                buforOdczytu = new BufferedReader(odczytKonfiguracji);
                programPDF = buforOdczytu.readLine();
                programDir = buforOdczytu.readLine();
                buforOdczytu.close();
                odczytKonfiguracji.close();
                Out.println("Konfiguracja została wczytana.");
                } catch (FileNotFoundException ex) {
                    Error.println(ex);
                }                
            } catch (IOException ex) {
                Error.println(ex);
            }

                headerKlienci = new Vector<String>();
                headerKlienci.add("Id");
                headerKlienci.add("Nazwa firmy");
                headerKlienci.add("Miejscowość");
                headerKlienci.add("Kod pocztowy");
                headerKlienci.add("Adres");
                headerKlienci.add("NIP");

                headerFaktury = new Vector<String>();
                headerFaktury.add("Id");
                headerFaktury.add("Numer faktury");
                headerFaktury.add("Data sprzedaży");
                headerFaktury.add("Data wystawienia");
                headerFaktury.add("Sprzedawca");
                headerFaktury.add("Nabywca");
                headerFaktury.add("Suma brutto");

                headerProdukty = new Vector<String>();
                headerProdukty.add("Id");
                headerProdukty.add("Nazwa produktu");
                headerProdukty.add("PKWiU");
                headerProdukty.add("Jednostka");
                headerProdukty.add("Stawka VAT");
                headerProdukty.add("Cena brutto");

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = DdFakturyApp.getApplication().getMainFrame();
            aboutBox = new DdFakturyAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        DdFakturyApp.getApplication().show(aboutBox);
    }

    @Action
    public void pomoc() {
        if (pomoc == null) {
            JFrame mainFrame = DdFakturyApp.getApplication().getMainFrame();
            pomoc = new pomoc(mainFrame);
            pomoc.setLocationRelativeTo(mainFrame);
        }
        DdFakturyApp.getApplication().show(pomoc);
    }

    @Action
    public void wersjeBD() {
        if (wersjaBD == null) {
            JFrame mainFrame = DdFakturyApp.getApplication().getMainFrame();
            wersjaBD = new wersjaBD(mainFrame);
            wersjaBD.setLocationRelativeTo(mainFrame);
        }
        DdFakturyApp.getApplication().show(wersjaBD);
    }

    @Action
    public void wersjeProgram() {
        if (wersjaProgram == null) {
            JFrame mainFrame = DdFakturyApp.getApplication().getMainFrame();
            wersjaProgram = new wersjaProgram(mainFrame);
            wersjaProgram.setLocationRelativeTo(mainFrame);
        }
        DdFakturyApp.getApplication().show(wersjaProgram);
    }

    @Action
    public void pokazFaktury() {
        jTabbedPane1.setSelectedIndex(0);        
    }

    @Action
    public void pokazProdukty() {
        jTabbedPane1.setSelectedIndex(1);        
    }

    @Action
    public void pokazKlientow() {
        jTabbedPane1.setSelectedIndex(2);        
    }

    @Action
    public void pokazSprzedajacego() {
        jTabbedPane1.setSelectedIndex(3);
    }

    @Action
    public void nowyProdukt() {
//        if (nowyProdukt == null) {
            JFrame mainFrame = DdFakturyApp.getApplication().getMainFrame();
            nowyProdukt = new nowyProdukt(mainFrame, Produkty.getNewId());
            nowyProdukt.setLocationRelativeTo(mainFrame);
  //      }
        DdFakturyApp.getApplication().show(nowyProdukt);
    }

    @Action
    public void nowaFaktura() {
        //if (nowaFaktura == null) {
            JFrame mainFrame = DdFakturyApp.getApplication().getMainFrame();
            nowaFaktura = new nowaFaktura(mainFrame, Faktury.getNewId(), Faktury.getLastNumber(new SimpleDateFormat("yyyy").format(new Date())));
            nowaFaktura.setLocationRelativeTo(mainFrame);
        //}
        DdFakturyApp.getApplication().show(nowaFaktura);
    }

    @Action
    public void nowyNabywca() {
        //if (nowyNabywca == null) {
            JFrame mainFrame = DdFakturyApp.getApplication().getMainFrame();
            nowyNabywca = new nowyKlient(mainFrame, Klienci.getNewId());
            nowyNabywca.setLocationRelativeTo(mainFrame);
        //}
        DdFakturyApp.getApplication().show(nowyNabywca);
    }

    @Action
    public void edytujNabywca(int numer) {
        //if (nowyNabywca == null) {
            JFrame mainFrame = DdFakturyApp.getApplication().getMainFrame();
            edytujNabywca = new edytujKlient(mainFrame, Klienci.getByID(numer));
            edytujNabywca.setLocationRelativeTo(mainFrame);
        //}
        DdFakturyApp.getApplication().show(edytujNabywca);
    }
    
    @Action
    public void edytujProdukt(int numer) {
        //if (nowyNabywca == null) {
            JFrame mainFrame = DdFakturyApp.getApplication().getMainFrame();
            edytujProdukt = new edytujProdukt(mainFrame, Produkty.getByID(numer));
            edytujProdukt.setLocationRelativeTo(mainFrame);
        //}
        DdFakturyApp.getApplication().show(edytujProdukt);
    }

    @Action
    public void edytujFaktura(int numer) {
        //if (nowyNabywca == null) {
            JFrame mainFrame = DdFakturyApp.getApplication().getMainFrame();
            edytujFaktura = new edytujFaktura(mainFrame, Faktury.getByID(numer));
            edytujFaktura.setLocationRelativeTo(mainFrame);
        //}
        DdFakturyApp.getApplication().show(edytujFaktura);
    }
    
    @Action
    public void ustawienia() {
        if (ustawienia == null) {
            JFrame mainFrame = DdFakturyApp.getApplication().getMainFrame();
            ustawienia = new ustawienia(mainFrame, programPDF);
            ustawienia.setLocationRelativeTo(mainFrame);
        }
        DdFakturyApp.getApplication().show(ustawienia);
    }
    
    @Action
    public void importKlienci() {
      //  JOptionPane.showMessageDialog(new Component() {}, "Funkcja jeszcze nie zaimpementowana!", "Informacja",1);
        returnVal = fc.showOpenDialog(fc);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            FileReader odczytPliku = null;
            try {
                File file = fc.getSelectedFile();
                String linia = new String();
                odczytPliku = new FileReader(file);
                BufferedReader buforOdczytuPliku = new BufferedReader(odczytPliku);
                
                while((linia = buforOdczytuPliku.readLine())!=null){
                    if(linia.contains("company www")){
                    String nazwa = linia.substring(linia.indexOf("name=")+5, linia.indexOf("code", linia.indexOf("name=")+6)-2);
                    nazwa = nazwa.replaceAll("''", "\"");
                    String miejscowosc = linia.substring(linia.indexOf("place=")+7, linia.indexOf(" ", linia.indexOf("place=")+7)-1);
                    String kod = linia.substring(linia.indexOf("code=")+6, linia.indexOf("/", linia.indexOf("code=")+6)-1);
                    String adres = linia.substring(linia.indexOf("address=")+9, linia.indexOf("account", linia.indexOf("address=")+9)-2);
                   String nip = linia.substring(linia.indexOf("tic=")+5, linia.indexOf(" ", linia.indexOf("tic=")+5)-1);
                    System.out.println(nazwa +" "+ miejscowosc +" "+ kod +" "+ adres +" "+ nip + "\n");
                    Klienci.add(nazwa, miejscowosc, kod, adres, nip);
                    }
                }                
                buforOdczytuPliku.close();
                odczytPliku.close();
            } catch (Exception ex) {
                Logger.getLogger(DdFakturyView.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    odczytPliku.close();
                } catch (IOException ex) {
                    Logger.getLogger(DdFakturyView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            System.out.println("Open command cancelled by user.");
        }
    }

    @Action
    public void importProdukty() {
      //  JOptionPane.showMessageDialog(new Component() {}, "Funkcja jeszcze nie zaimpementowana!", "Informacja",1);
        returnVal = fc.showOpenDialog(fc);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            FileReader odczytPliku = null;
            try {
                File file = fc.getSelectedFile();
                String linia = new String();
                odczytPliku = new FileReader(file);
                BufferedReader buforOdczytuPliku = new BufferedReader(odczytPliku);

                while((linia = buforOdczytuPliku.readLine())!=null){
                    if(linia.contains("product pkwiu")){
                        String nazwa = linia.substring(linia.indexOf("name=")+6, linia.indexOf("code", linia.indexOf("name=")+6)-2);
                        nazwa = nazwa.replaceAll("'", "");
                        String pkwiu = linia.substring(linia.indexOf("pkwiu=")+7, linia.indexOf(" ", linia.indexOf("pkwiu=")+7)-1);
                        String jednostka = linia.substring(linia.indexOf("quanType=")+10, linia.indexOf(" ", linia.indexOf("quanType=")+10)-1);
                        int vat = Integer.valueOf(linia.substring(linia.indexOf("vat=")+5, linia.indexOf(" ", linia.indexOf("vat=")+5)-1));
                        double cena = Double.valueOf((linia.substring(linia.indexOf("netto1=")+8, linia.indexOf(" ", linia.indexOf("netto1=")+8)-1).replaceAll(",",".")));
                    System.out.println(nazwa +" "+ pkwiu +" "+ jednostka +" "+ vat +" "+ cena + "\n");
                    Produkty.add(nazwa, pkwiu, jednostka, vat, cena);
                    }
                }
                buforOdczytuPliku.close();
                odczytPliku.close();
            } catch (Exception ex) {
                Logger.getLogger(DdFakturyView.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    odczytPliku.close();
                } catch (IOException ex) {
                    Logger.getLogger(DdFakturyView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            System.out.println("Open command cancelled by user.");
        }
    }

    @Action
    public void exportKlienci() {
        JOptionPane.showMessageDialog(new Component() {}, "Funkcja jeszcze nie zaimpementowana!", "Informacja",1);
    }

    @Action
    public void exportProdukty() {        
        JOptionPane.showMessageDialog(new Component() {}, "Funkcja jeszcze nie zaimpementowana!", "Informacja",1);
        Produkty.add("ąśężźćłóń", "ąśężźćłóń", "ąśężźćłóń", 23, 10.00);
    }

    @Action
    public void poprawProdukty() {
        Produkty.clean();
    }

    @Action
    public void poprawKlienci() {
        Klienci.clean();
    }

    @Action
    public void dbKopia() {
        try {
            Runtime.getRuntime().exec("mysqldump ddFaktury -pqweasd -r dbCopy" + System.getProperty("file.separator") + new java.text.SimpleDateFormat("dd-MM-yyyy").format(new Date()) + ".sql");
        } catch (IOException ex) {
            Logger.getLogger(DdFakturyView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Action
    public void dbPrzywroc() {
        try {
            statusMessageLabel.setText("mysql ddFaktury -pqweasd < dbCopy" + System.getProperty("file.separator")+"01-03-2012.sql");
            Process pr;
            //pr = Runtime.getRuntime().exec("mysql -D ddFaktury -pqweasd < dbCopy" + System.getProperty("file.separator")+"01-03-2012.sql");
            pr = Runtime.getRuntime().exec("mysql -D ddFaktury -pqweasd -e \"show tables\"");
            statusMessageLabel.setText("mysql -D ddFaktury -pqweasd -e \"show tables\"");
            InputStream is  = pr.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String line = null;
            while ( (line = br.readLine()) != null)
                Out.println(line);
        } catch (IOException ex) {
            Logger.getLogger(DdFakturyView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Action
    public void openDir() {
        try {
            Runtime.getRuntime().exec(programDir+" fakturyPDF");
        } catch (IOException ex) {
            Logger.getLogger(DdFakturyView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        fakturyPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        faktury = new javax.swing.JTable();
        produktyPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        produkty = new javax.swing.JTable();
        klienciPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        klienci = new javax.swing.JTable();
        sprzedajacyPanel = new javax.swing.JPanel();
        sprzedajacyPanel_label = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        sprzedajacyPanel_textField = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jToolBar1 = new javax.swing.JToolBar();
        jButton3 = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        jButton5 = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        jButton6 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jButton4 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jButton9 = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu plikMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem wyjscieMenuItem = new javax.swing.JMenuItem();
        edycjaMenu = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        javax.swing.JMenu fakturaMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem nowaFakturaMenuItem = new javax.swing.JMenuItem();
        pokazFakturyMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu produktMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem nowyProduktMenuItem = new javax.swing.JMenuItem();
        pokazProduktyMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu klientMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem nowyKlientMenuItem = new javax.swing.JMenuItem();
        pokazKlientowMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu sprzedajacyMenu = new javax.swing.JMenu();
        pokazSprzedajacegoMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu pomocMenu = new javax.swing.JMenu();
        pomocMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem oprogramieMenuItem = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        fakturyPanel.setName("fakturyPanel"); // NOI18N
        fakturyPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                fakturyPanelComponentShown(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        faktury.setAutoCreateRowSorter(true);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ddfaktury.ui.DdFakturyApp.class).getContext().getResourceMap(DdFakturyView.class);
        faktury.setBackground(resourceMap.getColor("faktury.background")); // NOI18N
        faktury.setForeground(resourceMap.getColor("faktury.foreground")); // NOI18N
        faktury.setModel(new MyDefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Numer", "Data", "Sprzedawca", "Nabywca", "Lista produktów", "Suma brutto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        faktury.setInheritsPopupMenu(true);
        faktury.setName("faktury"); // NOI18N
        faktury.setSelectionBackground(resourceMap.getColor("faktury.selectionBackground")); // NOI18N
        faktury.setSelectionForeground(resourceMap.getColor("faktury.selectionForeground")); // NOI18N
        faktury.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fakturyMouseClicked(evt);
            }
        });
        faktury.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                fakturyFocusGained(evt);
            }
        });
        jScrollPane1.setViewportView(faktury);

        javax.swing.GroupLayout fakturyPanelLayout = new javax.swing.GroupLayout(fakturyPanel);
        fakturyPanel.setLayout(fakturyPanelLayout);
        fakturyPanelLayout.setHorizontalGroup(
            fakturyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fakturyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
                .addContainerGap())
        );
        fakturyPanelLayout.setVerticalGroup(
            fakturyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fakturyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("fakturyPanel.TabConstraints.tabTitle"), fakturyPanel); // NOI18N

        produktyPanel.setName("produktyPanel"); // NOI18N
        produktyPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                produktyPanelComponentShown(evt);
            }
        });

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        produkty.setAutoCreateRowSorter(true);
        produkty.setBackground(resourceMap.getColor("produkty.background")); // NOI18N
        produkty.setForeground(resourceMap.getColor("produkty.foreground")); // NOI18N
        produkty.setModel(new MyDefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Nazwa produktu", "PKWiU", "Jednostka", "Stawka VAT", "Cena brutto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        produkty.setInheritsPopupMenu(true);
        produkty.setName("produkty"); // NOI18N
        produkty.setSelectionBackground(resourceMap.getColor("produkty.selectionBackground")); // NOI18N
        produkty.setSelectionForeground(resourceMap.getColor("produkty.selectionForeground")); // NOI18N
        produkty.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                produktyMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(produkty);

        javax.swing.GroupLayout produktyPanelLayout = new javax.swing.GroupLayout(produktyPanel);
        produktyPanel.setLayout(produktyPanelLayout);
        produktyPanelLayout.setHorizontalGroup(
            produktyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, produktyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
                .addContainerGap())
        );
        produktyPanelLayout.setVerticalGroup(
            produktyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, produktyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("produktyPanel.TabConstraints.tabTitle"), produktyPanel); // NOI18N

        klienciPanel.setName("klienciPanel"); // NOI18N
        klienciPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                klienciPanelComponentShown(evt);
            }
        });

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        klienci.setAutoCreateRowSorter(true);
        klienci.setBackground(resourceMap.getColor("klienci.background")); // NOI18N
        klienci.setForeground(resourceMap.getColor("klienci.foreground")); // NOI18N
        klienci.setModel(new MyDefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Nazwa firmy", "Miejscowość", "Kod pocztowy", "Adres", "NIP"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        klienci.setInheritsPopupMenu(true);
        klienci.setName("klienci"); // NOI18N
        klienci.setSelectionBackground(resourceMap.getColor("klienci.selectionBackground")); // NOI18N
        klienci.setSelectionForeground(resourceMap.getColor("klienci.selectionForeground")); // NOI18N
        klienci.getTableHeader().setReorderingAllowed(false);
        klienci.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                klienciMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(klienci);
        klienci.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        klienci.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("klienci.columnModel.title0")); // NOI18N
        klienci.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("klienci.columnModel.title1")); // NOI18N
        klienci.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("klienci.columnModel.title2")); // NOI18N
        klienci.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("klienci.columnModel.title3")); // NOI18N
        klienci.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("klienci.columnModel.title4")); // NOI18N
        klienci.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("klienci.columnModel.title5")); // NOI18N

        javax.swing.GroupLayout klienciPanelLayout = new javax.swing.GroupLayout(klienciPanel);
        klienciPanel.setLayout(klienciPanelLayout);
        klienciPanelLayout.setHorizontalGroup(
            klienciPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(klienciPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
                .addContainerGap())
        );
        klienciPanelLayout.setVerticalGroup(
            klienciPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(klienciPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("klienciPanel.TabConstraints.tabTitle"), klienciPanel); // NOI18N

        sprzedajacyPanel.setName("sprzedajacyPanel"); // NOI18N
        sprzedajacyPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                sprzedajacyPanelComponentShown(evt);
            }
        });

        sprzedajacyPanel_label.setName("sprzedajacyPanel_label"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        javax.swing.GroupLayout sprzedajacyPanel_labelLayout = new javax.swing.GroupLayout(sprzedajacyPanel_label);
        sprzedajacyPanel_label.setLayout(sprzedajacyPanel_labelLayout);
        sprzedajacyPanel_labelLayout.setHorizontalGroup(
            sprzedajacyPanel_labelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sprzedajacyPanel_labelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sprzedajacyPanel_labelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        sprzedajacyPanel_labelLayout.setVerticalGroup(
            sprzedajacyPanel_labelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sprzedajacyPanel_labelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addContainerGap(53, Short.MAX_VALUE))
        );

        sprzedajacyPanel_textField.setName("sprzedajacyPanel_textField"); // NOI18N

        jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
        jTextField1.setEnabled(false);
        jTextField1.setName("jTextField1"); // NOI18N

        jTextField2.setText(resourceMap.getString("jTextField2.text")); // NOI18N
        jTextField2.setEnabled(false);
        jTextField2.setName("jTextField2"); // NOI18N

        jTextField3.setText(resourceMap.getString("jTextField3.text")); // NOI18N
        jTextField3.setEnabled(false);
        jTextField3.setName("jTextField3"); // NOI18N

        jTextField4.setText(resourceMap.getString("jTextField4.text")); // NOI18N
        jTextField4.setEnabled(false);
        jTextField4.setName("jTextField4"); // NOI18N

        jTextField5.setText(resourceMap.getString("jTextField5.text")); // NOI18N
        jTextField5.setEnabled(false);
        jTextField5.setName("jTextField5"); // NOI18N

        jButton7.setText(resourceMap.getString("jButton7.text")); // NOI18N
        jButton7.setName("jButton7"); // NOI18N
        jButton7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton7MouseClicked(evt);
            }
        });

        jButton8.setText(resourceMap.getString("jButton8.text")); // NOI18N
        jButton8.setName("jButton8"); // NOI18N
        jButton8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton8MouseClicked(evt);
            }
        });

        jTextField6.setText(resourceMap.getString("jTextField6.text")); // NOI18N
        jTextField6.setEnabled(false);
        jTextField6.setName("jTextField6"); // NOI18N

        jTextField7.setText(resourceMap.getString("jTextField7.text")); // NOI18N
        jTextField7.setEnabled(false);
        jTextField7.setName("jTextField7"); // NOI18N

        javax.swing.GroupLayout sprzedajacyPanel_textFieldLayout = new javax.swing.GroupLayout(sprzedajacyPanel_textField);
        sprzedajacyPanel_textField.setLayout(sprzedajacyPanel_textFieldLayout);
        sprzedajacyPanel_textFieldLayout.setHorizontalGroup(
            sprzedajacyPanel_textFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sprzedajacyPanel_textFieldLayout.createSequentialGroup()
                .addGroup(sprzedajacyPanel_textFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sprzedajacyPanel_textFieldLayout.createSequentialGroup()
                        .addContainerGap(362, Short.MAX_VALUE)
                        .addComponent(jButton8)
                        .addGap(18, 18, 18)
                        .addComponent(jButton7))
                    .addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE))
                .addContainerGap())
        );
        sprzedajacyPanel_textFieldLayout.setVerticalGroup(
            sprzedajacyPanel_textFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sprzedajacyPanel_textFieldLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(sprzedajacyPanel_textFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7)
                    .addComponent(jButton8)))
        );

        javax.swing.GroupLayout sprzedajacyPanelLayout = new javax.swing.GroupLayout(sprzedajacyPanel);
        sprzedajacyPanel.setLayout(sprzedajacyPanelLayout);
        sprzedajacyPanelLayout.setHorizontalGroup(
            sprzedajacyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sprzedajacyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sprzedajacyPanel_label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sprzedajacyPanel_textField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        sprzedajacyPanelLayout.setVerticalGroup(
            sprzedajacyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sprzedajacyPanelLayout.createSequentialGroup()
                .addGroup(sprzedajacyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(sprzedajacyPanel_textField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sprzedajacyPanel_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("sprzedajacyPanel.TabConstraints.tabTitle"), sprzedajacyPanel); // NOI18N

        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ddfaktury.ui.DdFakturyApp.class).getContext().getActionMap(DdFakturyView.class, this);
        jButton3.setAction(actionMap.get("nowaFaktura")); // NOI18N
        jButton3.setIcon(resourceMap.getIcon("jButton3.icon")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setName("jButton3"); // NOI18N
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton3);

        jSeparator6.setName("jSeparator6"); // NOI18N
        jToolBar1.add(jSeparator6);

        jButton5.setAction(actionMap.get("nowyProdukt")); // NOI18N
        jButton5.setIcon(resourceMap.getIcon("jButton5.icon")); // NOI18N
        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setName("jButton5"); // NOI18N
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton5);

        jSeparator7.setName("jSeparator7"); // NOI18N
        jToolBar1.add(jSeparator7);

        jButton6.setAction(actionMap.get("nowyNabywca")); // NOI18N
        jButton6.setIcon(resourceMap.getIcon("jButton6.icon")); // NOI18N
        jButton6.setText(resourceMap.getString("jButton6.text")); // NOI18N
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setName("jButton6"); // NOI18N
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton6);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jToolBar1.add(jSeparator1);

        jSeparator2.setName("jSeparator2"); // NOI18N
        jToolBar1.add(jSeparator2);

        jButton4.setAction(actionMap.get("openDir")); // NOI18N
        jButton4.setIcon(resourceMap.getIcon("jButton4.icon")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setName("jButton4"); // NOI18N
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton4);

        jButton10.setAction(actionMap.get("ustawienia")); // NOI18N
        jButton10.setIcon(resourceMap.getIcon("jButton10.icon")); // NOI18N
        jButton10.setText(resourceMap.getString("jButton10.text")); // NOI18N
        jButton10.setToolTipText(resourceMap.getString("jButton10.toolTipText")); // NOI18N
        jButton10.setFocusable(false);
        jButton10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton10.setName("jButton10"); // NOI18N
        jButton10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton10);

        jSeparator3.setName("jSeparator3"); // NOI18N
        jToolBar1.add(jSeparator3);

        jSeparator4.setName("jSeparator4"); // NOI18N
        jToolBar1.add(jSeparator4);

        jButton9.setAction(actionMap.get("quit")); // NOI18N
        jButton9.setIcon(resourceMap.getIcon("jButton9.icon")); // NOI18N
        jButton9.setText(resourceMap.getString("jButton9.text")); // NOI18N
        jButton9.setFocusable(false);
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton9.setName("jButton9"); // NOI18N
        jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton9);

        jSeparator5.setName("jSeparator5"); // NOI18N
        jToolBar1.add(jSeparator5);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE))
        );

        menuBar.setName("menuBar"); // NOI18N

        plikMenu.setText(resourceMap.getString("plikMenu.text")); // NOI18N
        plikMenu.setName("plikMenu"); // NOI18N

        wyjscieMenuItem.setAction(actionMap.get("quit")); // NOI18N
        wyjscieMenuItem.setIcon(resourceMap.getIcon("wyjscieMenuItem.icon")); // NOI18N
        wyjscieMenuItem.setText(resourceMap.getString("wyjscieMenuItem.text")); // NOI18N
        wyjscieMenuItem.setName("wyjscieMenuItem"); // NOI18N
        plikMenu.add(wyjscieMenuItem);

        menuBar.add(plikMenu);

        edycjaMenu.setText(resourceMap.getString("edycjaMenu.text")); // NOI18N
        edycjaMenu.setName("edycjaMenu"); // NOI18N

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        jMenuItem1.setAction(actionMap.get("importKlienci")); // NOI18N
        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAction(actionMap.get("importProdukty")); // NOI18N
        jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenu1.add(jMenuItem2);

        edycjaMenu.add(jMenu1);

        jMenu2.setText(resourceMap.getString("jMenu2.text")); // NOI18N
        jMenu2.setName("jMenu2"); // NOI18N

        jMenuItem3.setAction(actionMap.get("exportKlienci")); // NOI18N
        jMenuItem3.setText(resourceMap.getString("jMenuItem3.text")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        jMenu2.add(jMenuItem3);

        jMenuItem4.setAction(actionMap.get("exportProdukty")); // NOI18N
        jMenuItem4.setText(resourceMap.getString("jMenuItem4.text")); // NOI18N
        jMenuItem4.setName("jMenuItem4"); // NOI18N
        jMenu2.add(jMenuItem4);

        edycjaMenu.add(jMenu2);

        jSeparator8.setName("jSeparator8"); // NOI18N
        edycjaMenu.add(jSeparator8);

        jMenuItem5.setAction(actionMap.get("dbKopia")); // NOI18N
        jMenuItem5.setText(resourceMap.getString("jMenuItem5.text")); // NOI18N
        jMenuItem5.setName("jMenuItem5"); // NOI18N
        edycjaMenu.add(jMenuItem5);

        jMenuItem8.setAction(actionMap.get("dbPrzywroc")); // NOI18N
        jMenuItem8.setText(resourceMap.getString("jMenuItem8.text")); // NOI18N
        jMenuItem8.setName("jMenuItem8"); // NOI18N
        edycjaMenu.add(jMenuItem8);

        jMenu3.setText(resourceMap.getString("jMenu3.text")); // NOI18N
        jMenu3.setName("jMenu3"); // NOI18N

        jMenuItem9.setAction(actionMap.get("poprawKlienci")); // NOI18N
        jMenuItem9.setText(resourceMap.getString("jMenuItem9.text")); // NOI18N
        jMenuItem9.setName("jMenuItem9"); // NOI18N
        jMenu3.add(jMenuItem9);

        jMenuItem10.setAction(actionMap.get("poprawProdukty")); // NOI18N
        jMenuItem10.setText(resourceMap.getString("jMenuItem10.text")); // NOI18N
        jMenuItem10.setName("jMenuItem10"); // NOI18N
        jMenu3.add(jMenuItem10);

        edycjaMenu.add(jMenu3);

        menuBar.add(edycjaMenu);

        fakturaMenu.setText(resourceMap.getString("fakturaMenu.text")); // NOI18N
        fakturaMenu.setName("fakturaMenu"); // NOI18N

        nowaFakturaMenuItem.setAction(actionMap.get("nowaFaktura")); // NOI18N
        nowaFakturaMenuItem.setIcon(resourceMap.getIcon("nowaFakturaMenuItem.icon")); // NOI18N
        nowaFakturaMenuItem.setText(resourceMap.getString("nowaFakturaMenuItem.text")); // NOI18N
        nowaFakturaMenuItem.setName("nowaFakturaMenuItem"); // NOI18N
        fakturaMenu.add(nowaFakturaMenuItem);

        pokazFakturyMenuItem.setAction(actionMap.get("pokazFaktury")); // NOI18N
        pokazFakturyMenuItem.setIcon(resourceMap.getIcon("pokazFakturyMenuItem.icon")); // NOI18N
        pokazFakturyMenuItem.setText(resourceMap.getString("pokazFakturyMenuItem.text")); // NOI18N
        pokazFakturyMenuItem.setName("pokazFakturyMenuItem"); // NOI18N
        fakturaMenu.add(pokazFakturyMenuItem);

        menuBar.add(fakturaMenu);

        produktMenu.setText(resourceMap.getString("produktMenu.text")); // NOI18N
        produktMenu.setName("produktMenu"); // NOI18N

        nowyProduktMenuItem.setAction(actionMap.get("nowyProdukt")); // NOI18N
        nowyProduktMenuItem.setIcon(resourceMap.getIcon("nowyProduktMenuItem.icon")); // NOI18N
        nowyProduktMenuItem.setText(resourceMap.getString("nowyProduktMenuItem.text")); // NOI18N
        nowyProduktMenuItem.setName("nowyProduktMenuItem"); // NOI18N
        produktMenu.add(nowyProduktMenuItem);

        pokazProduktyMenuItem.setAction(actionMap.get("pokazProdukty")); // NOI18N
        pokazProduktyMenuItem.setIcon(resourceMap.getIcon("pokazProduktyMenuItem.icon")); // NOI18N
        pokazProduktyMenuItem.setText(resourceMap.getString("pokazProduktyMenuItem.text")); // NOI18N
        pokazProduktyMenuItem.setName("pokazProduktyMenuItem"); // NOI18N
        produktMenu.add(pokazProduktyMenuItem);

        menuBar.add(produktMenu);

        klientMenu.setText(resourceMap.getString("klientMenu.text")); // NOI18N
        klientMenu.setName("klientMenu"); // NOI18N

        nowyKlientMenuItem.setAction(actionMap.get("nowyNabywca")); // NOI18N
        nowyKlientMenuItem.setIcon(resourceMap.getIcon("nowyKlientMenuItem.icon")); // NOI18N
        nowyKlientMenuItem.setText(resourceMap.getString("nowyKlientMenuItem.text")); // NOI18N
        nowyKlientMenuItem.setName("nowyKlientMenuItem"); // NOI18N
        klientMenu.add(nowyKlientMenuItem);

        pokazKlientowMenuItem.setAction(actionMap.get("pokazKlientow")); // NOI18N
        pokazKlientowMenuItem.setIcon(resourceMap.getIcon("pokazKlientowMenuItem.icon")); // NOI18N
        pokazKlientowMenuItem.setText(resourceMap.getString("pokazKlientowMenuItem.text")); // NOI18N
        pokazKlientowMenuItem.setName("pokazKlientowMenuItem"); // NOI18N
        klientMenu.add(pokazKlientowMenuItem);

        menuBar.add(klientMenu);

        sprzedajacyMenu.setText(resourceMap.getString("sprzedajacyMenu.text")); // NOI18N
        sprzedajacyMenu.setName("sprzedajacyMenu"); // NOI18N

        pokazSprzedajacegoMenuItem.setAction(actionMap.get("pokazSprzedajacego")); // NOI18N
        pokazSprzedajacegoMenuItem.setIcon(resourceMap.getIcon("pokazSprzedajacegoMenuItem.icon")); // NOI18N
        pokazSprzedajacegoMenuItem.setText(resourceMap.getString("pokazSprzedajacegoMenuItem.text")); // NOI18N
        pokazSprzedajacegoMenuItem.setName("pokazSprzedajacegoMenuItem"); // NOI18N
        sprzedajacyMenu.add(pokazSprzedajacegoMenuItem);

        menuBar.add(sprzedajacyMenu);

        pomocMenu.setText(resourceMap.getString("pomocMenu.text")); // NOI18N
        pomocMenu.setName("pomocMenu"); // NOI18N

        pomocMenuItem.setAction(actionMap.get("pomoc")); // NOI18N
        pomocMenuItem.setIcon(resourceMap.getIcon("pomocMenuItem.icon")); // NOI18N
        pomocMenuItem.setText(resourceMap.getString("pomocMenuItem.text")); // NOI18N
        pomocMenuItem.setName("pomocMenuItem"); // NOI18N
        pomocMenu.add(pomocMenuItem);

        oprogramieMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        oprogramieMenuItem.setIcon(resourceMap.getIcon("oprogramieMenuItem.icon")); // NOI18N
        oprogramieMenuItem.setText(resourceMap.getString("oprogramieMenuItem.text")); // NOI18N
        oprogramieMenuItem.setName("oprogramieMenuItem"); // NOI18N
        pomocMenu.add(oprogramieMenuItem);

        jMenuItem6.setAction(actionMap.get("wersjeBD")); // NOI18N
        jMenuItem6.setText(resourceMap.getString("jMenuItem6.text")); // NOI18N
        jMenuItem6.setName("jMenuItem6"); // NOI18N
        pomocMenu.add(jMenuItem6);

        jMenuItem7.setAction(actionMap.get("wersjeProgram")); // NOI18N
        jMenuItem7.setText(resourceMap.getString("jMenuItem7.text")); // NOI18N
        jMenuItem7.setName("jMenuItem7"); // NOI18N
        pomocMenu.add(jMenuItem7);

        menuBar.add(pomocMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 502, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void sprzedajacyPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_sprzedajacyPanelComponentShown
        // TODO add your handling code here:
        try{
                sprzedawca = Sprzedawca.get();
                jTextField1.setText(sprzedawca.getNazwa());
                jTextField2.setText(sprzedawca.getMiejscowosc());
                jTextField3.setText(sprzedawca.getKod());
                jTextField4.setText(sprzedawca.getAdres());
                jTextField5.setText(sprzedawca.getNip());
                jTextField6.setText(sprzedawca.getBank());
                jTextField7.setText(sprzedawca.getNumerKonta());
                statusMessageLabel.setText("Sprzedawca: "+sprzedawca.getNazwa());
        }catch(IndexOutOfBoundsException ioobe){
            Error.println(ioobe);
            statusMessageLabel.setText("Br" +
                    "ak sprzedawcy!");
        }
    }//GEN-LAST:event_sprzedajacyPanelComponentShown

    private void jButton8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton8MouseClicked
        // TODO add your handling code here:
        jTextField1.setEnabled(true);
        jTextField2.setEnabled(true);
        jTextField3.setEnabled(true);
        jTextField4.setEnabled(true);
        jTextField5.setEnabled(true);
        jTextField6.setEnabled(true);
        jTextField7.setEnabled(true);
        statusMessageLabel.setText("Edytuj sprzedawcę!");
    }//GEN-LAST:event_jButton8MouseClicked

    private void jButton7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton7MouseClicked
        // TODO add your handling code here:
        sprzedawca = Sprzedawca.update(1, jTextField1.getText(), jTextField2.getText(), jTextField3.getText(), jTextField4.getText(), jTextField5.getText(), jTextField6.getText(), jTextField7.getText());
        jTextField1.setEnabled(false);
        jTextField2.setEnabled(false);
        jTextField3.setEnabled(false);
        jTextField4.setEnabled(false);
        jTextField5.setEnabled(false);
        jTextField6.setEnabled(false);
        jTextField7.setEnabled(false);
        statusMessageLabel.setText("Zaktualizowano sprzedawcę");
    }//GEN-LAST:event_jButton7MouseClicked

    private void produktyPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_produktyPanelComponentShown
        // TODO add your handling code here:
        produkty.setModel(new MyDefaultTableModel(Produkty.getAllAsVector(), headerProdukty));
        statusMessageLabel.setText("Ilość produktów to: "+produkty.getRowCount());
    }//GEN-LAST:event_produktyPanelComponentShown

    private void fakturyPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_fakturyPanelComponentShown
        // TODO add your handling code here:
        faktury.setModel(new MyDefaultTableModel(Faktury.getAllAsVector(), headerFaktury));
        statusMessageLabel.setText("Ilość faktur to: "+faktury.getRowCount());
    }//GEN-LAST:event_fakturyPanelComponentShown

    private void klienciPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_klienciPanelComponentShown
        // TODO add your handling code here:
        klienci.setModel(new MyDefaultTableModel(Klienci.getAllAsVector(), headerKlienci));
        statusMessageLabel.setText("Ilość klientów to: "+klienci.getRowCount());
    }//GEN-LAST:event_klienciPanelComponentShown

    private void klienciMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_klienciMouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount()>=2){
            //Out.println(klienci.getSelectedRow());
            //Out.println(klienci.getValueAt(klienci.getSelectedRow(), 0));
            statusMessageLabel.setText("Edytuj: " + klienci.getValueAt(klienci.getSelectedRow(), 1).toString());
            edytujNabywca(Integer.parseInt(klienci.getValueAt(klienci.getSelectedRow(), 0).toString()));            
        }
    }//GEN-LAST:event_klienciMouseClicked

    private void fakturyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fakturyMouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount()>=2){
            //Out.println(klienci.getSelectedRow());
            //Out.println(klienci.getValueAt(klienci.getSelectedRow(), 0));
            statusMessageLabel.setText("Edytuj: " + faktury.getValueAt(faktury.getSelectedRow(), 1).toString());
            edytujFaktura(Integer.parseInt(faktury.getValueAt(faktury.getSelectedRow(), 0).toString()));
        }
    }//GEN-LAST:event_fakturyMouseClicked

    private void produktyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_produktyMouseClicked
        // TODO add your handling code here:
        if(evt.getClickCount()>=2){
            //Out.println(klienci.getSelectedRow());
            //Out.println(klienci.getValueAt(klienci.getSelectedRow(), 0));
            statusMessageLabel.setText("Edytuj: " + produkty.getValueAt(produkty.getSelectedRow(), 1).toString());
            edytujProdukt(Integer.parseInt(produkty.getValueAt(produkty.getSelectedRow(), 0).toString()));
        }
    }//GEN-LAST:event_produktyMouseClicked

    private void fakturyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_fakturyFocusGained
        // TODO add your handling code here:
        statusMessageLabel.setText("Focus");
    }//GEN-LAST:event_fakturyFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu edycjaMenu;
    private javax.swing.JTable faktury;
    private javax.swing.JPanel fakturyPanel;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTable klienci;
    private javax.swing.JPanel klienciPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem pokazFakturyMenuItem;
    private javax.swing.JMenuItem pokazKlientowMenuItem;
    private javax.swing.JMenuItem pokazProduktyMenuItem;
    private javax.swing.JMenuItem pokazSprzedajacegoMenuItem;
    private javax.swing.JMenuItem pomocMenuItem;
    private javax.swing.JTable produkty;
    private javax.swing.JPanel produktyPanel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPanel sprzedajacyPanel;
    private javax.swing.JPanel sprzedajacyPanel_label;
    private javax.swing.JPanel sprzedajacyPanel_textField;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private File konfiguracja;
    private FileReader odczytKonfiguracji;
    private BufferedReader buforOdczytu;

    private JDialog aboutBox;
    private JDialog pomoc;
    private JDialog wersjaBD;
    private JDialog wersjaProgram;
    private JDialog nowyProdukt;
    private JDialog nowyNabywca;
    private JDialog nowaFaktura;
    private JDialog edytujProdukt;
    private JDialog edytujNabywca;
    private JDialog edytujFaktura;
    public  JDialog ustawienia;
    public static String programPDF;
    public static String programDir;

    public Sprzedawca sprzedawca;
    Vector<String> headerKlienci;
    Vector<String> headerFaktury;
    Vector<String> headerProdukty;
        //Create a file chooser
    final JFileChooser fc = new JFileChooser();

    //In response to a button click:
    int returnVal;
}