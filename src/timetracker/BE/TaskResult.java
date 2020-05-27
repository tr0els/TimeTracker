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
public class TaskResult
{

    private List<? extends TaskBase> tasks;

    /**
     * Constructor for Client
     */
    public TaskResult()
    {
    }

    /**
     * Returnerer tasks
     *
     * @return
     */
    public List<? extends TaskBase> getTasks()
    {
        return tasks;
    }

    /**
     * Sætter tasks
     *
     * @param tasks
     */
    public void setTasks(List<? extends TaskBase> tasks)
    {
        this.tasks = tasks;
    }
}
