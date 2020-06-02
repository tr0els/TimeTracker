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
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class Sorttaskbydatedesc implements Comparator<Task>
{

    public int compare(Task a, Task b)
    {
        return b.getLastWorkedOn().compareTo(a.getLastWorkedOn());
    }

}
