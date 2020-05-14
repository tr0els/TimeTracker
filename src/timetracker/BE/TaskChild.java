/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BE;

import java.time.LocalDateTime;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class TaskChild extends TaskBase {
    private TaskParent parent;

    public TaskChild() {}
    
    public TaskChild(int id, String name, boolean billable, int projectId, int personId, LocalDateTime start, LocalDateTime end, String time, TaskParent parent) {
        super(id, name, billable, projectId, personId, start, end, time);
        this.parent = parent;
    }

    public TaskParent getParent() {
        return parent;
    }

    public void setParent(TaskParent parent) {
        this.parent = parent;
    }
}
