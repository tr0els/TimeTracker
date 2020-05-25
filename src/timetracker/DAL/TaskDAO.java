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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import timetracker.BE.Project;
import timetracker.BE.Task;
import timetracker.BE.TaskBase;
import timetracker.BE.TaskChild;
import timetracker.BE.TaskGroup;
import timetracker.BE.TaskParent;
import timetracker.BE.TaskResult;
import timetracker.BE.Task.Log;
import timetracker.BE.TaskForDataView;
import timetracker.BE.User;
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
                    log.setTask_name(rs.getString("task_name"));

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



   public  List<TaskForDataView> getListOfTaskForDataView(Project project, User user) throws DALException {
       List<TaskForDataView> taskForOverviewData = new ArrayList<>();
       
  
       
       try (Connection con = dbCon.getConnection()){
       
       String project_id = "";
       String user_id =  "";
       
       
       if(project != null )
       {
           project_id += "and p.project_id =" + project.getProject_id()+ "\n";
       }
       
       if(user != null)
       {
       user_id += "and pr.person_id = " +user.getPerson_id() +"\n";
       }
       
           
       String sql = 
            "select tl.task_name, tl.task_start, tl.task_end, tl.billable, concat(pr.name, + ' ' + pr.surname) as name \n"
          + "from tasklog tl , Project p, Person pr \n"  
          + "where tl.project_id = p.project_id  \n"
          + "and pr.person_id = tl.person_id \n"
          + project_id 
          + user_id
          + "order by tl.task_start asc;";
         // + "and p.project_id = 9 \n"
          //+ "and pr.person_id = 1 " ;

        Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
             LocalDateTime end_time;
                if (rs.getTimestamp("task_end") != null) {
                    end_time = rs.getTimestamp("task_end").toLocalDateTime();
            } else {
               end_time = LocalDateTime.now();
            }
    
            String Billable; 
            TaskForDataView task = new TaskForDataView();
            task.setName(rs.getString("task_name"));
            task.setStart(rs.getTimestamp("task_start").toLocalDateTime());
            task.setEnd(end_time);
          
            task.setBillable(rs.getBoolean("billable"));
            task.setMedarbejder(rs.getString("name"));
            
            taskForOverviewData.add(task);
                
                
             
            }
         
       
      
       } catch (SQLException ex) {
          throw new DALException("kunne ikke hente din liste af task" + ex);
        }
       
          return taskForOverviewData;
    }
    
    
    
    
    
    
    
    
    
    
    

    

    
    /**
     * Gets all tasks for a person grouped by date,
     * including time spent on tasks per date, parent task, and child task.
     * 
     * @return todo
     * @throws DALException
     * @throws SQLException 
     */
    private List<? extends TaskBase> getTasks(int person_id, String groupBy, boolean includeTaskParents, boolean includeTaskChildren) throws DALException, SQLException {

        // Fetch task data 
        try (Connection con = dbCon.getConnection()) {
            String sql =
                    "-- LOCAL VARIABLES\n"
                    + "DECLARE @person_id int = 0\n"
                    + "\n"
                    + "SELECT *, MIN(task_start) OVER (PARTITION BY task_date, task_name, project_id, billable ORDER BY task_start) AS group_start FROM \n"
                    + "(\n"
                    + "-- PARENT TASKS (CALCULATED TASKS BASED ON GROUPING OF ACTUAL TASK DATA)\n"
                    + "SELECT \n"
                    + "	'TaskParent' AS type,\n"
                    + "	COUNT(task_id) AS num_children, \n"
                    + "	NULL AS task_id,\n"
                    + "	task_name, \n"
                    + "	billable, \n"
                    + "	project_id, \n"
                    + "	MIN(task_start) AS task_start, \n"
                    + "	MAX(task_end) AS task_end, \n"
                    + "	CAST(task_start AS DATE) as task_date,\n"
                    + "	CAST(SUM(DATEDIFF(HOUR, task_start, task_end)) AS VARCHAR) + ':' +\n"
                    + "		RIGHT('0' + CAST(SUM(DATEDIFF(MINUTE, task_start, task_end)) AS VARCHAR), 2) + ':' +\n"
                    + "		RIGHT('0' + CAST(SUM(DATEDIFF(SECOND, task_start, task_end)) AS VARCHAR), 2) AS total_time,\n"
                    + "	CASE WHEN billable = 1 THEN (\n"
                    + "	CAST(SUM(DATEDIFF(HOUR, task_start, task_end)) AS VARCHAR) + ':' +\n"
                    + "		RIGHT('0' + CAST(SUM(DATEDIFF(MINUTE, task_start, task_end)) AS VARCHAR), 2) + ':' +\n"
                    + "		RIGHT('0' + CAST(SUM(DATEDIFF(SECOND, task_start, task_end)) AS VARCHAR), 2)) ELSE '0:00:00' END AS billable_time,\n"
                    + "		0 AS is_group_total\n"
                    + "FROM \n"
                    + "	Tasklog\n"
                    + "WHERE \n"
                    + "	task_end IS NOT NULL AND \n"
                    + "	person_id = CASE WHEN (@person_id = 0) THEN person_id ELSE @person_id END\n"
                    + "GROUP BY \n"
                    + "	task_name, \n"
                    + "	billable, \n"
                    + "	project_id, \n"
                    + "	CAST(task_start AS DATE)\n"
                    + "\n"
                    + "UNION\n"
                    + "\n"
                    + "-- CHILD TASKS (ACTUAL STORED TASKLOGS)\n"
                    + "SELECT \n"
                    + "	'TaskChild' AS type,\n"
                    + "	1 AS num_children, \n"
                    + "	task_id,\n"
                    + "	task_name, \n"
                    + "	billable, \n"
                    + "	project_id, \n"
                    + "	task_start, \n"
                    + "	task_end, \n"
                    + "	CAST(task_start AS DATE) AS task_date,\n"
                    + "	CONVERT(VARCHAR(5),DATEDIFF(SECOND,task_start,task_end)/60/60) + ':' + \n"
                    + "		RIGHT('0' + CONVERT(VARCHAR(2),DATEDIFF(SECOND,task_start,task_end)/60%60), 2) + ':' +\n"
                    + "		RIGHT('0' + CONVERT(VARCHAR(2),DATEDIFF(SECOND,task_start,task_end)%60),2)\n"
                    + "		AS total_time,\n"
                    + "		CASE WHEN billable = 1 THEN CONVERT(VARCHAR(5),DATEDIFF(SECOND,task_start,task_end)/60/60) + ':' + \n"
                    + "		RIGHT('0' + CONVERT(VARCHAR(2),DATEDIFF(SECOND,task_start,task_end)/60%60), 2) + ':' +\n"
                    + "		RIGHT('0' + CONVERT(VARCHAR(2),DATEDIFF(SECOND,task_start,task_end)%60),2) ELSE '0:00:00' END AS billable_time,\n"
                    + "		0 AS is_group_total\n"
                    + "FROM \n"
                    + "	Tasklog\n"
                    + "WHERE \n"
                    + "	task_end IS NOT NULL AND \n"
                    + "	person_id = CASE WHEN (@person_id = 0) THEN person_id ELSE @person_id END\n"
                    + "\n"
                    + "UNION\n"
                    + "\n"
                    + "-- DATE TASKS (CALCULATED TASKS BASED ON GROUPING OF ACTUAL TASK DATA)\n"
                    + "SELECT \n"
                    + "	'TaskGroup' AS type,\n"
                    + "	COUNT(*) AS num_children, \n"
                    + "	NULL,\n"
                    + "	NULL, \n"
                    + "	NULL, \n"
                    + "	NULL, \n"
                    + "	NULL, \n"
                    + "	NULL, \n"
                    + "	CAST(task_start AS DATE) as task_date,\n"
                    + "	CONVERT(VARCHAR(5),SUM(DATEDIFF(SECOND,task_start,task_end))/60/60) + ':' + \n"
                    + "		RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,task_start,task_end))/60%60), 2) + ':' +\n"
                    + "		RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,task_start,task_end))%60),2)\n"
                    + "		AS total_time,\n"
                    + "		CONVERT(VARCHAR(5),SUM(CASE WHEN billable = 1 THEN (DATEDIFF(SECOND,task_start,task_end))END)/60/60)+ ':' + \n"
                    + "		RIGHT('0' + CONVERT(VARCHAR(2),SUM(CASE WHEN billable = 1 THEN (DATEDIFF(SECOND,task_start,task_end))END)/60%60),2)+ ':'+\n"
                    + "		RIGHT('0' + CONVERT(VARCHAR(2),SUM(CASE WHEN billable = 1 THEN (DATEDIFF(SECOND,task_start,task_end))END)%60),2) AS billable_time,\n"
                    + "	1 AS is_group_total\n"
                    + "FROM \n"
                    + "	Tasklog\n"
                    + "WHERE \n"
                    + "	task_end IS NOT NULL AND \n"
                    + "	person_id = CASE WHEN (@person_id = 0) THEN person_id ELSE @person_id END\n"
                    + "GROUP BY \n"
                    + "	CAST(task_start AS DATE)\n"
                    + ") AS MyTaskTable\n"
                    + "\n"
                    + "ORDER BY \n"
                    + "	task_date DESC,\n"
                    + "	is_group_total DESC,\n"
                    + "	group_start ASC,\n"
                    + "	task_name DESC,\n"
                    + "	project_id DESC,\n"
                    + "	billable ASC,\n"
                    + "	task_start ASC,\n"
                    + "	type DESC";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, person_id);
            
            ResultSet rs = ps.executeQuery();

            // Lists that holds the different types of tasks
            List<TaskGroup> allTaskGroups = (groupBy != null) ? new ArrayList<>() : null;
            List<TaskParent> allTaskParents = (groupBy == null && includeTaskParents == true) ? new ArrayList<>() : null;
            List<TaskChild> allTaskChildren = (includeTaskParents == false && includeTaskChildren == true) ? null : null;
            
            // Variables that reference tasks currently being processed
            TaskGroup tg = null;
            TaskParent tp = null;

            while (rs.next()) {

                // Instantiate task entities and add them to their respective
                // relations to build a hierarchy (stacking). It is relying on
                // the task type and the specific order of tasks returned by 
                // the sql query.                
                if(rs.getString("type").equals("TaskGroup")) {                    

                    tg = new TaskGroup();

                    tg.setName(rs.getString("name"));
                    tg.setTime(rs.getString("total_time"));

                    // Add group to list of all groups
                    allTaskGroups.add(tg);
                }
                
                if(rs.getString("type").equals("TaskParent")) {

                    tp = new TaskParent();
                    
                    tp.setName(rs.getString("name"));
                    tp.setBillable(rs.getBoolean("billable"));
                    tp.setProjectId(rs.getInt("project_id"));
                    tp.setPersonId(rs.getInt("person_id"));
                    tp.setStart(rs.getTimestamp("task_start").toLocalDateTime());
                    tp.setEnd(rs.getTimestamp("task_end").toLocalDateTime());
                    tp.setTime(rs.getString("total_time"));
                    
                    // Add parent to current group or list of all parents
                    if(tg != null) {
                        tg.addParent(tp);
                    } else {
                        allTaskParents.add(tp);
                    }
                }
        
                if(rs.getString("type").equals("TaskChild")) {
                    TaskChild tc = new TaskChild();
                
                    tc.setId(rs.getInt("id"));
                    tc.setName(rs.getString("name"));
                    tc.setBillable(rs.getBoolean("billable"));
                    tc.setProjectId(rs.getInt("project_id"));
                    tc.setPersonId(rs.getInt("person_id"));
                    tc.setStart(rs.getTimestamp("task_start").toLocalDateTime());
                    tc.setEnd(rs.getTimestamp("task_end").toLocalDateTime());
                    tc.setTime(rs.getString("total_time"));
                    
                    // Add child to current parent or list of all children
                    if(tp != null) {
                        tp.addChild(tc);
                    } else {
                        allTaskChildren.add(tc);
                    }
                }
            }
            
            // Return list of tasks
            if(allTaskGroups != null) {
                return allTaskGroups;
            } else if (allTaskParents != null) {
                return allTaskParents;
            } else {
                return allTaskChildren;
            }
        } catch (SQLException e) {
            throw new DALException("Could not connect to database");
        }
    }
    
    public List<TaskGroup> getTasksGroupedByDate() throws DALException, SQLException {
        return (List<TaskGroup>)getTasks(0, "DATE", true, true);
    }
    



}
