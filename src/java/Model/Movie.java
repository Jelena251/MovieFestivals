/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

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
    

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "movie", cascade = {CascadeType.ALL})
    @Lob
    private List<ImageObject> images;

    @Transient
    private UIComponent component;

    public Movie() {
        images = new ArrayList<>();
    }
    
    public Movie(String title, String originalTitle, String year, String summary, String Director, String Stars, String runtime, String country, String link1, String link2){
        this.title=title;
        this.originalTitle=originalTitle;
        this.year=Integer.valueOf(year);
        this.summary=summary;
        this.director=Director;
        this.actors=Stars;
        this.runtime=Integer.valueOf(runtime);
        this.countries=country;
        this.link1=link1;
        this.link2=link2;
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

    public void addPicture(byte[] image, String title) {
        images.add(new ImageObject(image, title));
    }

    public DefaultStreamedContent getByTitle(String title) {
        ImageObject obj = images.stream()
                .filter(image -> title.equals(image.getTitle()))
                .findFirst()
                .orElse(null);
        if (obj != null) {
            return obj.getImageObject();
        }
        return null;
    }

    public StreamedContent getImage() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            // So, browser is requesting the image. Get ID value from actual request param.
            String id = context.getExternalContext().getRequestParameterMap().get("id");
            ImageObject obj = images.stream()
                    .filter(im -> (im.getId() == Integer.valueOf(id) ))
                    .findFirst()
                    .orElse(null);
            if (obj != null) {
                return obj.getImageObject();
            }
            return null;
        }
    }
}
