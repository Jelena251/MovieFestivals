/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Constatnts.ReservationStatus;
import Model.Reservation;
import beans.Korisnik;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import lombok.Data;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author user
 */
@ManagedBean(name = "resAdmin")
@Data
@ViewScoped
public class ReservationAdministrationController implements Serializable {

    private Korisnik korisnik;
    private List<Reservation> reservations;

    public ReservationAdministrationController() {
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        try {
            fetchAllReservations(s);
            checkForOutdatedReservations(s);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sf.close();
            s.close();
        }
    }

    public void fetchAllReservations(Session s) {
        Criteria c = s.createCriteria(Reservation.class);
        if (korisnik != null) {
            c.add(Restrictions.eq("korisnik.id", korisnik.getId()));

        }
        reservations = c.list();
    }

    public void checkForOutdatedReservations(Session s) throws Exception {
        Date currentDate = new Date();
        long timeConstantMilli = 1000 * 60 * 60 * 48;
        int counter = 0;
        for (Reservation r : reservations) {
            if (r.getState().equals(ReservationStatus.OUTDATED)) {
                counter++;
            }
            if ((currentDate.getTime() - r.getCreationTime().getTime()) > timeConstantMilli && r.getState().equals(ReservationStatus.RESERVED)) {
                r.setState(ReservationStatus.OUTDATED);
                counter++;
                s.saveOrUpdate(r);
                s.beginTransaction().commit();
            }
        }
        if (counter >= 3 && korisnik != null) {
            korisnik.setBlocked(true);
            s.saveOrUpdate(korisnik);
            s.beginTransaction().commit();
        }
    }
    
    
    public void cancelReservation(Reservation res){
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        try {
            res.setState(ReservationStatus.CANCELED);
            s.saveOrUpdate(res);
            s.beginTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sf.close();
            s.close();
        }
    }
}
