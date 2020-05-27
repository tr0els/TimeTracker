/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BE;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class Project extends RecursiveTreeObject<Project>
{

    private int projectId;
    public String projectName;
    private int projectRate;
    private int clientId;
    private String clientName;
    private String totalTime;
    private String billableTime;

    /**
     * Constructor for Project som tager imod parametre
     *
     * @param projectId
     * @param projectName
     * @param projectRate
     * @param clientId
     * @param total_tid
     */
    public Project(int projectId, String projectName, int projectRate, int clientId, String totalTime)
    {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectRate = projectRate;
        this.clientId = clientId;
        this.clientName = clientName;
        this.totalTime = totalTime;

    }

    /**
     * Constructor for Project
     */
    public Project()
    {
    }

    /**
     * Returnerer billableTime
     *
     * @return
     */
    public String getBillableTime()
    {
        return billableTime;
    }

    /**
     * Sætter billableTime
     *
     * @param billableTime
     */
    public void setBillableTime(String billableTime)
    {
        this.billableTime = billableTime;
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
     * Returnerer projectName
     *
     * @return
     */
    public String getProjectName()
    {
        return projectName;
    }

    /**
     * Sætter projectName
     *
     * @param projectName
     */
    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    /**
     * Returnerer projectRate
     *
     * @return
     */
    public int getProjectRate()
    {
        return projectRate;
    }

    /**
     * Sætter projectRate
     *
     * @param projectRate
     */
    public void setProjectRate(int projectRate)
    {
        this.projectRate = projectRate;
    }

    /**
     * Returnerer clientId
     *
     * @return
     */
    public int getClientId()
    {
        return clientId;
    }

    /**
     * Sætter clientId
     *
     * @param clientId
     */
    public void setClientId(int clientId)
    {
        this.clientId = clientId;
    }

    /**
     * Returnerer client_name
     *
     * @return
     */
    public String getClientName()
    {
        return clientName;
    }

    /**
     * Sætter client_name
     *
     * @param clientName
     */
    public void setClientName(String clientName)
    {
        this.clientName = clientName;
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
     * ToString metode så klassen viser det rigtige
     *
     * @return
     */
    @Override
    public String toString()
    {
        return projectName;
    }

}
