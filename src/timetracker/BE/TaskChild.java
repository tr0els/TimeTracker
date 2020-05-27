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
public class TaskChild extends TaskBase
{

    private int id;
    private TaskParent parent;

    /**
     * Constructor for TaskChild som tager imod parametre
     *
     * @param id
     * @param name
     * @param billable
     * @param projectId
     * @param start
     * @param end
     * @param time
     * @param parent
     */
    public TaskChild(int id, String name, boolean billable, int projectId, LocalDateTime start, LocalDateTime end, String time, TaskParent parent)
    {
        super(name, billable, projectId, start, end, time);
        this.id = id;
        this.parent = parent;
    }

    /**
     * Constructor for TaskChild
     */
    public TaskChild()
    {
    }

    /**
     * Returnerer id
     *
     * @return
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sætter id
     *
     * @param id
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Returnerer parent
     *
     * @return
     */
    public TaskParent getParent()
    {
        return parent;
    }

    /**
     * Sætter parent
     *
     * @param parent
     */
    public void setParent(TaskParent parent)
    {
        this.parent = parent;
    }
}
