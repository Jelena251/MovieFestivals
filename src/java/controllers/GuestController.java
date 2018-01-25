/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import beans.Festival;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.TypedQuery;
import lombok.Data;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author user
 */
@ManagedBean
@Data
@ViewScoped
public class GuestController implements Serializable{

    private String name;
    private String resultMsg;
    private String dateMessage;

    private Date startDate;
    private Date endDate;
    private Date currentDate;
    private List<Festival> results;

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
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        try {
            Criteria criteria = s.createCriteria(Festival.class);
            if (name != null) {
                criteria.add(Restrictions.like("name", name+'%'));
            }
            if (startDate != null && endDate != null) {
                criteria.add(Restrictions.ge("endDate", currentDate));
                criteria.add(Restrictions.ge("endDate", startDate));
                criteria.add(Restrictions.le("endDate", endDate));
            } else if (startDate != null) {
                criteria.add(Restrictions.ge("endDate", currentDate));
                criteria.add(Restrictions.ge("startDate", startDate));
            } else if (endDate != null ) {
                criteria.add(Restrictions.ge("endDate", currentDate));
                criteria.add(Restrictions.le("endDate", endDate));
            }
            criteria.addOrder(Order.asc("startDate"));
            results = criteria.list();
            name=null;
            startDate = null;
            endDate = null;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            s.close();
            sf.close();
        }

    }
}
