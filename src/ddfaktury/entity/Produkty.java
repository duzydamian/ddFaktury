package ddfaktury.entity;
// Generated 2011-12-20 15:10:35 by Hibernate Tools 3.2.1.GA

import ddfaktury.ui.Out;
import ddfaktury.ui.Error;
import ddfaktury.ui.MyDouble;
import ddfaktury.utils.HibernateUtil;
import java.util.List;
import java.util.Vector;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * Produkty generated by hbm2java
 */
public class Produkty  implements java.io.Serializable {


     private Integer id;
     private String nazwa;
     private String pkwiu;
     private String jednostka;
     private int vat;
     private double cena;

    public Produkty() {
    }

    public Produkty(String nazwa, String pkwiu, String jednostka, int vat, double cena) {
       this.nazwa = nazwa;
       this.pkwiu = pkwiu;
       this.jednostka = jednostka;
       this.vat = vat;
       this.cena = cena;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNazwa() {
        return this.nazwa;
    }
    
    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }
    public String getPkwiu() {
        return this.pkwiu;
    }
    
    public void setPkwiu(String pkwiu) {
        this.pkwiu = pkwiu;
    }
    public String getJednostka() {
        return this.jednostka;
    }
    
    public void setJednostka(String jednostka) {
        this.jednostka = jednostka;
    }
    public int getVat() {
        return this.vat;
    }
    
    public void setVat(int vat) {
        this.vat = vat;
    }
    public double getCena() {
        return this.cena;
    }
    
    public void setCena(double cena) {
        this.cena = cena;
    }

        public static List<Produkty> getAll() {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            List result = session.createQuery("from Produkty").list();
            session.getTransaction().commit();
            return result;
        }

        public static List<Produkty> getAllOrder() {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            List result = session.createQuery("from Produkty order by nazwa").list();
            session.getTransaction().commit();
            return result;
        }

        public static Vector<Produkty> getAllAsVector() {
            Vector tableData = new Vector();

            for(Produkty o : getAll()) {
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(o.getId());
            oneRow.add(o.getNazwa());
            oneRow.add(o.getPkwiu());
            oneRow.add(o.getJednostka());
            oneRow.add(o.getVat());
            oneRow.add(MyDouble.StringRet(o.getCena()));
            tableData.add(oneRow);
            }
            return tableData;
        }

    public static void add(String nazwa, String pkwiu, String jednostka, int vat, double cena) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Produkty pr = new Produkty(nazwa, pkwiu, jednostka, vat, cena);
        session.save(pr);

        session.getTransaction().commit();
    }

    //public static Sprzedawca update(int id, String nazwa, String miejscowosc, String kod, String adres, String nip){
    public static void update(int id, String nazwa, String pkwiu, String jednostka, int vat, double cena){
        try{
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            Produkty pr = new Produkty(nazwa, pkwiu, jednostka, vat, cena);
            pr.setId(id);

            session.update(pr);
            session.getTransaction().commit();
            Out.println("Update successfully!");
            }catch(Exception e){
                Error.println(e);
            }
    }

    public static void update(Produkty pro){
        try{
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();

            Produkty pr = new Produkty(pro.getNazwa(),pro.getPkwiu(),pro.getJednostka(),pro.getVat(),pro.getCena());
            pr.setId(pro.getId());

            session.update(pr);
            session.getTransaction().commit();
            Out.println("Update successfully!");
            }catch(Exception e){
                Error.println(e);
            }
    }
    
        public static int getNewId() {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            List<Produkty> result = session.createQuery("from Produkty order by(id)").list();
            session.getTransaction().commit();
            if(result.size()==0)
                return 0;
            else
                return (result.get(result.size()-1).getId()+1);
        }

    public static Produkty getByID(int numer) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<Produkty> result = session.createQuery("from Produkty where id="+numer).list();
        session.getTransaction().commit();
        return result.get(0);
    }

    public static Produkty getByName(String nazwa) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<Produkty> result = session.createQuery("from Produkty where nazwa='"+nazwa+"'").list();
        session.getTransaction().commit();
        return result.get(0);
    }

    public static Double getPriceByName(String nazwa) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<Produkty> result = session.createQuery("from Produkty where nazwa='"+nazwa+"'").list();
        session.getTransaction().commit();
        return result.get(0).getCena();
    }

    public static Integer getVATByName(String nazwa) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<Produkty> result = session.createQuery("from Produkty where nazwa='"+nazwa+"'").list();
        session.getTransaction().commit();
        return result.get(0).getVat();
    }

    public static String getPKWiUByName(String nazwa) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<Produkty> result = session.createQuery("from Produkty where nazwa like'"+nazwa+"'").list();
        session.getTransaction().commit();
        return result.get(0).getPkwiu();
    }

    public static String getUnitByName(String nazwa) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<Produkty> result = session.createQuery("from Produkty where nazwa like'"+nazwa+"'").list();
        session.getTransaction().commit();
        return result.get(0).getJednostka();
    }

    public static void deleteByID(int numer) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createQuery("delete from Produkty where id = "+numer);
        int rowCount = query.executeUpdate();
    }

    public static void clean() {
        int i=0;
        for(Produkty o : getAll()) {
            o.setNazwa(replace(o.getNazwa()));
            update(o);
            i++;
        }
        System.out.println(i);
    }

    public static String replace(String text){
        text = text.replaceAll("''", "");
        text = text.replaceAll("\"", "");
        return text;
    }
}