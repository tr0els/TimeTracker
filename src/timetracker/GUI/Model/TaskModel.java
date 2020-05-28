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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import timetracker.BE.Project;
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
public class TaskModel implements Runnable {

    /**
     * Singleton opsætning af vores model. singleton gør at vores model ikke vil
     * blive instansieret mere end en gang.
     */
    private static BLLManager bll;
    private static TaskModel model = null;

    private ObservableList<TaskForDataView> taskForDataview;

    public TaskModel() throws DALException, SQLException {
        bll = BLLManager.getInstance();
        taskForDataview = FXCollections.observableArrayList();

    }

    @Override
    public void run() {
        try {
            model = new TaskModel();
        } catch (DALException ex) {
            Logger.getLogger(TaskModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(TaskModel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static TaskModel getInstance() throws DALException, SQLException {
        if (model == null) {
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
    public void startTask(String task_name, boolean billable, int project_id, int person_id) throws DALException {
        bll.startTask(task_name, billable, project_id, person_id);
    }

    /**
     * Stopper en aktiv task
     *
     * @param person_id
     * @throws timetracker.DAL.DALException
     */
    public void stopTask(int person_id) throws DALException {
        bll.stopTask(person_id);

    }

    public List<TaskGroup> getTasksGroupedByDate(int personId, String groupBy, boolean includeTaskParents, boolean includeTaskChildren) throws DALException {
        return bll.getTasksGroupedByDate(personId, groupBy, includeTaskParents, includeTaskChildren);
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
    public TreeMap<Task, List<Task>> getTaskbyIDs(int project_id, int person_id) throws DALException {

        return bll.getTaskbyIDs(project_id, person_id);
    }

    public TreeMap<String, List<Task>> getTaskbyDays(int days, int person_id) throws DALException {
        return bll.getTaskbyDays(days, person_id);
    }

    /**
     * returnere en specifik task udfra task_id
     *
     * @param task_id
     * @return
     * @throws DALException
     */
    public Task getTaskbyID(int task_id) throws DALException {
        return bll.getTaskbyID(task_id);
    }

    /**
     * Sender en task til db for at opdatere den.
     *
     * @param task
     * @throws DALException
     */
    public void updateTaskbyID(Task task) throws DALException {
        bll.updateTaskbyID(task);
    }

    public ObservableList<TaskForDataView> getListOfTaskForDataView(Project project, User user, String fradato, String tildato, String monthStart, String monthEnd) throws DALException {
        taskForDataview.clear();
        taskForDataview.addAll(bll.getListOfTaskForDataView(project, user, fradato, tildato, monthStart, monthEnd));
        return taskForDataview;

    }

}
