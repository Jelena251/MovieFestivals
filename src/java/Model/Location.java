/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import beans.Place;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;



/**
 *
 * @author user
 */
@Entity(name="locations")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"sale"})
public class Location implements Serializable{
    @Id
    private String name;
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> sale;
    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;
    
    public Location(String name, Place place){
        this.name = name;
        this.place = place;
        this.sale = new ArrayList<>();
    }
    
    public void dodajSalu(String sala){
        this.sale.add(sala);
    }
    
    public String toString(){
        return name + "("+place+")";
    }
    
}
