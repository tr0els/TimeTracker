/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BE;

import java.time.LocalDateTime;

/**
 *
 * @author BBran
 */
public class TaskLog extends Task {
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    
        public TaskLog(int task_id, String task_name, boolean billable, int project_id, int person_id, LocalDateTime start_time, LocalDateTime end_time)
    {
        super(task_id, task_name, billable, project_id, person_id);
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public LocalDateTime getStart_time() {
        return start_time;
    }

    public void setStart_time(LocalDateTime start_time) {
        this.start_time = start_time;
    }

    public LocalDateTime getEnd_time() {
        return end_time;
    }

    public void setEnd_time(LocalDateTime end_time) {
        this.end_time = end_time;
    }
        
        
}
