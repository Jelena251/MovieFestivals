/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.GenericGenerator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.primefaces.model.DefaultStreamedContent;

/**
 *
 * @author user
 */
@Entity(name = "images")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id", "image"})
public class ImageObject implements Serializable {

    @GenericGenerator(name = "kaugen", strategy = "increment")
    @GeneratedValue(generator = "kaugen")
    @Id
    private int id;
    private String title;
    @Column(name = "IMAGE")
    @Lob
    private byte[] image;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "MOVIE_ID")
    private Movie movie;
    
    public ImageObject(byte[] image, String title){
        this.title = title;
        this.image = image;
    }

    public DefaultStreamedContent getImageObject() {
        InputStream is = new ByteArrayInputStream((byte[]) image);
        return new DefaultStreamedContent(is, "image/png");
    }

}
