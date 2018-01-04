/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author user
 */
@ManagedBean
@Entity(name = "festival")
@ViewScoped
public class Festival implements Serializable {

    @GenericGenerator(name = "kaugen", strategy = "increment")
    @GeneratedValue(generator = "kaugen")
    @Id
    private int id;
    private String name;
    private Date startDate;
    private Date endDate;
    @OneToMany
    private List<Location> festivalLocations;
    private String about;
    @OneToMany
    private List<Movie> movies;
    @Transient
    private String chosenPlace;
    @Transient
    private String chosenLocation;
    @Transient
    private List<Place> places;
    @Transient
    private Set<Location> locations;
    @Transient
    private String movie;
    @Transient
    private String locationMessage;
    @Transient
    private String dateMessage;

    public Festival() {
        festivalLocations = new LinkedList();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        if (this.endDate != null && startDate.after(this.endDate)) {
            dateMessage = "Start date not valid! Start date must be before end Date!";
            this.startDate = null;
        } else {
            dateMessage = "";
            this.startDate = startDate;
        }
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        if (this.startDate != null && endDate.before(this.startDate)) {
            dateMessage = "End Date is not valid! End date must be after start Date!";
            this.endDate = null;
        } else {
            dateMessage = "";
            this.endDate = endDate;
        }
    }

    public List<Location> getFestivalLocations() {
        return festivalLocations;
    }

    public void setFestivalLocations(List<Location> festivalLocations) {
        this.festivalLocations = festivalLocations;
    }

    public String getChosenPlace() {
        return chosenPlace;
    }

    public void setChosenPlace(String chosenPlace) {
        this.chosenPlace = chosenPlace;
    }

    public String getChosenLocation() {
        return chosenLocation;
    }

    public void setChosenLocation(String chosenLocation) {
        this.chosenLocation = chosenLocation;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public String addNewFestival() {
        /**
         * Add new Festival
         */
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        Criteria criteria = s.createCriteria(Place.class);
        places = criteria.list();
        chosenPlace = places.get(0).getName();
        locations = allLocationsForPlace();
        s.close();
        return null;
    }

    public List<Place> getPlaces() {
        if (places == null) {
            places = allPlaces();
        }
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public Set<Location> getLocations() {
        return locations;
    }

    public void setLocations(Set<Location> locations) {
        this.locations = locations;
    }

    public String getLocationMessage() {
        return locationMessage;
    }

    public void setLocationMessage(String locationMessage) {
        this.locationMessage = locationMessage;
    }

    public String getDateMessage() {
        return dateMessage;
    }

    public void setDateMessage(String dateMessage) {
        this.dateMessage = dateMessage;
    }

    public List<Place> allPlaces() {
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        Criteria criteria = s.createCriteria(Place.class);
        places = criteria.list();
        chosenPlace = places.get(0).getName();
        locations = allLocationsForPlace();
        s.close();
        return places;
    }

    public Set<Location> allLocationsForPlace() {

        /**
         * set place in a location
         */
        locationMessage = "";
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        locations = null;
        for (Place p : places) {
            if (chosenPlace.equals(p.getName())) {
                locations = p.getLocations();
                break;
            }
        }
        chosenLocation = locations.iterator().next().toString();
        s.close();
        return locations;
    }

    public void addLocation() {
        Location locToAdd = null;
        for (Location l : locations) {
            if (l.toString().equals(chosenLocation)) {
                locToAdd = l;
                break;
            }
        }
        if (festivalLocations.contains(locToAdd)) {
            locationMessage = "This location is allready added! Please add another location or proceed further!";
            return;
        }
        festivalLocations.add(locToAdd);

    }

    public void deleteLocation() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params
                = fc.getExternalContext().getRequestParameterMap();
        int id = Integer.valueOf(params.get("idToDelete"));
        for (Location l : festivalLocations) {
            if (l.getId() == id) {
                festivalLocations.remove(l);
                break;
            }
        }
    }

    public void updateData() {

    }

}
