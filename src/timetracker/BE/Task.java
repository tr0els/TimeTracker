/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BE;

import java.time.LocalDateTime;
import javafx.collections.ObservableList;
import timetracker.DAL.DALException;
import timetracker.DAL.DALManager;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class Task {


    private int task_id;
    private String task_name;
    private boolean billable;
    private int project_id;
    private int person_id;
  
    public Task(int task_id, String task_name, boolean billable, int project_id, int person_id) {
        
        this.task_id = task_id;
        this.task_name = task_name;
        this.billable = billable;
        this.project_id = project_id;
        this.person_id = person_id;

    }

    public Task(){

    }

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public boolean isBillable() {
        return billable;
    }

    public void setBillable(boolean billable) {
        this.billable = billable;
    }

    public int getProject_id() {
        return project_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }

    public int getPerson_id() {
        return person_id;
    }

    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }

    /**
     * nested class med Log data
     */
    public static class Log {

        LocalDateTime start_time;
        LocalDateTime end_time;

        public Log(LocalDateTime start_time, LocalDateTime end_time) {
            this.start_time = start_time;
            this.end_time = end_time;
        }

        public Log() {

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

        @Override
        public String toString() {
            return "Log{" + "start_time=" + start_time + ", end_time=" + end_time + '}';
        }

    }
}
