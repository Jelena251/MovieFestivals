/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Model.Movie;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import lombok.Data;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author user
 */
@ManagedBean
@Data
@SessionScoped
public class MovieController implements Serializable {

    private Movie movieObj;
    private UploadedFile file;

    public MovieController() {
        movieObj = new Movie();
    }

    public void saveMovie() {
        if (movieObj != null) {
            Configuration cfg = new Configuration();
            cfg.configure("resources/hibernate.cfg.xml");
            SessionFactory sf = cfg.buildSessionFactory();
            Session s = sf.openSession();
            try {
                Transaction tx = s.beginTransaction();
                movieObj.getImages().forEach(image -> image.setMovie(movieObj));
                s.save(movieObj);
                tx.commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                s.close();
                sf.close();
            }
        }
    }

    public void uploadPoster(FileUploadEvent event) {
        file = event.getFile();
        byte[] contents = file.getContents();
        movieObj.setPoster(contents);
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void upload(FileUploadEvent event) {
        file = event.getFile();
        byte[] contents = file.getContents();
        movieObj.addPicture(contents, file.getFileName());
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

}
