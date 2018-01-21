/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Model.Location;
import Model.Movie;
import beans.Festival;
import beans.Projection;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIColumn;
import javax.faces.context.FacesContext;
import lombok.Data;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author user
 */
@ManagedBean
@ViewScoped
@Data
public class ProjectionController {

    private List<Projection> projections;
    private Projection projection;

    private int festivalId;
    private Festival festival;
    private List<Location> locations;
    private int locationId;
    private Location locationObj;
    private List<String> sale;
    private String sala;

    private Date projDate;
    private Date timeObj;

    public ProjectionController() {
        populateProjectionList();
    }

    public void populateProjectionList() {
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        try {
            Criteria criteria = s.createCriteria(Projection.class);
            projections = criteria.list();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s.close();
            sf.close();
        }
    }

    public void onRowEdit(RowEditEvent event) {
        DataTable o = (DataTable) event.getSource();
        Projection projection = (Projection) o.getRowData();
        String updateQuerryString = "update Projection ";
/*
        Query query = session.createQuery("update Stock set stockName = :stockName" +
    				" where stockCode = :stockCode");
query.setParameter("stockName", "DIALOG1");
query.setParameter("stockCode", "7277");
int result = query.executeUpdate();
        */
        if (locationObj != null) {
            projection.setLocation(locationObj);
            locationObj = null;
        }
        if (sala != null) {
            projection.setSala(sala);
            sala = null;
        }

        if (projDate != null) {
            Date date = projection.getDate();
            if (date != null) {
                projDate.setHours(date.getHours());
                projDate.setMinutes(date.getMinutes());
            }
            projection.setDate(projDate);
            projDate = null;
        }
       

        try {
            addToDatabase(projection);
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessage msg = new FacesMessage("Error occured on database save!");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

        //Send a confirm message
        FacesMessage msg = new FacesMessage("Projection Edited");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancel(RowEditEvent event) {

        FacesMessage msg = new FacesMessage("Edit Cancelled");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void addToDatabase(Projection p) throws Exception {
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        try {
            Transaction tx = s.beginTransaction();
            s.saveOrUpdate(p);
            tx.commit();
            System.out.println("Projection updated!!");
        } catch (Exception e) {
            throw (e);
        } finally {
            s.close();
            sf.close();
        }
    }

    public List<String> allDatesInPeriod(Projection proj) {
        Festival festival = proj.getFestival();
        List<String> dates = new ArrayList<>();
        if (festival != null) {
            LocalDate start = toLocalDate(festival.getStartDate());
            LocalDate end = toLocalDate(festival.getEndDate());
            if (start == null || end == null) {
                return new LinkedList<>();
            }
            while (!start.equals(end)) {
                dates.add(start.toString());
                start = start.plusDays(1);
            }
        }
        return dates;
    }

    public LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        Date lDate = new Date(date.getTime());
        return lDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void saveDate() {
        if (timeObj != null) {
            projDate.setHours(timeObj.getHours());
            projDate.setMinutes(timeObj.getMinutes());
        }
    }

    public void updateLocation(Projection p) {
        locationObj = null;
        if (p.getFestival() != null) {
            List<Location> festLoc = p.getFestival().getFestivalLocations();
            if (festLoc != null && !festLoc.isEmpty()) {
                locationObj = festLoc.stream()
                        .filter(l -> l.getId() == locationId)
                        .findFirst()
                        .orElse(null);
            }
        }

    }

    public List<String> fetchAllHalls(Projection p) {
        if (locationObj != null) {
            return locationObj.getSale();
        } else {
            return p.getLocation() != null ? p.getLocation().getSale() : null;
        }
    }

    public String noSellectionHallValue(Projection p) {
        List<String> sale;
        if (locationObj != null) {
            sale = locationObj.getSale();
            if (sale != null && !sale.isEmpty()) {
                return sale.get(0);
            }
        } else {
            return p.getSala();
        }
        return null;
    }

    public void updateData() {

    }
}
