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
abstract public class TaskBase {

    private int id;
    private String name;
    private boolean billable;
    private int projectId;
    private int personId;
    private LocalDateTime start;
    private LocalDateTime end;
    private String time; // maybe SimpleDateFormat("HH:mm:ss");

    public TaskBase() {
    }

    public TaskBase(String name, boolean billable, int projectId, int personId, LocalDateTime start, LocalDateTime end, String time) {
        this.name = name;
        this.billable = billable;
        this.projectId = projectId;
        this.personId = personId;
        this.start = start;
        this.end = end;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBillable() {
        return billable;
    }

    public void setBillable(boolean billable) {
        this.billable = billable;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
