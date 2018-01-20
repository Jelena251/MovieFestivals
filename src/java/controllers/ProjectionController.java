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

    private List<Festival> festivals;
    private List<Movie> movies;

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
        populateFestivalList();
        populateMovieList();
    }

    public void populateProjectionList() {
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();

        Criteria criteria = s.createCriteria(Projection.class);
        projections = criteria.list();

        s.close();
        sf.close();
    }

    public void populateFestivalList() {
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();

        Criteria criteria = s.createCriteria(Festival.class);
        festivals = criteria.list();

        s.close();
        sf.close();
    }

    public void populateMovieList() {
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();

        Criteria criteria = s.createCriteria(Movie.class);
        movies = criteria.list();

        s.close();
        sf.close();
    }

    public void onRowEdit(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Projection Edited");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancel(RowEditEvent event) {

        FacesMessage msg = new FacesMessage("Edit Cancelled");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onCellEdit(CellEditEvent event) {
        
        
		TreeTableColumn col= event.getTableColumn();
		DataTable o=(DataTable) event.getSource();
		Object oldValue = event.getOldValue();  //here i get old value
		Object newValue = event.getNewValue(); //new value
		Projection info=(Projection)o.getRowData();
		int newProj=info.getId();//primary key

        if (newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void updateFestivalData() {
        if (festivalId == 0) {
            return;
        }
        festival = festivals.stream()
                .filter(f -> f.getId() == festivalId)
                .findFirst()
                .orElse(null);
        if (festival != null) {
            locations = festival.getFestivalLocations();
            if (locations != null && !locations.isEmpty()) {
                locationObj = locations.get(0);
                locationId = locationObj.getId();
                sale = locations.get(0).getSale();
                sala = (sale != null) ? sale.get(0) : null;
            }

        }
    }

    public List<String> allDatesInPeriod() {
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
}
