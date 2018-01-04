/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import beans.Korisnik;
import beans.Location;
import beans.Place;
import constants.ConstantData;
import java.util.HashSet;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
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
public class MainController {

    private String username;
    private String password;
    private String poruka;
    private String newPassword;
    private String newPasswordConfirm;
    private Korisnik logedInUser;
    private String place;
    private String location;
    private int currentStep = 1;
    private String stepPage = "firstStep";
    private boolean isComplete = false;
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public boolean isIsComplete() {
        return isComplete;
    }

    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }
    
    
    public void nextStep(){
        currentStep++;
        
        switch(currentStep){
            case 1: stepPage = "firstStep"; break;
            case 2: stepPage = "secondStep"; break;
            case 3: stepPage = "thirdStep"; isComplete = true; break;
        }
    }

    public String getStepPage() {
        return stepPage;
    }

    public void setStepPage(String stepPage) {
        this.stepPage = stepPage;
    }

    /**
     * User Log In
     *
     * @return
     */
    
    
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
                    return korisnik.getRole();
                } else {
                    poruka = "Passwords do not match!";
                }
            } else {
                setPoruka("Username does not exists!");
            }
        } catch (Exception e) {

        } finally {
            s.close();
        }
        return "index ";
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
            address = "login";
        } catch (Exception e) {

        } finally {
            s.close();
        }
        return address;
    }

    /**
     * @return the poruka
     */
    public String getPoruka() {
        return poruka;
    }

    /**
     * @param poruka the poruka to set
     */
    public void setPoruka(String poruka) {
        this.poruka = poruka;
    }

    /**
     * return all possible roles that user can have
     */
    public List<String> getRoles() {
        return ConstantData.getRoles();
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirm() {
        return newPasswordConfirm;
    }

    public void setNewPasswordConfirm(String newPasswordConfirm) {
        this.newPasswordConfirm = newPasswordConfirm;
    }

    public Korisnik getLogedInUser() {
        return logedInUser;
    }

    public void setLogedInUser(Korisnik logedInUser) {
        this.logedInUser = logedInUser;
    }

    public String logOut() {
        logedInUser = null;
        poruka = "";
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        session.invalidate();
        return "index";
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
        placeObj.setLocations(new HashSet<Location>());
        try {
            s.save(placeObj);
            t.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s.close();
        }
        return "administrator";
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
            return "addLocation";
        }
        
        locationObj.setPlace(p);
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
        }
        return "administrator";
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

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
