/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BE;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class Task {

    private int taskId;
    private String taskName;
    private int projectId;
    private int personId;
    private String totalTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean billable;
    private String stringBillable;
    private LocalDateTime lastWorkedOn;
    
    
    public Task(int taskId, String taskName, int projectId, int personId, String totalTime, String task_tid, LocalDateTime startTime, LocalDateTime endTime, boolean billable, LocalDateTime lastWorkedOn) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.projectId = projectId;
        this.personId = personId;
        this.totalTime = totalTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.billable = billable;
        this.lastWorkedOn = lastWorkedOn;
    }

    public Task() {

    }

    public String getStringBillable() {
        return stringBillable;
    }

    public void setStringBillable(String stringBillable) {
        this.stringBillable = stringBillable;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isBillable() {
        return billable;
    }

    public void setBillable(boolean billable) {
        this.billable = billable;
    }

    public LocalDateTime getLastWorkedOn() {
        return lastWorkedOn;
    }

    public void setLastWorkedOn(LocalDateTime lastWorkedOn) {
        this.lastWorkedOn = lastWorkedOn;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
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

    @Override
    public String toString() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String last;
        if (lastWorkedOn == null)
        {
            last = "Ikke afsluttet!";
        }else{
        last = lastWorkedOn.format(format);
        }
        return taskId + ": " + taskName + " | Total tid brugt: " + totalTime + " | Sidst arbejdet på: " + last;
    }
}
