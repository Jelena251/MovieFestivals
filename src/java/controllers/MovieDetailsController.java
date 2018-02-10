/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Model.Movie;
import beans.Projection;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import lombok.Data;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author user
 */
@ManagedBean(name = "movieDetails")
@SessionScoped
@Data
public class MovieDetailsController {

    private Movie chosenMovie;
    private List<Projection> projections;
    
    public String showMovieDetails(Movie movie) {
        chosenMovie = movie;
        fetchAllFestivalsForMovie();
        return "movieDetails?faces-redirect=true";
    }
    
    public Date getCurrentDate(){
        return new Date();
    }
    
    public void fetchAllFestivalsForMovie(){
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        try { 
            Criteria criteria = s.createCriteria(Projection.class);
            criteria.add(Restrictions.ge("date", getCurrentDate()));
            criteria.add(Restrictions.eq("movie.id", chosenMovie.getId()));
            projections = criteria.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            s.close();
            sf.close();
        }
    }
    
}
