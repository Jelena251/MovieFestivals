/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import Model.Location;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class Place implements Serializable {

    @Id
    @Column(unique = true)
    private String name;
    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Location> locations;
    private Double longitude;
    private Double latitude;

    public Place(String name, String locations) {
        this.name = name;
        this.locations = new ArrayList<>();
        String[] locationArray = locations.split(",");
        String loc = locationArray[0];
        String sala = null;
        if (locationArray.length > 1) {
            sala = locationArray[1];
        }
        Location l = new Location(loc, this);
        if(sala != null && !sala.isEmpty()){
            l.dodajSalu(sala);
        }
        this.locations.add(l);
    }
    
    public Place(String name, List<Location> locations){
        this.name = name;
        this.locations = locations;
    }

    public void addLocation(String locations) {
        String[] locationArray = locations.split(",");
        String loc = locationArray[0];
        String sala = null;
        if (locationArray.length > 1) {
            sala = locationArray[1];
        }
        Location currentLocation = new Location(loc, this);
        int locationIndex = getLocationIndex(currentLocation);
        if (locationIndex < 0) {
            if (sala != null && !sala.isEmpty()) {
                currentLocation.dodajSalu(sala);
            }
            this.locations.add(currentLocation);
        } else {
            this.locations.get(locationIndex).dodajSalu(sala);
        }
    }

    
    public void addLocation(Location l){
        locations.add(l);
    }
    public int getLocationIndex(Location l) {
        for (int i = 0; i < locations.size(); i++) {
            if (l.equals(locations.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public String toString() {
        return name;
    }

}
