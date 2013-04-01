package ddfaktury.entity;
// Generated 2012-02-19 15:58:25 by Hibernate Tools 3.2.1.GA

import ddfaktury.ui.MyDouble;
import ddfaktury.utils.HibernateUtil;
import org.hibernate.Session;
import java.util.List;
import java.util.Vector;



/**
 * Wersje generated by hbm2java
 */
public class Wersje  implements java.io.Serializable {


     private Integer id;
     private String tresc;

    public Wersje() {
    }

    public Wersje(String tresc) {
       this.tresc = tresc;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public String getTresc() {
        return this.tresc;
    }
    
    public void setTresc(String tresc) {
        this.tresc = tresc;
    }

        public static List<Wersje> getAll() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List result = session.createQuery("from Wersje").list();
        session.getTransaction().commit();
        return result;
        }

        public static Vector<Faktury> getAllAsVector() {
            Vector tableData = new Vector();

            for(Wersje o : getAll()) {
            Vector<Object> oneRow = new Vector<Object>();
            oneRow.add(o.getId());
            //oneRow.add(o.);
            oneRow.add(o.getId());
            oneRow.add(o.getTresc());
            tableData.add(oneRow);
            }
            return tableData;
        }

        public static String getTrescByID(int id) {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            List<Wersje> result = session.createQuery("from Wersje where id='"+id+"'").list();
            session.getTransaction().commit();
            return result.get(0).getTresc();
        }



}

