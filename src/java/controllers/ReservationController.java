/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Model.Reservation;
import beans.Korisnik;
import beans.Projection;
import constants.ConstantData;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import lombok.Data;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author user
 */
@ManagedBean
@SessionScoped
@Data
public class ReservationController implements Serializable {

    private Reservation reservation;
    private final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private SecureRandom rnd = new SecureRandom();
    private String message;
    private List<Projection> projections;
    private Projection projection;
    private int ticketNumber;
    private List<Integer> selectValues;

    public ReservationController() {
        reservation = new Reservation();
    }

    public void reserveFestival(Projection p, Korisnik korisnik) {
       /* if (korisnik == null || korisnik.isBlocked()) {
        
            addMessage("Failure", "Your account is blocked because of too many registrations!");
            return;
        }*/
        korisnik = korisnik;
        if (p.getAvailPlaces() < ticketNumber) {
            addMessage("Failure", "You are trying to reserve more tickets than available!");
            return;
        }
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        try {
            Transaction tx = s.beginTransaction();
            reservation.setId(randomString(10));
            reservation.setKorisnik(korisnik);
            reservation.setProjection(p);
            reservation.setState("");
            reservation.setCreationTime(new Date());
            p.setAvailPlaces(p.getAvailPlaces() - ticketNumber);
            reservation.setNumOfTickets(ticketNumber);
            s.saveOrUpdate(reservation);
            s.saveOrUpdate(p);
            tx.commit();
            addMessage("Success", "Your reservation id is" + reservation.getId() + ".\n You can check your reservations under reservations tab!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s.close();
            sf.close();
        }
    }

    public void deleteReservation(Session s, Reservation r) throws Exception {
        Transaction tx = s.beginTransaction();
        Projection p = r.getProjection();
        p.setAvailPlaces(p.getAvailPlaces() + r.getNumOfTickets());

        s.delete(r);
        s.saveOrUpdate(p);
        tx.commit();
    }

    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public List<Integer> getSelectValues(int n) {
        selectValues = new ArrayList<>();
        for (int i = 1; i < n; i++) {
            selectValues.add(i);
        }
        return selectValues;
    }

    private String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

}
