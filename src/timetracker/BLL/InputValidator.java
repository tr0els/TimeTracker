/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BLL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import timetracker.DAL.DALException;
import timetracker.DAL.GetDataFacadeimpl;
import timetracker.DAL.IgetDataFacadeInterface;

/**
 *
 * @author Draik
 */
public class InputValidator {
    
    private final IgetDataFacadeInterface iGetData;
    
    private InputValidator() throws DALException{
        iGetData = new GetDataFacadeimpl();
    }

    /**
     * Singleton opsætning af vores InputValidator. singleton gør at vores
     * InputValidator ikke vil blive instansieret mere end en gang.
     */
    private static InputValidator validator = null;

    public static InputValidator getInstance() throws DALException {
        if (validator == null) {
            validator = new InputValidator();
        }
        return validator;
    }

    /**
     * funktionen som tjekker om inputtet har en email struktur
     * @param input
     * @return 
     */
    public static boolean valEmail(String input) {
        String emailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern emailPat = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPat.matcher(input);
        return matcher.find();
    }

    /**
     * tjekker om inputtet kun er i et ord og ikke har nogen numrer.
     * @param input
     * @return 
     */
    public static boolean valName(String input) {
        String emailRegex = "^[^\\d\\s]+$";
        Pattern emailPat = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPat.matcher(input);
        return matcher.find();
    }
    
    /**
     * checks if and email already exist
     */
    public boolean valExistingEmail(String email){
        return iGetData.validateExistingEmail(email);
    }

    public boolean valExistingEmailEdit(int person_id, String email) {
        return iGetData.valExistingEmailEdit(person_id, email);
    }


}
