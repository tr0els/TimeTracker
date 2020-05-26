/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BLL;

import java.util.Comparator;
import timetracker.BE.Task;

/**
 *
 * @author BBran
 */
public class Sortbydate implements Comparator<Task>{
    
    public int compare(Task a, Task b){
        return b.getLast_worked_on().compareTo(a.getLast_worked_on());
    }
    
}
