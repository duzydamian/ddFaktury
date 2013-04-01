package ddfaktury.entity;
// Generated 2012-02-19 17:28:55 by Hibernate Tools 3.2.1.GA


import ddfaktury.ui.MyDouble;
import ddfaktury.utils.HibernateUtil;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * Faktury generated by hbm2java
 */
public class Faktury  implements java.io.Serializable {


     private Integer id;
     private Klienci klienci;
     private Sprzedawca sprzedawca;
     private Date dataSprzedazy;
     private Date dataWystawienia;
     private String numer;
     private double suma;
     private Date data;
     private String listaProduktow;
     private String platnosc;

    public String getPlatnosc() {
        return platnosc;
    }

    public void setPlatnosc(String platnosc) {
        this.platnosc = platnosc;
    }
     private Set listaProduktows = new HashSet(0);
     private Set listaProduktows_1 = new HashSet(0);

    public Faktury() {
    }
	
    public Faktury(Klienci klienci, Sprzedawca sprzedawca, Date dataSprzedazy, Date dataWystawienia, String numer, double suma, String platnosc) {
        this.klienci = klienci;
        this.sprzedawca = sprzedawca;
        this.dataSprzedazy = dataSprzedazy;
        this.dataWystawienia = dataWystawienia;
        this.numer = numer;
        this.suma = suma;
        this.platnosc = platnosc;
    }

    public Faktury(Klienci klienci, Sprzedawca sprzedawca, Date dataSprzedazy, Date dataWystawienia, String numer, double suma, Date data, String listaProduktow, Set listaProduktows, Set listaProduktows_1, String platnosc) {
       this.klienci = klienci;
       this.sprzedawca = sprzedawca;
       this.dataSprzedazy = dataSprzedazy;
       this.dataWystawienia = dataWystawienia;
       this.numer = numer;
       this.suma = suma;
       this.data = data;
       this.listaProduktow = listaProduktow;
       this.platnosc = platnosc;
       this.listaProduktows = listaProduktows;
       this.listaProduktows_1 = listaProduktows_1;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public Klienci getKlienci() {
        return this.klienci;
    }

    public String getKlienciNazwa(){
        return this.klienci.getNazwa();
    }
    
    public void setKlienci(Klienci klienci) {
        this.klienci = klienci;
    }
    public Sprzedawca getSprzedawca() {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

        return this.sprzedawca;
    }
    
    public void setSprzedawca(Sprzedawca sprzedawca) {
        this.sprzedawca = sprzedawca;
    }
    public Date getDataSprzedazy() {
        return this.dataSprzedazy;
    }
    
    public void setDataSprzedazy(Date dataSprzedazy) {
        this.dataSprzedazy = dataSprzedazy;
    }
    public Date getDataWystawienia() {
        return this.dataWystawienia;
    }
    
    public void setDataWystawienia(Date dataWystawienia) {
        this.dataWystawienia = dataWystawienia;
    }
    public String getNumer() {
        return this.numer;
    }
    
    public void setNumer(String numer) {
        this.numer = numer;
    }
    public double getSuma() {
        return this.suma;
    }
    
    public void setSuma(double suma) {
        this.suma = suma;
    }
    public Date getData() {
        return this.data;
    }
    
    public void setData(Date data) {
        this.data = data;
    }
    public String getListaProduktow() {
        return this.listaProduktow;
    }
    
    public void setListaProduktow(String listaProduktow) {
        this.listaProduktow = listaProduktow;
    }
    public Set getListaProduktows() {
        return this.listaProduktows;
    }
    
    public void setListaProduktows(Set listaProduktows) {
        this.listaProduktows = listaProduktows;
    }
    public Set getListaProduktows_1() {
        return this.listaProduktows_1;
    }
    
    public void setListaProduktows_1(Set listaProduktows_1) {
        this.listaProduktows_1 = listaProduktows_1;
    }

        public static List<Faktury> getAll() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List result = session.createQuery("from Faktury").list();
        session.getTransaction().commit();
        return result;
        }

        public static Vector<Faktury> getAllAsVector() {
            Vector tableData = new Vector();
//            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//            session.beginTransaction();

            for(Faktury o : getAll()) {
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(o.getId());
            oneRow.add(o.getNumer());
            oneRow.add(o.getDataSprzedazy());
            oneRow.add(o.getDataWystawienia());            
            oneRow.add(o.getSprzedawca().getNazwa());
            oneRow.add(o.getKlienci().getNazwa());
            oneRow.add(MyDouble.StringRet(o.getSuma()));
            boolean add = tableData.add(oneRow);
            }
//            session.getTransaction().commit();
            return tableData;
        }

        public static int getNewId() {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            List<Faktury> result = session.createQuery("from Faktury order by(id)").list();
            session.getTransaction().commit();
            if(result.size()==0)
                return 0;
            else
                return (result.get(result.size()-1).getId()+1);
        }
/*
        public static int getLastNumber() {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            List<Faktury> result = session.createQuery("from Faktury order by(numer)").list();
            session.getTransaction().commit();
            return (result.get(result.size()-1).);
        }
*/
    public static Faktury getByID(int numer) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<Faktury> result = session.createQuery("from Faktury where id="+numer).list();
        session.getTransaction().commit();
        return result.get(0);
    }

    public static Faktury getByNumber(String numer) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<Faktury> result = session.createQuery("from Faktury where numer='"+numer+"'").list();
        session.getTransaction().commit();
        return result.get(0);
    }

    public static void deleteByID(int numer) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery("delete from Faktury where id = "+numer);
        int rowCount = query.executeUpdate();
    }

    public static void add(Klienci klienci, Sprzedawca sprzedawca, Date dataSprzedazy, Date dataWystawienia, String numer, double suma, String platnosc) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Faktury pr = new Faktury(klienci, sprzedawca, dataSprzedazy, dataWystawienia, numer, suma, platnosc);
        session.save(pr);

        session.getTransaction().commit();
    }

    public static String getLastNumber() {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            List<Faktury> result = session.createQuery("from Faktury order by numer desc").list();
            session.getTransaction().commit();
            if(result.size()==0)
                return "1/01/2012";
            else
                if(result.get(0).getNumer().contains("99")){
                    try{
                        return getNumberBigeerThanHunder();
                    }catch(Exception e){
                        return result.get(0).getNumer();
                    }
                }else
                    return result.get(0).getNumer();
        }
    public static String getNumberBigeerThanHunder(){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            List<Faktury> result = session.createQuery("from Faktury where numer>=100 order by numer desc").list();
            session.getTransaction().commit();
            return result.get(0).getNumer();
    }
}

