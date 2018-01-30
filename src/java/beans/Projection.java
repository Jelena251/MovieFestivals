/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import Model.Location;
import Model.Movie;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author user
 */
@Entity(name = "projection")
@Data
public class Projection implements Serializable {

    @GenericGenerator(name = "kaugen", strategy = "increment")
    @GeneratedValue(generator = "kaugen")
    @Id
    private int id;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "FESTIVAL_ID")
    private Festival festival;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToOne
    @JoinColumn(name = "MOVIE_ID")
    private Movie movie;

    Date date;
    private String sala;

    @OneToOne
    private Location location;

    private int availPlaces;
    private double price;

    public String toString() {
        return movie + "(" + festival + ")";
    }

}
