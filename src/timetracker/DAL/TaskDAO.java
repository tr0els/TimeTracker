/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import timetracker.BE.Task;
import timetracker.DAL.DALException;

/**
 *
 * @author Charlotte
 */
public class TaskDAO {
    
    private DatabaseConnector dbCon;
    
     public TaskDAO() throws DALException {
        dbCon = new DatabaseConnector();
    }
    
    
     /**
     * Starter en ny task
     *
     * @param task_name
     * @param billable
     * @param project_id
     * @param person_id
     * @throws timetracker.DAL.DALException
     */
    public void startTask(String task_name, boolean billable, int project_id, int person_id) throws DALException {
        try ( Connection con = dbCon.getConnection()) {

            String sql = "INSERT INTO Tasklog (task_name, billable, project_id, person_id, task_start) VALUES (?,?,?,?,CURRENT_TIMESTAMP)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, task_name);
            ps.setBoolean(2, billable);
            ps.setInt(3, project_id);
            ps.setInt(4, person_id);

            ps.execute();
        } catch (SQLException e) {
            throw new DALException("Kunne ikke starte Tasken");
        }
    }
    
    /**
     * Stopper en igangværende task
     *
     * @param person_id
     * @throws timetracker.DAL.DALException
     */
    public void stopTask(int person_id) throws DALException {
        try ( Connection con = dbCon.getConnection()) {

            String sql = "UPDATE Tasklog SET task_end = CURRENT_TIMESTAMP\n"
                    + "WHERE person_id = ? AND task_end IS NULL";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, person_id);

            ps.execute();

        } catch (SQLException e) {
            throw new DALException("Kunne ikke stoppe tasken");
        }
    }
    

    
    
        /**
     * Returnerer en liste af et projekts tilhørende Tasks udfra person_id og
     * project_id
     *
     * @param project_id
     * @param person_id
     * @return
     */
    public List<Task> getTaskbyIDs(int project_id, int person_id) throws DALException {
        ArrayList<Task> taskbyID = new ArrayList<>();

        try ( Connection con = dbCon.getConnection()) {

            String sql = "SELECT t.task_id, t.task_name, t.project_id, t.person_id, MAX(tl.task_end) as last_worked_on, \n"
                    + "CONVERT(VARCHAR(5),SUM(DATEDIFF(SECOND,tl.task_start,tl.task_end))/60/60) + ':' +\n"
                    + "RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,tl.task_start,tl.task_end))/60%60), 2) + ':' +\n"
                    + "RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,tl.task_start,tl.task_end))%60),2)\n"
                    + "AS total_time FROM Task t\n"
                    + "INNER JOIN Task_log tl ON t.task_id = tl.task_id\n"
                    + "WHERE t.person_id = ? AND t.project_id = ?\n"
                    + "Group BY t.task_id, t.task_name, t.project_id, t.person_id;";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, person_id);
            ps.setInt(2, project_id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Task task = new Task();
                int task_id = rs.getInt("task_id");
                
                task.setTask_id(task_id);
                task.setTask_name(rs.getString("task_name"));
                task.setProject_id(rs.getInt("project_id"));
                task.setPerson_id(rs.getInt("person_id"));
                task.setTotal_tid(rs.getString("total_time"));
                task.setLast_worked_on(rs.getTimestamp("last_worked_on").toLocalDateTime());
               
                taskbyID.add(task);
            }

        } catch (SQLException e) {
            throw new DALException("Kunne ikke finde Task på bruger og projekt");
        }

        return taskbyID;
    }

    public List<Task.Log> getLogsbyID(int task_id) throws DALException {
        ArrayList<Task.Log> logsbyID = new ArrayList<>();
        try ( Connection con = dbCon.getConnection()) {

            String sql = "SELECT *,\n"
                    + "CONVERT(VARCHAR(5),DATEDIFF(SECOND,tl.task_start,tl.task_end)/60/60) + ':' +\n"
                    + "RIGHT('0' + CONVERT(VARCHAR(2),DATEDIFF(SECOND,tl.task_start,tl.task_end)/60%60), 2) + ':' +\n"
                    + "RIGHT('0' + CONVERT(VARCHAR(2),DATEDIFF(SECOND,tl.task_start,tl.task_end)%60),2)\n"
                    + "AS total_time\n"
                    + "FROM Task_log tl\n"
                    + "WHERE tl.task_id = ?\n"
                    + "ORDER BY task_start DESC";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, task_id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Task.Log log = new Task.Log();

                LocalDateTime end_time;
                if (rs.getTimestamp("task_end") != null) {
                    end_time = rs.getTimestamp("task_end").toLocalDateTime();
                } else {
                    end_time = null;
                }

                log.setLog_id(rs.getInt("log_id"));
                log.setTask_id(rs.getInt("task_id"));
                log.setBillable(rs.getBoolean("billable"));
                log.setTotal_tid(rs.getString("total_time"));
                log.setStart_time(rs.getTimestamp("task_start").toLocalDateTime());
                log.setEnd_time(end_time);

                logsbyID.add(log);
            }

        } catch (SQLException e) {
        throw new DALException("kunne ikke  finde logsne til din task");
        }

        return logsbyID;
   }
    
    
        
//    /**
//     * Returnerer Task udfra task_id
//     *
//     * @param task_id
//     * @return
//     */
//    public Task getTaskbyTaskID(int task_id) throws DALException {
//        Task task = new Task();
//
//        try ( Connection con = dbCon.getConnection()) {
//
//            String sql = "SELECT * FROM Tasklog WHERE task_id = " + task_id + ";";
//            Statement statement = con.createStatement();
//            ResultSet rs = statement.executeQuery(sql);
//            while (rs.next()) {
//                task.setTask_id(rs.getInt("task_id"));
//                task.setTask_name(rs.getString("task_name"));
//                task.setBillable(rs.getBoolean("billable"));
//                task.setPerson_id(rs.getInt("person_id"));
//                task.setProject_id(rs.getInt("project_id"));
//            }
//
//        } catch (SQLException e) {
//            throw new DALException("Kunne ikke finde tasken");
//        }
//        return task;
//    }
    
//    /**
//     * Returnerer en liste af et projekts tilhørende Tasks
//     *
//     * @param project_id
//     * @return
//     * @throws timetracker.DAL.DALException
//     */
//    public List<Task> getTaskbyProjectID(int project_id) throws DALException {
//        ArrayList<Task> taskbyID = new ArrayList<>();
//
//        try ( Connection con = dbCon.getConnection()) {
//
//            String sql = "SELECT * FROM Tasklog WHERE project_id = " + project_id + ";";
//            Statement statement = con.createStatement();
//            ResultSet rs = statement.executeQuery(sql);
//
//            while (rs.next()) {
//                Task task = new Task();
//
//                task.setTask_id(rs.getInt("task_id"));
//                task.setTask_name(rs.getString("task_name"));
//                task.setBillable(rs.getBoolean("billable"));
//                task.setProject_id(rs.getInt("project_id"));
//                task.setPerson_id(rs.getInt("person_id"));
//                task.setBillable(rs.getBoolean("billable"));
//
//                taskbyID.add(task);
//            }
//            return taskbyID;
//
//        } catch (SQLException e) {
//            throw new DALException("Kunne ikke Projektets tilhørende tasks");
//        }
//        //return null;
//    }
    
    
//        public List<Task.Log> getTaskLogListbyTaskID(int task_id) throws DALException {
//        ArrayList<Task.Log> tasklogbyID = new ArrayList<>();
//
//        try ( Connection con = dbCon.getConnection()) {
//
//            String sql = "SELECT *, CAST(task_end - task_start AS TIME(0)) AS total_time FROM Tasklog WHERE task_id = ?;";
//            PreparedStatement ps = con.prepareStatement(sql);
//
//            ps.setInt(1, task_id);
//
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                Task.Log log = new Task.Log();
//
//                LocalDateTime end_time;
//                if (rs.getTimestamp("task_end") != null) {
//                    end_time = rs.getTimestamp("task_end").toLocalDateTime();
//                } else {
//                    end_time = null;
//                }
//
//                log.setTotal_tid(rs.getTime("total_time"));
//                log.setStart_time(rs.getTimestamp("task_start").toLocalDateTime());
//                log.setEnd_time(end_time);
//
//                tasklogbyID.add(log);
//            }
//
//        } catch (SQLException e) {
//            throw new DALException("kunne ikke hente logs for tasken");
//        }
//
//        return tasklogbyID;
//    }

    /**
     * Returnerer en liste af tasks ud fra person id og dag (0 = idag, 1 = igår
     * osv.)
     *
     * @param person_id
     * @param dag
     * @return
     */
//    public List<Task.Log> getTaskLogListbyDay(int person_id, int dag) throws DALException {
//        ArrayList<Task.Log> tasklogbyDay = new ArrayList<>();
//
//        try ( Connection con = dbCon.getConnection()) {
//
//            String sql = "SELECT Tasklog.*, Project.project_name, CAST(task_end - task_start AS TIME(0)) AS total_time FROM Tasklog\n"
//                    + "JOIN Project ON Project.project_id = Tasklog.project_id\n"
//                    + "WHERE CAST(task_start AS DATE) = DATEADD(day, -" + dag + ", CONVERT(date, GETDATE()))\n"
//                    + "AND person_id = ? ORDER BY task_start DESC";
//            PreparedStatement ps = con.prepareStatement(sql);
//
//            ps.setInt(1, person_id);
//
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//
//                Task.Log log = new Task.Log();
//
//                LocalDateTime end_time;
//                Time total_time;
//
//                if (rs.getTimestamp("task_end") != null) {
//                    end_time = rs.getTimestamp("task_end").toLocalDateTime();
//                    total_time = rs.getTime("total_time");
//                } else {
//                    end_time = null;
//                    total_time = null;
//                }
//
//                log.setBillable(rs.getBoolean("billable"));
//                log.setProject_name(rs.getString("project_name"));
//                log.setTask_name(rs.getString("task_name"));
//                log.setTotal_tid(total_time);
//                log.setStart_time(rs.getTimestamp("task_start").toLocalDateTime());
//                log.setEnd_time(end_time);
//                log.setTask_id(rs.getInt("task_id"));
//
//                tasklogbyDay.add(log);
//            }
//
//        } catch (SQLException e) {
//            throw new DALException("Kunne ikke hente tasklogs for dagen");
//        }
//
//        return tasklogbyDay;
//    }
    
}
