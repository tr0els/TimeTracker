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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import timetracker.BE.Task;
import timetracker.BE.Task.Log;
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
        try (Connection con = dbCon.getConnection()) {

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
        try (Connection con = dbCon.getConnection()) {

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
    public HashMap<Task, List<Task>> getTaskbyIDs(int project_id, int person_id) throws DALException {

        HashMap<Task, List<Task>> map = new HashMap<>();

        String typeTask = "TASK";
        String typeLog = "LOG";

        try (Connection con = dbCon.getConnection()) {

            String sql = "SELECT 'TASK' AS type, \n"
                    + "	MIN(t.task_id) as task_id,\n"
                    + "	t.project_id, t.task_name,\n"
                    + "	MIN(t.task_start) as task_start,\n"
                    + "	MAX(t.task_end) as task_end,\n"
                    + "	CONVERT(VARCHAR(5),SUM(DATEDIFF(SECOND,t.task_start,t.task_end))/60/60) + ':' +\n"
                    + "	RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,t.task_start,t.task_end))/60%60), 2) + ':' +\n"
                    + "	RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,t.task_start,t.task_end))%60),2)\n"
                    + "	AS total_time,\n"
                    + "	'0' as billable, CONCAT(project_id, '-', task_name) as group_key\n"
                    + "FROM Tasklog t\n"
                    + "WHERE task_end IS NOT NULL and person_id = ? and project_id = ?\n"
                    + "GROUP BY t.project_id, t.task_name\n"
                    + "\n"
                    + "UNION\n"
                    + "\n"
                    + "SELECT 'LOG' AS type, \n"
                    + "	t.task_id,\n"
                    + "	t.project_id, \n"
                    + "	t.task_name,\n"
                    + "	MIN(t.task_start) as task_start,\n"
                    + "	MAX(t.task_end) as task_end,\n"
                    + "		CONVERT(VARCHAR(5),SUM(DATEDIFF(SECOND,t.task_start,t.task_end))/60/60) + ':' +\n"
                    + "	RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,t.task_start,t.task_end))/60%60), 2) + ':' +\n"
                    + "	RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,t.task_start,t.task_end))%60),2)\n"
                    + "	AS total_time,\n"
                    + "	t.billable, CONCAT(project_id, '-', task_name) as group_key\n"
                    + "FROM Tasklog t\n"
                    + "WHERE task_end IS NOT NULL and person_id = ? and project_id = ?\n"
                    + "GROUP BY t.project_id, t.task_name, t.billable, t.task_id\n"
                    + "ORDER BY group_key, type ASC, task_start DESC;";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, person_id);
            ps.setInt(2, project_id);
            ps.setInt(3, person_id);
            ps.setInt(4, project_id);

            ResultSet rs = ps.executeQuery();

            ArrayList<Task> logs = new ArrayList<>();

            while (rs.next()) {

                if (rs.getString("type").equalsIgnoreCase(typeLog)) {

                    Task log = new Task();

                    LocalDateTime end_time;
                    if (rs.getTimestamp("task_end") != null) {
                        end_time = rs.getTimestamp("task_end").toLocalDateTime();
                    } else {
                        end_time = null;
                    }

                    String sBillable;
                    if (rs.getBoolean("billable") == true) {
                        sBillable = "Ja";
                    } else {
                        sBillable = "Nej";
                    }

                    log.setTask_id(rs.getInt("task_id"));
                    log.setBillable(rs.getBoolean("billable"));
                    log.setTotal_tid(rs.getString("total_time"));
                    log.setStart_time(rs.getTimestamp("task_start").toLocalDateTime());
                    log.setEnd_time(end_time);
                    log.setStringBillable(sBillable);

                    logs.add(log);

                }

                if (rs.getString("type").equalsIgnoreCase(typeTask)) {

                    Task task = new Task();
                    int task_id = rs.getInt("task_id");

                    task.setTask_id(task_id);
                    task.setTask_name(rs.getString("task_name"));
                    task.setProject_id(rs.getInt("project_id"));
                    task.setTotal_tid(rs.getString("total_time"));
                    task.setLast_worked_on(rs.getTimestamp("task_end").toLocalDateTime());

                    map.put(task, logs);

                    logs = new ArrayList<>();

                }

            }

        } catch (SQLException e) {
            throw new DALException("Kunne ikke finde Task på bruger og projekt");
        }

        return map;
    }

    /**
     * returnere en specifik task udfra task_id
     * @param task_id
     * @return
     * @throws DALException 
     */
    public Task getTaskbyID(int task_id) throws DALException {
        Task task = new Task();

        try (Connection con = dbCon.getConnection()) {

            String sql = "SELECT * FROM Tasklog WHERE task_id = " + task_id + ";";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {

                LocalDateTime end_time;
                if (rs.getTimestamp("task_end") != null) {
                    end_time = rs.getTimestamp("task_end").toLocalDateTime();
                } else {
                    end_time = null;
                }

                task.setTask_id(rs.getInt("task_id"));
                task.setTask_name(rs.getString("task_name"));
                task.setProject_id(rs.getInt("project_id"));
                task.setPerson_id(rs.getInt("person_id"));
                task.setStart_time(rs.getTimestamp("task_start").toLocalDateTime());
                task.setEnd_time(end_time);
                task.setBillable(rs.getBoolean("billable"));

            }

        } catch (SQLException e) {
            throw new DALException("Kunne ikke finde tasken");
        }
        return task;

    }

    /**
     * Modtager en Task og opdatere den i DB.
     * @param task
     * @throws DALException 
     */
    public void updateTaskbyID(Task task) throws DALException {

        try (Connection con = dbCon.getConnection()) {

            int task_id = task.getTask_id();

            String sql = "UPDATE Tasklog SET task_name = ?, billable = ?, project_id = ?, person_id = ?, task_start = ?, task_end = ? WHERE task_id = ?;";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, task.getTask_name());
            ps.setBoolean(2, task.isBillable());
            ps.setInt(3, task.getProject_id());
            ps.setInt(4, task.getPerson_id());
            ps.setTimestamp(5, java.sql.Timestamp.valueOf(task.getStart_time()));
            ps.setTimestamp(6, java.sql.Timestamp.valueOf(task.getEnd_time()));
            ps.setInt(7, task_id);

            ps.execute();

        } catch (SQLException e) {
            throw new DALException("Kunne ikke opdatere tasken");
        }

    }
}
