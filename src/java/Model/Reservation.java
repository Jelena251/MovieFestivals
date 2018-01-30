/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import beans.Korisnik;
import beans.Projection;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 *
 * @author user
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    @Id
    String id;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "projection_id")
    Projection projection;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "user_id")
    Korisnik korisnik;
    int numOfTickets;
}
