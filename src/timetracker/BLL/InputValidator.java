/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BLL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Draik
 */
public class InputValidator {

    /**
     * Singleton opsætning af vores InputValidator. singleton gør at vores
     * InputValidator ikke vil blive instansieret mere end en gang.
     */
    private static InputValidator validator = null;

    public static InputValidator getInstance(){
        if (validator == null) {
            validator = new InputValidator();
        }
        return validator;
    }

    public static boolean valEmail(String input) {
        String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern emailPat = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPat.matcher(input);
        return matcher.find();
    }

}
