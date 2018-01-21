/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import beans.Place;
import java.io.Serializable;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;



/**
 *
 * @author user
 */
@Entity(name="locations")
@Data
public class Location implements Serializable{
    @GenericGenerator(name = "kaugen", strategy = "increment")
    @GeneratedValue(generator = "kaugen")
    @Id
    private int id;
    private String name;
    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> sale;
    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;
    
    public String toString(){
        return name + "("+place+")";
    }
}
