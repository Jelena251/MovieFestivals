/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package validators;

import beans.Korisnik;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import beans.Zahtev;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author user
 */
@FacesValidator("user.usernameUnique")
public class UsernameValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            return;
        }
        Configuration cfg = new Configuration();
        cfg.configure("/resources/hibernate.cfg.xml");
        SessionFactory sf = cfg.buildSessionFactory();
        Session s = sf.openSession();
        Transaction t = s.beginTransaction();
        Criteria criteria = s.createCriteria(Korisnik.class);
        criteria.add(Restrictions.eq("username", value.toString()));
        Criteria criteria1 = s.createCriteria(Zahtev.class);
        criteria1.add(Restrictions.eq("username", value.toString()));
        Zahtev zahtev = (Zahtev) criteria1.uniqueResult();
        Korisnik korisnik = (Korisnik) criteria.uniqueResult();

        if (korisnik != null || zahtev != null) {
            s.close();
            throw new ValidatorException(new FacesMessage("Username allready taken!"));
        }
        s.close();

    }
}
