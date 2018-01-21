/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import Model.Location;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author user
 */
@ManagedBean
@Entity(name = "places")
@Data
public class Place implements Serializable {

    @GenericGenerator(name = "kaugen", strategy = "increment")
    @GeneratedValue(generator = "kaugen")
    @Id
    private int id;
    @Column(unique = true)
    private String name;
    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Location> locations;

    public String toString() {
        return name;
    }

    public void addLocation(Location l) {
        locations.add(l);
    }

}
