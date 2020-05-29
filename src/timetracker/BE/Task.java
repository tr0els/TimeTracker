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
public class Task
{

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
    private String projectName;

    /**
     * Constructor for Task som tager imod parametre
     *
     * @param taskId
     * @param taskName
     * @param projectId
     * @param personId
     * @param totalTime
     * @param startTime
     * @param endTime
     * @param billable
     * @param lastWorkedOn
     */
    public Task(int taskId, String taskName, int projectId, int personId, String totalTime, LocalDateTime startTime, LocalDateTime endTime, boolean billable, LocalDateTime lastWorkedOn)
    {
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

    /**
     * Constructor for Project
     */
    public Task()
    {

    }

    /**
     * Returnerer stringBillable
     *
     * @return
     */
    public String getStringBillable()
    {
        return stringBillable;
    }

    /**
     * Sætter stringBillable
     *
     * @param stringBillable
     */
    public void setStringBillable(String stringBillable)
    {
        this.stringBillable = stringBillable;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * Returnerer startTime
     *
     * @return
     */
    public LocalDateTime getStartTime()
    {
        return startTime;
    }

    /**
     * Sætter startTime
     *
     * @param startTime
     */
    public void setStartTime(LocalDateTime startTime)
    {
        this.startTime = startTime;
    }

    /**
     * Returnerer endTime
     *
     * @return
     */
    public LocalDateTime getEndTime()
    {
        return endTime;
    }

    /**
     * Sætter endTime
     *
     * @param endTime
     */
    public void setEndTime(LocalDateTime endTime)
    {
        this.endTime = endTime;
    }

    /**
     * Returnerer billable
     *
     * @return
     */
    public boolean isBillable()
    {
        return billable;
    }

    /**
     * Sætter billable
     *
     * @param billable
     */
    public void setBillable(boolean billable)
    {
        this.billable = billable;
    }

    /**
     * Returnerer lastWorkedOn
     *
     * @return
     */
    public LocalDateTime getLastWorkedOn()
    {
        return lastWorkedOn;
    }

    /**
     * Sætter lastWorkedOn
     *
     * @param lastWorkedOn
     */
    public void setLastWorkedOn(LocalDateTime lastWorkedOn)
    {
        this.lastWorkedOn = lastWorkedOn;
    }

    /**
     * Returnerer totalTime
     *
     * @return
     */
    public String getTotalTime()
    {
        return totalTime;
    }

    /**
     * Sætter totalTime
     *
     * @param totalTime
     */
    public void setTotalTime(String totalTime)
    {
        this.totalTime = totalTime;
    }

    /**
     * Returnerer taskId
     *
     * @return
     */
    public int getTaskId()
    {
        return taskId;
    }

    /**
     * Sætter taskId
     *
     * @param taskId
     */
    public void setTaskId(int taskId)
    {
        this.taskId = taskId;
    }

    /**
     * Returnerer taskName
     *
     * @return
     */
    public String getTaskName()
    {
        return taskName;
    }

    /**
     * Sætter taskName
     *
     * @param taskName
     */
    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
    }

    /**
     * Returnerer projectId
     *
     * @return
     */
    public int getProjectId()
    {
        return projectId;
    }

    /**
     * Sætter projectId
     *
     * @param projectId
     */
    public void setProjectId(int projectId)
    {
        this.projectId = projectId;
    }

    /**
     * Returnerer personId
     *
     * @return
     */
    public int getPersonId()
    {
        return personId;
    }

    /**
     * Sætter personId
     *
     * @param personId
     */
    public void setPersonId(int personId)
    {
        this.personId = personId;
    }

    /**
     * ToString metode så klassen viser det rigtige
     *
     * @return
     */
    @Override
    public String toString()
    {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String last;
        if (lastWorkedOn == null)
        {
            last = "Ikke afsluttet!";
        } else
        {
            last = lastWorkedOn.format(format);
        }
        return taskId + ": " + taskName + " | Total tid brugt: " + totalTime + " | Sidst arbejdet på: " + last;
    }
}
