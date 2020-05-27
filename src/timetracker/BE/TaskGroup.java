/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BE;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class TaskGroup extends TaskBase {

    private String name;
    private String time;
    private List<TaskParent> parents = new ArrayList<>();

    /**
     * Constructor for TaskGroup
     */
    public TaskGroup() {}

    /**
     * Returnerer name
     * @return 
     */
    public String getName() {
        return name;
    }

    /**
     * Sætter name
     * @param name 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returnerer time
     * @return 
     */
    public String getTime() {
        return time;
    }

    /**
     * Sætter time
     * @param time 
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Returnerer parents
     * @return 
     */
    public List<TaskParent> getParents() {
        return parents;
    }

    /**
     * Sætter parents
     * @param parents 
     */
    public void setParents(List<TaskParent> parents) {
        this.parents = parents;
    }
    
    /**
     * Tilføjer parent
     * @param parent 
     */
    public void addParent(TaskParent parent) {
        parents.add(parent);
    }
}
