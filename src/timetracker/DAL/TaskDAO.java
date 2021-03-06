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
import java.util.List;
import java.util.TreeMap;
import timetracker.BE.Project;
import timetracker.BE.Task;
import timetracker.BE.TaskBase;
import timetracker.BE.TaskChild;
import timetracker.BE.TaskGroup;
import timetracker.BE.TaskParent;
import timetracker.BE.TaskForDataView;
import timetracker.BE.User;
import timetracker.BLL.Sorttaskbydatedesc;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class TaskDAO
{

    private DatabaseConnector dbCon;

    public TaskDAO() throws DALException
    {
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
    public void startTask(String task_name, boolean billable, int project_id, int person_id) throws DALException
    {
        try ( Connection con = dbCon.getConnection())
        {
            String sql = "INSERT INTO Tasklog (task_name, billable, project_id, person_id, task_start) VALUES (?,?,?,?,CURRENT_TIMESTAMP)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, task_name);
            ps.setBoolean(2, billable);
            ps.setInt(3, project_id);
            ps.setInt(4, person_id);

            ps.execute();
        } catch (SQLException e)
        {
            throw new DALException("Kunne ikke starte Tasken");
        }
    }

    /**
     * Stopper en igangværende task
     *
     * @param person_id
     * @throws timetracker.DAL.DALException
     */
    public void stopTask(int person_id) throws DALException
    {
        try ( Connection con = dbCon.getConnection())
        {
            String sql = "UPDATE Tasklog SET task_end = CURRENT_TIMESTAMP\n"
                    + "WHERE person_id = ? AND task_end IS NULL";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, person_id);

            ps.execute();
        } catch (SQLException e)
        {
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
    public TreeMap<Task, List<Task>> getTaskbyIDs(int project_id, int person_id) throws DALException
    {
        TreeMap<Task, List<Task>> map = new TreeMap<>(new Sorttaskbydatedesc());

        String typeTask = "TASK";
        String typeLog = "LOG";
        try ( Connection con = dbCon.getConnection())
        {
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

            while (rs.next())
            {
                if (rs.getString("type").equalsIgnoreCase(typeLog))
                {
                    Task log = new Task();

                    LocalDateTime end_time;
                    if (rs.getTimestamp("task_end") != null)
                    {
                        end_time = rs.getTimestamp("task_end").toLocalDateTime();
                    } else
                    {
                        end_time = null;
                    }

                    log.setTaskId(rs.getInt("task_id"));
                    log.setBillable(rs.getBoolean("billable"));
                    log.setTotalTime(rs.getString("total_time"));
                    log.setStartTime(rs.getTimestamp("task_start").toLocalDateTime());
                    log.setEndTime(end_time);
                    log.setTaskName(rs.getString("task_name"));

                    logs.add(log);
                }

                if (rs.getString("type").equalsIgnoreCase(typeTask))
                {
                    Task task = new Task();
                    int task_id = rs.getInt("task_id");

                    task.setTaskId(task_id);
                    task.setTaskName(rs.getString("task_name"));
                    task.setProjectId(rs.getInt("project_id"));
                    task.setTotalTime(rs.getString("total_time"));
                    task.setLastWorkedOn(rs.getTimestamp("task_end").toLocalDateTime());

                    map.put(task, logs);

                    logs = new ArrayList<>();
                }
            }
        } catch (SQLException e)
        {
            throw new DALException("Kunne ikke finde Task på bruger og projekt");
        }
        return map;
    }

    /**
     * Returnere en specifik task udfra task_id
     *
     * @param task_id
     * @return
     * @throws DALException
     */
    public Task getTaskbyID(int task_id) throws DALException
    {
        Task task = new Task();

        try ( Connection con = dbCon.getConnection())
        {
            String sql = "SELECT * FROM Tasklog WHERE task_id = " + task_id + ";";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next())
            {
                LocalDateTime end_time;
                if (rs.getTimestamp("task_end") != null)
                {
                    end_time = rs.getTimestamp("task_end").toLocalDateTime();
                } else
                {
                    end_time = null;
                }
                task.setTaskId(rs.getInt("task_id"));
                task.setTaskName(rs.getString("task_name"));
                task.setProjectId(rs.getInt("project_id"));
                task.setPersonId(rs.getInt("person_id"));
                task.setStartTime(rs.getTimestamp("task_start").toLocalDateTime());
                task.setEndTime(end_time);
                task.setBillable(rs.getBoolean("billable"));
            }
        } catch (SQLException e)
        {
            throw new DALException("Kunne ikke finde tasken");
        }
        return task;
    }

    /**
     * Modtager en Task og opdatere den i DB.
     *
     * @param task
     * @throws DALException
     */
    public void updateTaskbyID(Task task) throws DALException
    {
        try ( Connection con = dbCon.getConnection())
        {
            int task_id = task.getTaskId();

            String sql = "UPDATE Tasklog SET task_name = ?, billable = ?, project_id = ?, person_id = ?, task_start = ?, task_end = ? WHERE task_id = ?;";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, task.getTaskName());
            ps.setBoolean(2, task.isBillable());
            ps.setInt(3, task.getProjectId());
            ps.setInt(4, task.getPersonId());
            ps.setTimestamp(5, java.sql.Timestamp.valueOf(task.getStartTime()));
            ps.setTimestamp(6, java.sql.Timestamp.valueOf(task.getEndTime()));
            ps.setInt(7, task_id);

            ps.execute();
        } catch (SQLException e)
        {
            throw new DALException("Kunne ikke opdatere tasken");
        }
    }

    /**
     * Returnerer en liste af tasks som er tilknyttet en bruger samt eventuelle
     * filtre.
     *
     * @param project
     * @param user
     * @param fradato
     * @param tildato
     * @param monthStart
     * @param monthEnd
     * @return
     * @throws DALException
     */
    public List<TaskForDataView> getListOfTaskForDataView(Project project, User user, String fradato, String tildato, String monthStart, String monthEnd) throws DALException
    {
        List<TaskForDataView> taskForOverviewData = new ArrayList<>();

        try ( Connection con = dbCon.getConnection())
        {
            String project_id = "";
            String user_id = "";
            String fradato_caluse = "";
            String tildato_clause = "";
            String periode_clause = "";

            if (fradato != null)
            {
                fradato_caluse += "AND  cast(tl.task_start as date)  >= '" + fradato + "'\n";
            }

            if (tildato != null)
            {
                tildato_clause += "AND cast(tl.task_end as date) <= '" + tildato + "' \n";
            }

            if (monthStart != null && monthEnd != null)
            {
                periode_clause += "AND cast(tl.task_start as date) Between convert(date, '" + monthStart + "', 103) and convert(date, '" + monthEnd + "', 103)";
            }

            if (project != null)
            {
                project_id += "and p.project_id =" + project.getProjectId() + "\n";
            }

            if (user != null)
            {
                user_id += "and per.person_id = " + user.getPersonId() + "\n";
            }
            String sql
                    = "select tl.task_name, tl.task_start, tl.task_end, tl.billable, concat(per.name, + ' ' + per.surname) as fullname, \n"
                    + "CONVERT(VARCHAR(5),SUM(DATEDIFF(SECOND,tl.task_start,tl.task_end))/60/60) + ':' +\n"
                    + "RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,tl.task_start,tl.task_end))/60%60), 2) + ':' +\n"
                    + "RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,tl.task_start,tl.task_end))%60),2) as time \n"
                    + "from tasklog tl , Project p, Person per \n"
                    + "where tl.project_id = p.project_id  \n"
                    + "and per.person_id = tl.person_id \n"
                    + "and tl.task_end is not null \n"
                    + project_id
                    + user_id
                    + fradato_caluse
                    + tildato_clause
                    + periode_clause
                    + "group by tl.task_name, tl.task_start, tl.task_end, tl.billable, per.name, per.surname\n"
                    + "order by tl.task_start asc;";

            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next())
            {
                LocalDateTime end_time;
                if (rs.getTimestamp("task_end") != null)
                {
                    end_time = rs.getTimestamp("task_end").toLocalDateTime();
                } else
                {
                    end_time = LocalDateTime.now();
                }
                String Billable;
                TaskForDataView task = new TaskForDataView();
                task.setName(rs.getString("task_name"));
                task.setStart(rs.getTimestamp("task_start").toLocalDateTime());
                task.setEnd(end_time);
                task.setTime(rs.getString("time"));
                task.setBillable(rs.getBoolean("billable"));
                task.setUser(rs.getString("fullname"));

                taskForOverviewData.add(task);
            }
        } catch (SQLException ex)
        {
            throw new DALException("kunne ikke hente din liste af task" + ex);
        }
        return taskForOverviewData;
    }

    /**
     * Henter en liste af brugerens opgaver. Hvilke opgaver der hentes og
     * hvordan de grupper bestemmes ud fra de angivne parametre.
     *
     * Bemærk: Det er muligt at henter en liste af brugerens opgaver grupperet
     * efter dato, med samlet tidsforbrug for dage og opgaver. Hver parent
     * opgave har de opgaver den består af grupperet under sig (stacked).
     *
     * Det var tiltænkt at metoden kunne udbygges til at kunne levere alle de
     * forskellige nødvendige udtræk af opgaver dynamisk, men dette er kun
     * delvist implementeret for nu.
     */
    private List<? extends TaskBase> getTasks(int person_id, String groupBy, boolean includeTaskParents, boolean includeTaskChildren) throws DALException
    {

        // Fetch task data 
        try ( Connection con = dbCon.getConnection())
        {
            String sql
                    = "SELECT *, MIN(task_start) OVER (PARTITION BY task_date, task_name, project_id, billable) AS group_start FROM \n"
                    + "(\n"
                    + "SELECT \n"
                    + "	'TaskParent' AS type,\n"
                    + "	COUNT(task_id) AS num_children, \n"
                    + "	NULL AS task_id,\n"
                    + "	task_name, \n"
                    + "	billable, \n"
                    + "	project_id, \n"
                    + "	NULL AS person_id, \n"
                    + "	MIN(task_start) AS task_start, \n"
                    + "	MAX(task_end) AS task_end, \n"
                    + "	CAST(task_start AS DATE) as task_date,\n"
                    + " RIGHT('0' + CONVERT(VARCHAR(5),SUM(DATEDIFF(SECOND,task_start,task_end))/60/60), 2) + ':' +\n"
                    + " RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,task_start,task_end))/60%60), 2) + ':' +\n"
                    + "	RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,task_start,task_end))%60),2)\n"
                    + "	AS total_time\n,"
                    + "	0 AS is_group_total\n"
                    + "FROM \n"
                    + "	Tasklog\n"
                    + "WHERE \n"
                    + "	task_end IS NOT NULL AND \n"
                    + "	person_id = ?\n"
                    + "GROUP BY \n"
                    + "	task_name, \n"
                    + "	billable, \n"
                    + "	project_id, \n"
                    + "	person_id, \n"
                    + "	CAST(task_start AS DATE)\n"
                    + "\n"
                    + "UNION\n"
                    + "\n"
                    + "SELECT \n"
                    + "	'TaskChild' AS type,\n"
                    + "	1 AS num_children, \n"
                    + "	task_id,\n"
                    + "	task_name, \n"
                    + "	billable, \n"
                    + "	project_id, \n"
                    + "	person_id, \n"
                    + "	task_start, \n"
                    + "	task_end, \n"
                    + "	CAST(task_start AS DATE) AS task_date,\n"
                    + " RIGHT('0' + CONVERT(VARCHAR(5),DATEDIFF(SECOND,task_start,task_end)/60/60), 2) + ':' +\n"
                    + " RIGHT('0' + CONVERT(VARCHAR(2),DATEDIFF(SECOND,task_start,task_end)/60%60), 2) + ':' +\n"
                    + "	RIGHT('0' + CONVERT(VARCHAR(2),DATEDIFF(SECOND,task_start,task_end)%60),2)\n"
                    + "	AS total_time\n,"
                    + "	0 AS is_group_total\n"
                    + "FROM \n"
                    + "	Tasklog\n"
                    + "WHERE \n"
                    + "	task_end IS NOT NULL AND \n"
                    + "	person_id = ?\n"
                    + "\n"
                    + "UNION\n"
                    + "\n"
                    + "SELECT \n"
                    + "	'TaskGroup' AS type,\n"
                    + "	COUNT(*) AS num_children, \n"
                    + "	NULL,\n"
                    + "	NULL, \n"
                    + "	NULL, \n"
                    + "	NULL, \n"
                    + "	NULL, \n"
                    + "	NULL, \n"
                    + "	NULL, \n"
                    + "	CAST(task_start AS DATE) as task_date,\n"
                    + " RIGHT('0' + CONVERT(VARCHAR(5),SUM(DATEDIFF(SECOND,task_start,task_end))/60/60), 2) + ':' +\n"
                    + " RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,task_start,task_end))/60%60), 2) + ':' +\n"
                    + "	RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,task_start,task_end))%60),2)\n"
                    + "	AS total_time\n,"
                    + "	1 AS is_group_total\n"
                    + "FROM \n"
                    + "	Tasklog\n"
                    + "WHERE \n"
                    + "	task_end IS NOT NULL AND \n"
                    + "	person_id = ?\n"
                    + "GROUP BY \n"
                    + "	CAST(task_start AS DATE)\n"
                    + ") AS MyTaskTable\n"
                    + "\n"
                    + "ORDER BY \n"
                    + "	task_date DESC,\n"
                    + "	is_group_total DESC,\n"
                    + "	group_start DESC,\n"
                    + "	task_name DESC,\n"
                    + "	project_id DESC,\n"
                    + "	billable ASC,\n"
                    + "	type DESC,\n"
                    + "	task_start DESC";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, person_id);
            ps.setInt(2, person_id);
            ps.setInt(3, person_id);

            ResultSet rs = ps.executeQuery();

            // lists that holds the different types of tasks
            List<TaskGroup> allTaskGroups = new ArrayList<>();
            List<TaskParent> allTaskParents = (groupBy.isEmpty() && includeTaskParents == true) ? new ArrayList<>() : null;
            List<TaskChild> allTaskChildren = (includeTaskParents == false && includeTaskChildren == true) ? null : null;

            // variables to reference tasks currently being processed
            TaskGroup tg = null;
            TaskParent tp = null;

            while (rs.next())
            {
                // instantiate task entities and add them to their respective
                // relations to build a hierarchy (stacking) og tasks. 
                // It is relying on the task type and the specific order 
                // of tasks returned by the sql query. 

                // create task group (date)
                if (rs.getString("type").equals("TaskGroup"))
                {
                    tg = new TaskGroup();

                    tg.setName(rs.getString("task_date"));
                    tg.setTime(rs.getString("total_time"));

                    // add group to list of all groups
                    allTaskGroups.add(tg);
                }

                // create task parent
                if (rs.getString("type").equals("TaskParent"))
                {
                    tp = new TaskParent();

                    tp.setName(rs.getString("task_name"));
                    tp.setBillable(rs.getBoolean("billable"));
                    tp.setProjectId(rs.getInt("project_id"));
                    tp.setStart(rs.getTimestamp("task_start").toLocalDateTime());
                    tp.setEnd(rs.getTimestamp("task_end").toLocalDateTime());
                    tp.setTime(rs.getString("total_time"));

                    // add parent to current group or list of all parents
                    if (tg != null)
                    {
                        tg.addParent(tp);
                    } else
                    {
                        allTaskParents.add(tp);
                    }
                }

                // create task child
                if (rs.getString("type").equals("TaskChild"))
                {
                    TaskChild tc = new TaskChild();

                    tc.setId(rs.getInt("task_id"));
                    tc.setName(rs.getString("task_name"));
                    tc.setBillable(rs.getBoolean("billable"));
                    tc.setProjectId(rs.getInt("project_id"));
                    tc.setPersonId(rs.getInt("person_id"));
                    tc.setStart(rs.getTimestamp("task_start").toLocalDateTime());
                    tc.setEnd(rs.getTimestamp("task_end").toLocalDateTime());
                    tc.setTime(rs.getString("total_time"));

                    // add child to current parent or list of all children
                    if (tp != null)
                    {
                        // add reference of parent to child
                        tc.setParent(tp);
                        tp.addChild(tc);
                    } else
                    {
                        allTaskChildren.add(tc);
                    }
                }
            }

            // return the right list of tasks
            if (allTaskGroups != null)
            {
                return allTaskGroups;
            } else if (allTaskParents != null)
            {
                return allTaskParents;
            } else
            {
                return allTaskChildren;
            }
        } catch (SQLException ex)
        {
            throw new DALException("Something went wrong getting users tasks from database" + ex);
        }
    }

    /**
     * Ved hjælp af getTasks metoden hentes en liste af brugerens opgaver
     * grupperet efter dato, med samlet tidsforbrug for dage og opgaver. Hver
     * parent opgave har de opgaver den består af grupperet under sig (stacked).
     *
     * @param personId
     * @param groupBy
     * @param includeTaskParents
     * @param includeTaskChildren
     * @return en liste af alle stacked efter gruppe (dato), parent og child
     * @throws DALException
     */
    public List<TaskGroup> getTasksGroupedByDate(int personId, String groupBy, boolean includeTaskParents, boolean includeTaskChildren) throws DALException
    {
        return (List<TaskGroup>) getTasks(personId, "DATE", true, true);
    }

    /**
     * Opdaterer ændringer i en task
     *
     * @param taskChild er task objektet der skal opdateres
     * @throws DALException
     */
    public void editTask(TaskChild taskChild) throws DALException
    {
        try ( Connection con = dbCon.getConnection())
        {
            String sql = "UPDATE Tasklog SET task_name = ?, billable = ?, project_id = ?, task_start = ?, task_end = ? WHERE task_id = ?;";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, taskChild.getName());
            ps.setBoolean(2, taskChild.isBillable());
            ps.setInt(3, taskChild.getProjectId());
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(taskChild.getStart()));
            ps.setTimestamp(5, java.sql.Timestamp.valueOf(taskChild.getEnd()));
            ps.setInt(6, taskChild.getId());

            ps.execute();
        } catch (SQLException e)
        {
            throw new DALException("Kunne ikke opdatere tasken");
        }
    }

    /**
     * Henter eventuelt uafsluttet task der skal genoptages
     *
     * @param personId id of logged in person
     * @return TaskChild object
     * @throws DALException
     */
    public TaskChild getStartedTask(int personId) throws DALException
    {
        try ( Connection con = dbCon.getConnection())
        {
            String sql = "SELECT task_id,\n"
                    + "	task_name, \n"
                    + "	billable, \n"
                    + "	project_id, \n"
                    + "	person_id, \n"
                    + "	task_start, \n"
                    + "	task_end, \n"
                    + " RIGHT('0' + CONVERT(VARCHAR(5),DATEDIFF(SECOND,task_start,task_end)/60/60), 2) + ':' +\n"
                    + " RIGHT('0' + CONVERT(VARCHAR(2),DATEDIFF(SECOND,task_start,task_end)/60%60), 2) + ':' +\n"
                    + "	RIGHT('0' + CONVERT(VARCHAR(2),DATEDIFF(SECOND,task_start,task_end)%60),2)\n"
                    + "	AS total_time\n"
                    + "FROM \n"
                    + "	Tasklog\n"
                    + "WHERE \n"
                    + "	task_end IS NULL AND \n"
                    + "	person_id = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, personId);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                TaskChild tc = new TaskChild();

                tc.setId(rs.getInt("task_id"));
                tc.setName(rs.getString("task_name"));
                tc.setBillable(rs.getBoolean("billable"));
                tc.setProjectId(rs.getInt("project_id"));
                tc.setPersonId(rs.getInt("person_id"));
                tc.setStart(rs.getTimestamp("task_start").toLocalDateTime());
                tc.setEnd(null);
                tc.setTime(rs.getString("total_time"));

                return tc;
            }

            // no started task was found
            return null;
        } catch (SQLException ex)
        {
            throw new DALException("Database exception: " + ex);
        }
    }
}
