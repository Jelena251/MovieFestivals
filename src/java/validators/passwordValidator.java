/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validators;

import beans.Korisnik;
import beans.Zahtev;
import constants.ConstantData;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author user
 */
@FacesValidator("user.passwordValidator")
public class passwordValidator implements Validator {

    private int uppercase;
    private int lowercase;
    private int numerics;
    private int special;
    private boolean firstUppercase;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            return;
        }
        String password = value.toString();
        if (password.length() < 8 || password.length() > 12) {
            throw new ValidatorException(new FacesMessage("Password should be at least 8 characters long and at most 12!"));
        }
        firstUppercase = true;
        if (isSubsequent(password)) {
            throw new ValidatorException(new FacesMessage("Password should not contain 2 subsequent characters!"));
        }
        if (!firstUppercase) {
            throw new ValidatorException(new FacesMessage("First character in password should be uppercase letter!"));
        }
        countCharacters(password);
        if (uppercase < 2 || lowercase < 3) {
            throw new ValidatorException(new FacesMessage("Password should have at least 2 uppercase and 3 lowercase letters!"));
        }
        if (special < 1 || numerics < 1) {
            throw new ValidatorException(new FacesMessage("Password should have at least 1 special character and 1 numeric !"));
        }
    }

    public void countCharacters(String s) {
        uppercase = 0;
        special = 0;
        numerics = 0;
        for (int i = 0; i < s.length(); i++) {
            if (Character.isUpperCase(s.charAt(i))) {
                uppercase++;
            } else if (Character.isDigit(s.charAt(i))) {
                numerics++;
            } else if (ConstantData.getSpecialCharacters().contains(s.charAt(i))) {
                special++;
            }
        }
        lowercase = s.length() - uppercase;
    }

    public boolean isSubsequent(String s) {
        Character c = s.charAt(0);
        if (!Character.isUpperCase(c)) {
            firstUppercase = false;
        }
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                return true;
            }
            c = s.charAt(i);
        }
        return false;
    }

}
