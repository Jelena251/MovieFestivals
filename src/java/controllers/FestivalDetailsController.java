/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import beans.Festival;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import lombok.Data;

/**
 *
 * @author user
 */
@ManagedBean(name = "festDetails")
@Data
public class FestivalDetailsController implements Serializable {
    private Festival chosenFestival;
    
    public String showFestivalDetails(Festival festival){
        chosenFestival = festival;
        return "festivalDetails";
    }
}
