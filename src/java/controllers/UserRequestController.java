/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import beans.Korisnik;
import beans.Zahtev;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import lombok.Data;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author user
 */
@ManagedBean
@RequestScoped
@Data
public class UserRequestController {

    private List<Zahtev> zahtevi;

    public UserRequestController() {
        fetchRequestsFromDB();
    }

    public void fetchRequestsFromDB() {
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();

        Criteria criteria = s.createCriteria(Zahtev.class);
        zahtevi = criteria.list();

        s.close();
        sf.close();

    }

    public String approveRequest(Zahtev z, String role) {
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        Transaction tx = s.beginTransaction();

        //Create User From this request
        Korisnik korisnik = new Korisnik(z, role);
        s.save(korisnik);
        s.delete(z);
        tx.commit();

        s.close();
        sf.close();
        return "administrator";

    }
    public String removeRequest(Zahtev request) {
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        Transaction tx = s.beginTransaction();
        s.delete(request);
        tx.commit();
        s.close();
        sf.close();
        return "administrator";
    }    
    
}
