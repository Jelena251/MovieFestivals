/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Model.Location;
import beans.Korisnik;
import beans.Place;
import beans.Zahtev;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
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
@ManagedBean(name = "controller")
@SessionScoped
@Data
public class MainController implements Serializable {

    private String username;
    private String password;
    private String poruka;
    private String newPassword;
    private String newPasswordConfirm;
    private Korisnik logedInUser;
    private String place;
    private String location;
    private List<String> sale;
    private String sala;
    private Zahtev zahtev;

    public MainController() {
        sale = new LinkedList();
        zahtev = new Zahtev();
    }


    public String login() {
        poruka = "";
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        try {

            Transaction t = s.beginTransaction();
            Criteria criteria = s.createCriteria(Korisnik.class);
            criteria.add(Restrictions.eq("username", username));
            Korisnik korisnik = (Korisnik) criteria.uniqueResult();
            if (korisnik != null) {
                if (korisnik.getPassword().equals(this.password)) {
                    logedInUser = korisnik;
                    setSessionUser(korisnik);
                    return korisnik.getRole() + "?faces-redirect=true";
                } else {
                    poruka = "Passwords do not match!";
                }
            } else {
                setPoruka("Username does not exists!");
            }
        } catch (Exception e) {

        } finally {
            s.close();
            sf.close();
        }
        return "index?faces-redirect=true";
    }

    public String changePassword() {
        String address = "confirmPassword";
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        try {
            if (!newPassword.equals(newPasswordConfirm)) {
                poruka = "New password and confirmed password should be the same!";
                return address;
            }
            Transaction t = s.beginTransaction();
            Criteria criteria = s.createCriteria(Korisnik.class);
            criteria.add(Restrictions.eq("username", username));
            Korisnik korisnik = (Korisnik) criteria.uniqueResult();
            if (korisnik != null) {
                if (korisnik.getPassword().equals(this.password)) {
                    korisnik.setPassword(newPassword);
                    s.saveOrUpdate(korisnik);
                } else {
                    poruka = "Passwords do not match!";
                }
            } else {
                setPoruka("Username does not exists!");
            }
            t.commit();
            poruka = "Password successfully changed. You can log in now";
            address = "login?faces-redirect=true";
        } catch (Exception e) {

        } finally {
            s.close();
            sf.close();
        }
        return address;
    }

    public String logOut() {
        logedInUser = null;
        poruka = "";
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        session.invalidate();
        return "index?faces-redirect=true";
    }

    public void setSessionUser(Korisnik korisnik) {

        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        session.setAttribute("korisnik", korisnik);
    }

    public String addPlace() {
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();
        Place placeObj = new Place();
        placeObj.setName(this.place);
        placeObj.setLocations(new LinkedList<Location>());
        try {
            s.save(placeObj);
            t.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s.close();
            sf.close();
        }
        return "administrator?faces-redirect=true";
    }

    public String addLocation() {
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();
        Location locationObj = new Location();
        Place p = findPlace(place);
        if (p == null) {
            this.poruka = "This place does not exists in the database! Please choose one of the existing places or add a new one!";
            s.close();
            return "addLocation?faces-redirect=true";
        }

        locationObj.setPlace(p);
        locationObj.setSale(sale);
        locationObj.setName(location);
        p.addLocation(locationObj);
        try {
            s.saveOrUpdate(locationObj);
            s.saveOrUpdate(p);
            t.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s.close();
            sf.close();
        }
        return "administrator?faces-redirect=true";
    }

    /**
     * Get all possible places
     *
     */
    public List<Place> getPlaces() {
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        Criteria criteria = s.createCriteria(Place.class);
        List<Place> places = criteria.list();
        s.close();
        sf.close();
        return places;
    }

    /**
     * get Place object for the String of place name
     *
     * @param placeName
     * @return
     */
    public Place findPlace(String placeName) {
        for (Place p : getPlaces()) {
            if (placeName.equals(p.getName())) {
                return p;
            }
        }
        return null;
    }

    public void addHall() {
        sale.add(sala);
    }

    public void deleteSala() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params
                = fc.getExternalContext().getRequestParameterMap();
        String sala = params.get("salaToDelete");
        sale.remove(sala);
    }

    public void updateData() {

    }

    public String register() {
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();
        s.save(zahtev);
        t.commit();
        s.close();
        sf.close();

        zahtev = new Zahtev();
        return "index?faces-redirect=true";
    }

}
