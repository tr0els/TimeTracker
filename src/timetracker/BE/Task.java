/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BE;

import java.sql.Time;
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

    public Task() {

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
        Time total_tid;
        int task_id;

        public Log(LocalDateTime start_time, LocalDateTime end_time, int task_id, Time total_tid) {
            this.start_time = start_time;
            this.end_time = end_time;
            this.task_id = task_id;
            this.total_tid = total_tid;
        }

        public Log() {

        }

        public Time getTotal_tid() {
            return total_tid;
        }

        public void setTotal_tid(Time total_tid) {
            this.total_tid = total_tid;
        }

        public int getTask_id() {
            return task_id;
        }

        public void setTask_id(int task_id) {
            this.task_id = task_id;
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
            return "Log{" + "Task_id" + task_id + "start_time=" + start_time + ", end_time=" + end_time + '}';
        }

    }
}
