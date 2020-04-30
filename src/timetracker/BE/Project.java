/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BE;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class Project
{
    private int project_id; 
    private String project_name; 
    private int project_rate;
    private int client_id;

    public Project(int project_id, String project_name, int project_rate, int client_id)
    {
        this.project_id = project_id;
        this.project_name = project_name;
        this.project_rate = project_rate;
        this.client_id = client_id;
    }

    public Project()
    {
    }

    public int getProject_id()
    {
        return project_id;
    }

    public void setProject_id(int project_id)
    {
        this.project_id = project_id;
    }

    public String getProject_name()
    {
        return project_name;
    }

    public void setProject_name(String project_name)
    {
        this.project_name = project_name;
    }

    public int getProject_rate()
    {
        return project_rate;
    }

    public void setProject_rate(int project_rate)
    {
        this.project_rate = project_rate;
    }

    public int getClient_id()
    {
        return client_id;
    }

    public void setClient_id(int client_id)
    {
        this.client_id = client_id;
    }

    @Override
    public String toString() {
        return project_name;
    }
    
    
}
