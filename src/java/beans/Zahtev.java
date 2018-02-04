/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author user
 */
@Entity(name = "zahtevi")
@Data
@NoArgsConstructor
public class Zahtev implements Serializable {

    @GenericGenerator(name = "kaugen", strategy = "increment")
    @GeneratedValue(generator = "kaugen")
    @Id
    private int id;

    private String name;
    private String surname;
    @Column(unique = true)
    private String username;
    private String password;
    private String confirmPass;
    private String phone;
    private String email;

    public String toString() {
        return username;
    }
}
