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
import timetracker.BE.TaskChild;
import timetracker.BE.TaskForDataView;
import timetracker.BE.TaskGroup;
import timetracker.BE.TaskParent;
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
    //private ObservableList<Client> allClients;
    //private ObservableList<Project> allProjects;
    //private ObservableList<User> allUsers;
    private ObservableList<TaskForDataView> taskForDataview;

    public TaskModel() throws DALException, SQLException {
        bll = BLLManager.getInstance();
        //allProjects = FXCollections.observableArrayList();
        //allProjects.addAll(bll.getProjects());
        //allClients = FXCollections.observableArrayList();
        // allClients.addAll(bll.getClients());
        // allUsers = FXCollections.observableArrayList();
        //allUsers.addAll(bll.getUsers());
//        taskById = FXCollections.observableArrayList();
//        logsById = FXCollections.observableArrayList();
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

//    /**
//     * Sender det info fra MainControllerens "createProject" videre til DAL
//     * laget
//     *
//     * @param clientID
//     * @param projectName
//     * @param hourlyPay
//     * @throws DALException
//     */
//    public void createProject(int clientID, String projectName, int hourlyPay) throws DALException
//    {
//        bll.createProject(clientID, projectName, hourlyPay);
//    }
//
//    /**
//     * Sender det info fra MainControllerens "deleteProject" videre til DAL
//     * laget
//     *
//     * @param clientID
//     * @param projectName
//     * @param hourlyPay
//     * @throws DALException
//     */
//    public void deleteProject(int projectID) throws DALException
//    {
//        bll.deleteProject(projectID);
//    }
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
    
    /**
     * Henter en liste af brugerens tasks grupperet efter dato og stacked
     * @param personId
     * @param groupBy
     * @param includeTaskParents
     * @param includeTaskChildren
     * @return
     * @throws DALException 
     */
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
    
    /**
     * Opdater ændringer på en task Parent ved at ændre alle stacked Children
     * @param taskParent er den overordnede task med en liste af children
     */
    public void updateTask(TaskParent taskParent) {
        for (TaskChild taskChild : taskParent.getChildren()) {
            updateTask(taskChild);
        }
    }
    
    /**
     * Opdater ændringer på en task Child i databasen
     * @param taskChild er den task der skal opdateres
     */
    public void updateTask(TaskChild taskChild) {
        //bll.updateTask(taskChild); todo
    }
    
    
    
    public TreeMap<Task, List<Task>> getTaskbyIDs(int project_id, int person_id) throws DALException {
//        taskById.clear();
//        taskById.addAll(bll.getTaskbyIDs(project_id, person_id));
//        return taskById;
        return bll.getTaskbyIDs(project_id, person_id);
    }

    /**
     * returnere en specifik task udfra task_id
     * @param task_id
     * @return
     * @throws DALException 
     */
    public Task getTaskbyID(int task_id) throws DALException {
        return bll.getTaskbyID(task_id);
    }
    
    /**
     * Sender en task til db for at opdatere den.
     * @param task
     * @throws DALException 
     */
    public void updateTaskbyID(Task task) throws DALException{
        bll.updateTaskbyID(task);
    }
//    public ObservableList<Log> getLogsbyID(int task_id) throws DALException {
//        logsById.clear();
//        logsById.addAll(bll.getLogsbyID(task_id));
//        return logsById;
//    }
//    /**
//     * Sender det info fra MainControllerens "editProject" videre til DAL laget
//     *
//     * @param clientID
//     * @param projectName
//     * @param hourlyPay
//     * @param projectID
//     */
//    public void editProject(int clientID, String projectName, int hourlyPay, int projectID)
//    {
//        bll.editProject(clientID, projectName, hourlyPay, projectID);
//    }
//
//    /**
//     * retunere en liste af alle projekter.
//     *
//     * @return
//     * @throws DALException
//     * @throws SQLException
//     */
//    public ObservableList<Project> getProjects() throws DALException, SQLException {
//        allProjects.clear();
//        allProjects.addAll(bll.getProjects());
//        Comparator<Project> byName = (Project cl1, Project cl2) -> cl1.getProject_name().compareTo(cl2.getProject_name());
//        allProjects.sort(byName);
//
//        return allProjects;
//    }
//
//    public Project getProject(String projectName, int project_rate, int client_id)
//    {
//        return bll.getProject(projectName, project_rate, client_id);
//    }
//    /**DENNE METODE LIGGER INDE I CLIENTMODEL
//     * Sender Client objekt ned til DAL laget som skal oprettes.
//     *
//     * @param client
//     */
//    public void createClient(Client client) {
//        bll.createClient(client);
//    }
//    /**
//     * Sender Client objekt ned til DAL laget som skal ændres.
//     *
//     * @param client
//     */
//    public void editClient(Client client)
//    {
//        bll.deleteClient(client);
//    }
//
//    /**
//     * Sender Client objekt ned til DAL laget som skal slettes.
//     *
//     * @param client
//     */
//    public void deleteClient(Client client)
//    {
//        bll.deleteClient(client);
//    }
//    /**
//     * Returnerer en liste med alle Clients.
//     *
//     * @return
//     * @throws DALException
//     * @throws SQLException
//     */
//    public ObservableList<Client> getClients() throws DALException, SQLException
//    {
//        Comparator<Client> byName = (Client cl1, Client cl2) -> cl1.getClient_name().compareTo(cl2.getClient_name());
//        allClients.sort(byName);
//        return allClients;
//    }
//    /**
//     * Sender User objekt ned til DAL laget som skal oprettes.
//     *
//     * @param client
//     */
//    public void createUser(User user)
//    {
//        bll.createUser(user);
//    }
//
//    /**
//     * Sender User objekt ned til DAL laget som skal ændres.
//     *
//     * @param client
//     */
//    public void editUser(User user)
//    {
//        bll.editUser(user);
//    }
//
//    /**
//     * Sender Client objekt ned til DAL laget som skal slettes.
//     *
//     * @param client
//     */
//    public void deleteUser(User user)
//    {
//        bll.deleteUser(user);
//    }
//
//    /**
//     * Henter listen af Users nede fra DAL laget
//     *
//     * @return
//     * @throws DALException
//     * @throws SQLException
//     */
//    public ObservableList<User> getUsers() throws DALException, SQLException
//    {
//        allUsers.clear();
//        allUsers.addAll(bll.getUsers());
//        Comparator<User> byName = (User cl1, User cl2) -> cl1.getName().compareTo(cl2.getName());
//        allUsers.sort(byName);
//        return allUsers;
//    }
//
//    /**
//     * Returnerer en liste med de forskellige Professions.
//     *
//     * @return
//     * @throws DALException
//     * @throws SQLException
//     */
//    public ObservableList<Profession> getProfessions() throws DALException, SQLException
//    {
//        allProfessions.addAll(bll.getProfessions());
//        return allProfessions;
//    }
 
     public ObservableList<TaskForDataView> getListOfTaskForDataView(Project project, User user, String fradato, String tildato, String monthStart, String monthEnd) throws DALException{
     taskForDataview.clear();
     taskForDataview.addAll(bll.getListOfTaskForDataView(project, user, fradato,tildato,monthStart,monthEnd));
     return taskForDataview;
     
     
     }
    
}
