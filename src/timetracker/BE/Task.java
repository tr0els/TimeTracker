/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BE;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class Task {

    private int task_id;
    private String task_name;
    private int project_id;
    private int person_id;
    private String total_tid;

    private LocalDateTime last_worked_on;

    public Task(int task_id, String task_name, int project_id, int person_id, String total_tid, LocalDateTime last_worked_on) {

        this.task_id = task_id;
        this.task_name = task_name;
        this.project_id = project_id;
        this.person_id = person_id;
        this.total_tid = total_tid;
        this.last_worked_on = last_worked_on;

    }

    public Task() {

    }

    public LocalDateTime getLast_worked_on() {
        return last_worked_on;
    }

    public void setLast_worked_on(LocalDateTime last_worked_on) {
        this.last_worked_on = last_worked_on;
    }

    public String getTotal_tid() {
        return total_tid;
    }

    public void setTotal_tid(String total_tid) {
        this.total_tid = total_tid;
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

    @Override
    public String toString() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String last = last_worked_on.format(format);
        return task_name + " | Total tid brugt: " + total_tid + " | Sidst arbejdet på: " + last;
    }

    /**
     * nested class med Log data
     */
    public static class Log {

        private LocalDateTime start_time;
        private LocalDateTime end_time;
        private String total_tid;
        private int task_id;
        private int log_id;
        private boolean billable;

        public Log(LocalDateTime start_time, LocalDateTime end_time, int task_id, boolean billable, String total_tid, int log_id) {
            this.start_time = start_time;
            this.end_time = end_time;
            this.task_id = task_id;
            this.total_tid = total_tid;
            this.task_id = task_id;
            this.billable = billable;
            this.log_id = log_id;
        }

        public Log() {

        }

        public int getLog_id() {
            return log_id;
        }

        public void setLog_id(int log_id) {
            this.log_id = log_id;
        }

        public boolean isBillable() {
            return billable;
        }

        public void setBillable(boolean billable) {
            this.billable = billable;
        }

        public String getTotal_tid() {
            return total_tid;
        }

        public void setTotal_tid(String total_tid) {
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
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            String start = start_time.format(format);

            String slut = "ikke afsluttet";
            if (end_time != null) {
                slut = end_time.format(format);
            }
            String total = "-";
            if (total_tid != null) {
                total = total_tid + "";
            }
            return "" + total + " | [" + start + " - " + slut + "]" + "billable: " + billable;
        }

    }
}
