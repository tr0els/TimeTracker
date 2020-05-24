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
public class TaskGroup extends TaskBase {

    private String name;
    private String time;
    private List<TaskParent> parents;

    public TaskGroup() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<TaskParent> getParents() {
        return parents;
    }

    public void setParents(List<TaskParent> parents) {
        this.parents = parents;
    }
    
    public void addParent(TaskParent parent) {
        parents.add(parent);
    }
}
