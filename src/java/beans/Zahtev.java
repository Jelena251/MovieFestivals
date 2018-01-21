/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author user
 */
@ManagedBean
@Entity(name = "zahtevi")
@Data
public class Zahtev implements Serializable {

    @GenericGenerator(name = "kaugen", strategy = "increment")
    @GeneratedValue(generator = "kaugen")
    @Id
    private int id;

    private String name;
    private String surname;
    private String username;
    private String password;
    private String confirmPass;
    private String phone;
    private String email;

    public String register() {
        Configuration cfg = new Configuration();
        cfg.configure("resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();
        s.save(this);
        t.commit();
        s.close();
        return "index";
    }

    public String toString() {
        return username;
    }
}
