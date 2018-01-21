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
import javax.persistence.Transient;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author user
 */
@ManagedBean
@Entity(name = "korisnici")
@Data
public class Korisnik implements Serializable {

    @GenericGenerator(name = "kaugen", strategy = "increment")
    @GeneratedValue(generator = "kaugen")
    @Id
    private int id;

    private String name;
    private String surname;
    private String username;
    private String password;
    @Transient
    private String confirmPass;
    private String phone;
    private String email;
    private String role;

    public Korisnik(Zahtev zahtev, String role) {
        this.name = zahtev.getName();
        this.surname = zahtev.getSurname();
        this.username = zahtev.getUsername();
        this.password = zahtev.getPassword();
        this.phone = zahtev.getPhone();
        this.email = zahtev.getEmail();
        this.role = role;
    }

    public String toString() {
        return username;
    }
}
