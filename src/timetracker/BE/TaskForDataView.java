/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BE;

import java.time.LocalDateTime;

/**
 *
 * @author @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class TaskForDataView extends TaskBase
{

    private Project project;
    private String user;

    /**
     * Constructor for TaskForDataView som tager imod parametre
     *
     * @param name
     * @param billable
     * @param projectId
     * @param personId
     * @param start
     * @param end
     * @param time
     * @param parent
     * @param project
     * @param userName
     */
    public TaskForDataView(String name, boolean billable, int projectId, int personId, LocalDateTime start, LocalDateTime end, String time, TaskParent parent, Project project, String userName)
    {
        super(name, billable, projectId, personId, start, end, time);
        this.project = project;
        this.user = userName;
    }

    /**
     * Constructor for TaskForDataView
     */
    public TaskForDataView()
    {
    }

    /**
     * Returnerer user
     *
     * @return
     */
    public String getUser()
    {
        return user;
    }

    /**
     * Sætter user
     *
     * @param user
     */
    public void setUser(String user)
    {
        this.user = user;
    }

    /**
     * Returnerer project
     *
     * @return
     */
    public Project getProject()
    {
        return project;
    }

    /**
     * Sætter project
     *
     * @param project
     */
    public void setProject(Project project)
    {
        this.project = project;
    }

}
