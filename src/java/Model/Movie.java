/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.primefaces.model.DefaultStreamedContent;

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
    @Column(unique = true)
    private String title;
    @Column(unique = true)
    private String originalTitle;
    private int year;
    private String summary;
    private String director;
    private String actors;
    private int runtime;
    private String countries;
    private String link1;
    private String link2;

    @Column(name = "POSTER")
    @Lob
    private byte[] poster;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @Lob
    private List<byte[]> images;

    @Transient
    private UIComponent component;

    public Movie() {
        images = new ArrayList<>();
    }

    public UIComponent getComponent() {
        return component;
    }

    public String toString() {
        return title;
    }

    public DefaultStreamedContent getPosterImage() {
        InputStream is = new ByteArrayInputStream((byte[]) poster);
        return new DefaultStreamedContent(is, "image/png");
    }

    public List<DefaultStreamedContent> getImages() {
        List<DefaultStreamedContent> results = new ArrayList<>();
        for (byte[] slika : images) {
            InputStream is = new ByteArrayInputStream((byte[]) slika);
            results.add(new DefaultStreamedContent(is, "image/png"));
        }
        return results;
    }

    public void addPicture(byte[] image) {
        images.add(image);
    }
}
