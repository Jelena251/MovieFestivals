/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Serializable;
import javax.faces.component.UIComponent;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author user
 */
@Entity(name = "movies")
@Data
public class Movie implements Serializable {

    @GenericGenerator(name = "kaugen", strategy = "increment")
    @GeneratedValue(generator = "kaugen")
    @Id
    private int id;
    @Column(unique=true)
    private String title;
    @Column(unique=true)
    private String originalTitle;
    private int year;
    private String summary;
    private String director;
    private String actors;
    private int runtime;
    private String countries;
    private String link1;
    private String link2;

    @Transient
    private UIComponent component;

    public UIComponent getComponent() {
        return component;
    }
    public String toString(){
        return title;
    }
}
