/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BLL;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class DateUtil {
    
    /**
     * Returns the date formatted
     * 
     * @param date is the variable the be formatted
     * @return a formatted date as string
     */
    public static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM-yyyy");
        return formatter.format(date);
    }
    
    /**
     * Returns the date formatted with today and yesterday
     * 
     * @param date is the variable the be formatted
     * @return a formatted date as string
     */
    public static String formatDateConvenient(LocalDate date) {

        // check if date is today
        if(date.isEqual(LocalDate.now())) {
            return "I dag";
            
        // check if date is yesterday
        } else if(date.isEqual(LocalDate.now().minusDays(1))) {
            return  "I går";
            
        // return formatted date string
        } else {
            return formatDate(date);
        }
    }
}
