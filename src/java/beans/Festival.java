/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import Model.Location;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author jelena.zivkovic
 */
@Entity(name = "festival")
@Data
@AllArgsConstructor
public class Festival implements Serializable {

    @GenericGenerator(name = "kaugen", strategy = "increment")
    @GeneratedValue(generator = "kaugen")
    @Id
    private int id;

    @Column(unique = true)
    private String name;
    private Date startDate;
    private Date endDate;
    @OneToMany
    private List<Location> festivalLocations;
    private String about;
    @OneToMany(mappedBy = "festival")
    private List<Projection> projections;
    @Transient
    private String dateMessage;
    
    public Festival(){
       projections = new LinkedList();
    }
    

    public void setStartDate(Date startDate) {
        if (getEndDate() != null && startDate.after(getEndDate())) {
            dateMessage = "Start date not valid! Start date must be before end Date!";
            this.startDate =null;
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

    public void addProjection(Projection p){
        this.projections.add(p);
    }
}
