/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Model;

import timetracker.BE.Task;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import timetracker.BE.Client;
import timetracker.BE.Project;
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
    private ObservableList<Client> allClients;
    private ObservableList<Project> allProjects;
    private ObservableList<User> allUsers;

    private TaskModel() throws DALException, SQLException {
        bll = BLLManager.getInstance();
        allProjects = FXCollections.observableArrayList();
        allProjects.addAll(bll.getProject());
        allClients = FXCollections.observableArrayList();
        allClients.addAll(bll.getClients());
        allUsers = FXCollections.observableArrayList();
        allUsers.addAll(bll.getUsers());
    }

    public static TaskModel getInstance() throws DALException, SQLException {
        if (model == null) {
            model = new TaskModel();
        }
        return model;
    }

    /**
     * Sender det info fra MainControllerens "createProject" videre til DAL
     * laget
     *
     * @param clientID
     * @param projectName
     * @param hourlyPay
     * @throws DALException
     */
    public void createProject(int clientID, String projectName, int hourlyPay) throws DALException {
        bll.createProject(clientID, projectName, hourlyPay);
    }

    /**
     * Sender det info fra MainControllerens "deleteProject" videre til DAL
     * laget
     *
     * @param clientID
     * @param projectName
     * @param hourlyPay
     * @throws DALException
     */
    public void deleteProject(int clientID, String projectName, int hourlyPay) throws DALException {
        bll.deleteProject(clientID, projectName, hourlyPay);
    }

    /**
     * opretter og starter en ny task og modtager task object
     *
     * @param task_name
     * @param billable
     * @param project_id
     * @param person_id
     * @return
     */
    public void createTask(String task_name, boolean billable, int project_id, int person_id) {
        bll.createTask(task_name, billable, project_id, person_id);
    }

    /**
     * starter ny tidstagning på eksisterende task
     *
     * @param task_id
     */
    public void startTask(int task_id) {
        bll.startTask(task_id);
    }

    /**
     * pauser/stopper en aktiv task
     *
     * @param task_id
     */
    public void pauseTask(int task_id) {
        bll.pauseTask(task_id);

    }


    /**
     * Sender det info fra MainControllerens "editProject" videre til DAL laget
     *
     * @param clientID
     * @param projectName
     * @param hourlyPay
     * @param projectID
     */
    public void editProject(int clientID, String projectName, int hourlyPay, int projectID) {
        bll.editProject(clientID, projectName, hourlyPay, projectID);
    }

    /**
     * retunere en liste af alle projekter.
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public List<Project> getProjects() throws DALException, SQLException {
        Comparator<Project> byName = (Project cl1, Project cl2) -> cl1.getProject_name().compareTo(cl2.getProject_name());
        allProjects.sort(byName);
        return allProjects;
    }

    /**
     * Sender Client objekt ned til DAL laget som skal oprettes.
     *
     * @param client
     */
    public void createClient(Client client) {
        bll.createClient(client);
    }

    /**
     * Sender Client objekt ned til DAL laget som skal ændres.
     *
     * @param client
     */
    public void editClient(Client client) {
        bll.deleteClient(client);
    }

    /**
     * Sender Client objekt ned til DAL laget som skal slettes.
     *
     * @param client
     */
    public void deleteClient(Client client) {
        bll.deleteClient(client);
    }

    /**
     * Returnerer en liste med alle Clients.
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public ObservableList<Client> getClients() throws DALException, SQLException {
        Comparator<Client> byName = (Client cl1, Client cl2) -> cl1.getClient_name().compareTo(cl2.getClient_name());
        allClients.sort(byName);
        return allClients;
    }
    
    /**
     * Sender User objekt ned til DAL laget som skal oprettes.
     *
     * @param client
     */
    public void createUser(User user)
    {
        bll.createUser(user);
    }

    /**
     * Sender User objekt ned til DAL laget som skal ændres.
     *
     * @param client
     */
    public void editUser(User user)
    {
        bll.editUser(user);
    }

    /**
     * Sender Client objekt ned til DAL laget som skal slettes.
     *
     * @param client
     */
    public void deleteUser(User user)
    {
        bll.deleteUser(user);
    }

    /**
     * Henter listen af Users nede fra DAL laget
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public List<User> getUsers() throws DALException, SQLException
    {
        Comparator<User> byName = (User cl1, User cl2) -> cl1.getName().compareTo(cl2.getName());
        allUsers.sort(byName);
        return allUsers;
    }

    public Client getClientDetails(Client selectedClient) {
        Client newclient = new Client ();
        newclient = bll.getClientDetails(selectedClient);
        return newclient; 
        
    }
}
