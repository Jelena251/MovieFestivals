/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package constants;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author user
 */
public final class ConstantData {

    private static List<String> roles;
    private static Set<Character> specialCharacters; 
    public final static String OUTDATED = "outdated";
    public final static String APPROVED = "approved";
    public final static String PENDING = "pending";
    static {
        roles = new LinkedList();
        roles.add("administrator");
        roles.add("prodavac");
        roles.add("korisnik");
        
        specialCharacters = new HashSet();
        specialCharacters.add('#');
        specialCharacters.add('*');
        specialCharacters.add('.');
        specialCharacters.add('!');
        specialCharacters.add('?');
        specialCharacters.add('$');
    }

    public static List<String> getRoles() {
        return roles;
    }
    
    public static Set<Character> getSpecialCharacters(){
        return specialCharacters;
    }
}
