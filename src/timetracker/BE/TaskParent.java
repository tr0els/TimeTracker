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
public class TaskParent extends TaskBase
{

    List<TaskChild> children;

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
    public TaskParent(String name, boolean billable, int projectId, int personId, LocalDateTime start, LocalDateTime end, String time)
    {
        super(name, billable, projectId, personId, start, end, time);
    }

    /**
     * Constructor for Client
     */
    public TaskParent()
    {
    }

    /**
     * Returnerer children
     *
     * @return
     */
    public List<TaskChild> getChildren()
    {
        return children;
    }

    /**
     * Sætter children
     *
     * @param children
     */
    public void setChildren(List<TaskChild> children)
    {
        this.children = children;
    }

    /**
     * Tilføjer child
     *
     * @param child
     */
    public void addChild(TaskChild child)
    {
        this.children.add(child);
    }
}
