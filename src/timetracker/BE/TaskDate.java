/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BE;

import java.util.List;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
// note: if treetableview gets angry, make this class extend TaskBase so it behaves like a task
public class TaskDate {

    private String name;
    private String time;
    private List<TaskParent> parents;
}
