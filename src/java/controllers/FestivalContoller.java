/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Model.Location;
import Model.Movie;
import beans.Festival;
import beans.Place;
import beans.Projection;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import lombok.Data;
import org.hibernate.Transaction;

/**
 *
 * @author user
 */
@ManagedBean(name = "festivalController")
@SessionScoped
@Data
public class FestivalContoller implements Serializable {

    private Festival festival;

//List of locations for festival
    private List<Location> locations;
    private List<Projection> projections;
    private UIComponent component;

    private List<Place> places;
    private int chosenPlace;
    private Place placeObj;
//List of all possible locations for place
    private List<Location> allLocations;
    private int chosenLocation;
    private Location locationObj;
    private Location locationForProjection;
    private String locationMessage;

    private List<Movie> movies;
    private int chosenMovie;
    private Movie movieObj;

    private String chosenHall;
    private Movie movieToAdd;
    private Projection projection;

    private Date timeObj;
    private String festivalMessage;
    
    
    private int currentStep = 1;
    private String stepPage = "firstStep";
    private boolean complete = false;

    public FestivalContoller() {
        locations = new LinkedList();
        projections = new LinkedList();
        initializePlacesAndLocations();
        updateMovies();
        festival = new Festival();
        movieObj = new Movie();
        projection = new Projection();
    }

    public String getFestivalMessage() {
        String pom = festivalMessage;
        festivalMessage = "";
        return pom;
    }

    public void initializePlacesAndLocations() {

        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        try {
            Criteria criteria = s.createCriteria(Place.class);
            places = criteria.list();
            if (!places.isEmpty()) {
                placeObj = places.get(0);
                chosenPlace = placeObj.getId();
                allLocations = placeObj.getLocations();
                if (!allLocations.isEmpty()) {
                    locationObj = allLocations.get(0);
                    chosenLocation = locationObj.getId();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s.close();
            sf.close();
        }
    }

    public void updatePlacesAndLocations() {
        placeObj = places.stream()
                .filter(p -> p.getId() == Integer.valueOf(chosenPlace))
                .findFirst()
                .orElse(null);
        allLocations = placeObj.getLocations();
        chosenLocation = !allLocations.isEmpty() ? allLocations.iterator().next().getId() : 0;
        updateChosenLocationObj();
    }

    public void addLocation() {
        Location locToAdd = allLocations.stream()
                .filter(l -> l.getId() == chosenLocation)
                .findFirst()
                .orElse(null);
        if (locToAdd != null && locations.contains(locToAdd)) {
            locationMessage = "This location is allready added! Please add another location or proceed further!";
            return;
        }
        if (locationForProjection == null) {
            locationForProjection = locToAdd;
        }
        if (locToAdd != null) {
            locations.add(locToAdd);
        }

    }

    public void deleteLocation() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params
                = fc.getExternalContext().getRequestParameterMap();
        int id = Integer.valueOf(params.get("idToDelete"));
        for (Location l : locations) {
            if (l.getId() == id) {
                locations.remove(l);
                break;
            }
        }
    }

    public void updateData() {

    }

    public void saveMovie() {
        if (movieObj != null) {
            Configuration cfg = new Configuration();
            cfg.configure("resources/hibernate.cfg.xml");
            SessionFactory sf = cfg.buildSessionFactory();
            Session s = sf.openSession();
            try {
                Transaction tx = s.beginTransaction();
                s.save(movieObj);
                tx.commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                s.close();
                sf.close();
            }

            movies.add(movieObj);
            chosenMovie = movieObj.getId();
            projection.setMovie(movieObj);
        }
    }

    public void updateChosenLocationObj() {
        if (allLocations != null) {
            this.locationObj = allLocations.stream()
                    .filter(l -> (l.getId() == chosenLocation))
                    .findAny().orElse(null);
        }
    }

    public void updateChosenLocationObjForProjection() {
        if (locations != null) {
            this.locationForProjection = locations.stream()
                    .filter(l -> (l.getId() == chosenLocation))
                    .findAny().orElse(null);
        }
    }

    public List<String> allHallsForLocation() {
        return locationForProjection != null ? locationForProjection.getSale() : null;
    }

    public void updateMovies() {
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();

        Session s = sf.openSession();
        try {
            Criteria criteria = s.createCriteria(Movie.class);
            movies = criteria.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s.close();
            sf.close();
        }
    }

    public void addProjection() {
        if (projection != null) {
            initializeProjectionObject();
            festival.addProjection(projection);

            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(component.getClientId(), new FacesMessage("Projection for movie " + projection.getMovie().getTitle()
                    + " occuring on " + projection.getDate() + "is successfully added!"));
            projection = new Projection();
        }
    }

    public void initializeProjectionObject() {
        //add movie to projection
        movieObj = fetchMovieObj();
        projection.setMovie(movieObj);
        movieObj = new Movie();

        //addDate to projection
        Date helpDate = projection.getDate();
        helpDate.setHours(timeObj.getHours());
        helpDate.setMinutes(timeObj.getMinutes());
        projection.setDate(helpDate);

        //set Location
        projection.setLocation(locationForProjection);
    }

    public Movie fetchMovieObj() {
        return movies.stream()
                .filter(m -> m.getId()==chosenMovie)
                .findFirst()
                .orElse(null);
    }

    public void onLoadSecondPage() {
        //method to initialize chosenLocation
        //locationObjForProjection
        chosenLocation = locationForProjection != null ? locationForProjection.getId() : null;
    }

    public String addNewFestival() {
        if (festival == null) {
            return "";
        }
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        try {
            Transaction tx = s.beginTransaction();
            festival.setFestivalLocations(locations);
            s.saveOrUpdate(festival);
            for (Projection p : festival.getProjections()) {
                p.setFestival(festival);
                s.saveOrUpdate(p);
            }
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s.close();
            sf.close();
        }
        festival = new Festival();
        locations = new LinkedList<>();
        resetSteps();
        return "";
    }

    public List<String> allDatesInPeriod() {
        List<String> dates = new ArrayList<>();
        LocalDate start = toLocalDate(festival.getStartDate());
        LocalDate end = toLocalDate(festival.getEndDate());
        if (start == null || end == null) {
            return new LinkedList<>();
        }
        while (!start.equals(end)) {
            dates.add(start.toString());
            start = start.plusDays(1);
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
    
    
    public void nextStep() {
        currentStep++;

        switch (currentStep) {
            case 1:
                stepPage = "firstStep";
                break;
            case 2:
                stepPage = "secondStep";
                break;
            case 3:
                stepPage = "thirdStep";
                complete = true;
                break;
            default:
                stepPage = "firstStep";
                complete = false;
                currentStep = 1;
                break;
        }
    }

    public void resetSteps() {
        stepPage = "firstStep";
        currentStep = 1;
        complete = false;
    }
}
