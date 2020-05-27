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

    private int id;
    private TaskParent parent;

    public TaskChild() {
    }

    public TaskChild(int id, String name, boolean billable, int projectId, LocalDateTime start, LocalDateTime end, String time, TaskParent parent) {
        super(name, billable, projectId, start, end, time);
        this.id = id;
        this.parent = parent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskParent getParent() {
        return parent;
    }

    public void setParent(TaskParent parent) {
        this.parent = parent;
    }
}
