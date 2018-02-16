/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import Model.Location;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author jelena.zivkovic
 */
@Entity(name = "festival")
@Data
@AllArgsConstructor
public class Festival implements Serializable {
    @Id
    @Column(unique = true)
    private String name;
    private Date startDate;
    private Date endDate;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "FESTIVAL_LOCATIONS")
    private List<Location> festivalLocations;
    private String about;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "festival", cascade = {CascadeType.ALL})
    private List<Projection> projections;
    @Transient
    private String dateMessage;
    private int maxNum;
    
    public Festival() {
        projections = new LinkedList();
    }
    
    public Festival(String name, String startDate, String endDate, List<Location> locations, String about) throws ParseException{
        this.name = name;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        this.startDate = sdf.parse(startDate);
        this.endDate = sdf.parse(endDate);
        this.festivalLocations = locations;
        this.about = about;
        this.projections = new ArrayList<>();
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

    public void addProjection(Projection p) {
        this.projections.add(p);
    }

    public String toString() {
        return name;
    }

    public String locationsString() {
        String s = "";
        for (Location l : festivalLocations) {
            s = s + l.toString() + '\n';
        }

        return s;
    }

    public String locationHallsString() {
        String s = "";
        for (Location l : festivalLocations) {
            s = s + l.toString() + '\n';
            s = s + "Halls:\n";
            for (String h : l.getSale()) {
                s = s + h + '\n';
            }
        }

        return s;
    }

    public String dateString() {
        if (startDate != null && endDate != null) {
            return startDate.getDay() + '.' + startDate.getMonth() + '.' + startDate.getYear()
                    + "  -  " + endDate.getDay() + '.' + endDate.getMonth() + '.' + endDate.getYear();
        }
        return null;
    }
    
        public List<String> allDatesInPeriod() {
        List<String> dates = new ArrayList<>();
        LocalDate start = toLocalDate(this.getStartDate());
        LocalDate end = toLocalDate(this.getEndDate());
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
}
