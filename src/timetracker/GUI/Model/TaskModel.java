/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Model;

import timetracker.BE.Task;
import java.sql.SQLException;
import java.util.List;
import java.util.TreeMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import timetracker.BE.Project;
import timetracker.BE.TaskChild;
import timetracker.BE.TaskForDataView;
import timetracker.BE.TaskGroup;
import timetracker.BE.User;
import timetracker.BLL.BLLManager;
import timetracker.DAL.DALException;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class TaskModel {

    /**
     * Singleton opsætning af vores model. singleton gør at vores model ikke vil
     * blive instansieret mere end en gang.
     */
    private static BLLManager bll;
    private static TaskModel model = null;
    private ObservableList<TaskForDataView> taskForDataview;

    public TaskModel() throws DALException, SQLException
    {
        bll = BLLManager.getInstance();
        taskForDataview = FXCollections.observableArrayList();
    }

    public static TaskModel getInstance() throws DALException, SQLException
    {
        if (model == null)
        {
            model = new TaskModel();
        }
        return model;
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
        bll.startTask(task_name, billable, project_id, person_id);
    }

    /**
     * Stopper en aktiv task
     *
     * @param person_id
     * @throws timetracker.DAL.DALException
     */
    public void stopTask(int person_id) throws DALException
    {
        bll.stopTask(person_id);
    }

    /**
     * Henter eventuelt uafsluttet tasks der skal genoptages
     *
     * @param personId id of logged in person
     * @return TaskChild object
     * @throws DALException
     */
    public TaskChild getStartedTask(int personId) throws DALException
    {
        return bll.getStartedTask(personId);
    }

    /**
     * Henter og en liste af brugerens opgaver grupperet efter dato,
     * med samlet tidsforbrug for dage og opgaver. hver parent opgave har de
     * opgaver den består af grupperet under sig (stacked).
     *
     * @param personId på brugeren der er logget ind
     * @param groupBy 
     * @param includeTaskParents
     * @param includeTaskChildren
     * @return
     * @throws DALException
     */
    public List<TaskGroup> getTasksGroupedByDate(int personId, String groupBy, boolean includeTaskParents, boolean includeTaskChildren) throws DALException
    {
        return bll.getTasksGroupedByDate(personId, groupBy, includeTaskParents, includeTaskChildren);
    }

    /**
     * Opdaterer en redigeret task i databasen
     *
     * @param taskChild er det task objekt der skal opdateres
     */
    public void editTask(TaskChild taskChild) throws DALException
    {
        bll.editTask(taskChild);
    }

    /**
     * Bygger observable liste af task udfra et project_id som kan bruges i
     * vores view
     *
     * @param project_id
     * @param person_id
     * @return
     * @throws timetracker.DAL.DALException
     */
    public TreeMap<Task, List<Task>> getTaskbyIDs(int project_id, int person_id) throws DALException
    {
//        taskById.clear();
//        taskById.addAll(bll.getTaskbyIDs(project_id, person_id));
//        return taskById;
        return bll.getTaskbyIDs(project_id, person_id);
    }

    /**
     * returnere en specifik task udfra task_id
     *
     * @param task_id
     * @return
     * @throws DALException
     */
    public Task getTaskbyID(int task_id) throws DALException
    {
        return bll.getTaskbyID(task_id);
    }

    /**
     * Sender en task til db for at opdatere den.
     *
     * @param task
     * @throws DALException
     */
    public void updateTaskbyID(Task task) throws DALException
    {
        bll.updateTaskbyID(task);
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
    public ObservableList<TaskForDataView> getListOfTaskForDataView(Project project, User user, String fradato, String tildato, String monthStart, String monthEnd) throws DALException
    {
        taskForDataview.clear();
        taskForDataview.addAll(bll.getListOfTaskForDataView(project, user, fradato, tildato, monthStart, monthEnd));
        return taskForDataview;

    }

}
