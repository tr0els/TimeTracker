/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BE;

import java.time.LocalDateTime;

/**
 *
 * @author Charlotte
 */
public class TaskForDataView extends TaskBase{
    
   
    private Project project;
    private String medarbejder;
    
    public TaskForDataView(){
    }
    
    public TaskForDataView( String name, boolean billable, int projectId, int personId, LocalDateTime start, LocalDateTime end, String time, TaskParent parent, Project project, String medarebdjerNavn) {
        super(name, billable, projectId, personId,  start,  end,  time);
        this.project = project;
        this.medarbejder = medarebdjerNavn;
      
    }

    public String getMedarbejder() {
        return medarbejder;
    }

    public void setMedarbejder(String medarbejder) {
        this.medarbejder = medarbejder;
    }

    

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    
    
    
}
