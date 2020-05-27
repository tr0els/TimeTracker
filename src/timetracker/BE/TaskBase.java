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
abstract public class TaskBase
{

    private int id;
    private String name;
    private boolean billable;
    private int projectId;
    private int personId;
    private LocalDateTime start;
    private LocalDateTime end;
    private String time; // maybe SimpleDateFormat("HH:mm:ss");

    /**
     * Constructor for Client som tager imod parametre
     *
     * @param name
     * @param billable
     * @param projectId
     * @param personId
     * @param start
     * @param end
     * @param time
     */
    public TaskBase(String name, boolean billable, int projectId, int personId, LocalDateTime start, LocalDateTime end, String time)
    {
        this.name = name;
        this.billable = billable;
        this.projectId = projectId;
        this.personId = personId;
        this.start = start;
        this.end = end;
        this.time = time;
    }

    /**
     * Constructor for Client
     */
    public TaskBase()
    {
    }

    /**
     * Returnerer id
     * @return 
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sætter id
     * @param id 
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Returnerer name
     * @return 
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sætter name
     * @param name 
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returnerer billable
     * @return 
     */
    public boolean isBillable()
    {
        return billable;
    }

    /**
     * Sætter billable
     * @param billable 
     */
    public void setBillable(boolean billable)
    {
        this.billable = billable;
    }

    /**
     * Returnerer projectId
     * @return 
     */
    public int getProjectId()
    {
        return projectId;
    }

    /**
     * Sætter projectId
     * @param projectId 
     */
    public void setProjectId(int projectId)
    {
        this.projectId = projectId;
    }

    /**
     * Returnerer personId
     * @return 
     */
    public int getPersonId()
    {
        return personId;
    }

    /**
     * Sætter personId
     * @param personId 
     */
    public void setPersonId(int personId)
    {
        this.personId = personId;
    }

    /**
     * Returnerer start
     * @return 
     */
    public LocalDateTime getStart()
    {
        return start;
    }

    /**
     * Sætter start
     * @param start 
     */
    public void setStart(LocalDateTime start)
    {
        this.start = start;
    }

    /**
     * Returnerer end
     * @return 
     */
    public LocalDateTime getEnd()
    {
        return end;
    }

    /**
     * Sætter end
     * @param end 
     */
    public void setEnd(LocalDateTime end)
    {
        this.end = end;
    }

    /**
     * Returnerer time
     * @return 
     */
    public String getTime()
    {
        return time;
    }

    /**
     * Sætter time
     * 
     * @param time 
     */
    public void setTime(String time)
    {
        this.time = time;
    }
}
