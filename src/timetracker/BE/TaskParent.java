/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BE;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class TaskParent extends TaskBase {

    List<TaskChild> children;

    public TaskParent() {
    }

    public TaskParent(String name, boolean billable, int projectId, int personId, LocalDateTime start, LocalDateTime end, String time) {
        super(name, billable, projectId, personId, start, end, time);
    }

    public List<TaskChild> getChildren() {
        return children;
    }

    public void setChildren(List<TaskChild> children) {
        this.children = children;
    }

    public void addChild(TaskChild child) {
        this.children.add(child);
    }
}
