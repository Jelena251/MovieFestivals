/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Model.Movie;
import beans.Festival;
import beans.Projection;
import java.io.Serializable;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import lombok.Data;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

/**
 *
 * @author user
 */
@ManagedBean
@Data
@ViewScoped
public class UserController implements Serializable {

    private String name;
    private String resultMsg;
    private String dateMessage;

    private Date startDate;
    private Date endDate;
    private Date currentDate;
    private List<Festival> festivalResults;
    private List<Projection> results;
    private List<Festival> mostRecent;
    private String movieTitle;

    public UserController() {
        findMostRecentFestivals();
    }

    public void findMostRecentFestivals() {
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        try {
            Criteria criteria = s.createCriteria(Festival.class);
            criteria.add(Restrictions.ge("endDate", getCurrentDate()));
            criteria.setMaxResults(5);
            criteria.addOrder(Order.asc("startDate"));
            mostRecent = criteria.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s.close();
            sf.close();
        }
    }

    public Date getCurrentDate() {
        currentDate = new Date();
        return currentDate;
    }

    public String getDateMessage() {
        String hM = dateMessage;
        dateMessage = null;
        return hM;
    }

    public void setStartDate(Date startDate) {
        if (getEndDate() != null && startDate.after(getEndDate())) {
            dateMessage = "Start date not valid! Start date must be before end Date!";
            this.startDate = null;
        } else {
            dateMessage = "";
            this.startDate = startDate;
        }
    }

    public void setEndDate(Date endDate) {
        if (getStartDate() != null && endDate.before(getStartDate())) {
            dateMessage = "End Date is not valid! End date must be after start Date!";
            this.endDate = null;
        } else {
            dateMessage = "";
            this.endDate = endDate;
        }
    }

    public void updateData() {

    }

    public void search() {
        results = null;
        festivalResults = null;
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        try {
            if (movieTitle == null || movieTitle.equals("")) {
                festivalResults = getResultingFestivals(s).list();
            } else {
                results = getResultingProjections(s);
            }

            movieTitle = null;
            name = null;
            startDate = null;
            endDate = null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s.close();
            sf.close();
        }

    }

    public Criteria getResultingFestivals(Session s) {
        Criteria criteria = s.createCriteria(Festival.class, "festival");
        if (name != null) {
            criteria.add(Restrictions.like("name", name + '%'));
        }
        if (startDate != null && endDate != null) {
            criteria.add(Restrictions.ge("endDate", getCurrentDate()));
            criteria.add(Restrictions.ge("endDate", startDate));
            criteria.add(Restrictions.le("endDate", endDate));
        } else if (startDate != null) {
            criteria.add(Restrictions.ge("endDate", getCurrentDate()));
            criteria.add(Restrictions.ge("startDate", startDate));
        } else if (endDate != null) {
            criteria.add(Restrictions.ge("endDate", getCurrentDate()));
            criteria.add(Restrictions.le("endDate", endDate));
        }
        criteria.addOrder(Order.asc("startDate"));
        return criteria;
    }

    public List<Projection> getResultingProjections(Session s) {

        Criteria c = s.createCriteria(Projection.class);
        c.add(Restrictions.ge("date", getCurrentDate()));
        if (startDate != null) {
            c.add(Restrictions.ge("date", startDate));
        }
        if (endDate != null) {
            c.add(Restrictions.le("date", endDate));
        }
        List<Projection> projections = c.list();

        List<Projection> resultingProjections = new ArrayList<>();
        //if there is no results than return empty list
        if (projections != null && !projections.isEmpty()) {
            if ((name == null || name == "")) {
                //if there is no other search parameter return projection Results
                for (Projection p : projections) {
                    if (p.getMovie().getOriginalTitle().equals(movieTitle)) {
                        resultingProjections.add(p);
                    }
                }

            } else {
                for (Projection p : projections) {
                    if (p.getMovie().getOriginalTitle().equals(movieTitle) && p.getFestival().getName().contains(name)) {
                        resultingProjections.add(p);
                    }
                }
            }
        }
        return resultingProjections;
    }
}
