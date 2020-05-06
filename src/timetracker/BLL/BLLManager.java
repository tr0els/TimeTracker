/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BLL;

import timetracker.BE.Task;
import java.sql.SQLException;
import java.util.List;
import timetracker.BE.Client;
import timetracker.BE.Profession;
import timetracker.BE.Project;
import timetracker.BE.Task.Log;
import timetracker.BE.User;
import timetracker.DAL.DALException;
import timetracker.DAL.DALManager;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class BLLManager
{

    /**
     * Singleton opsætning af vores BLLManager. singleton gør at vores
     * bllmanager ikke vil blive instansieret mere end en gang.
     */
    private static DALManager dal;
    private static BLLManager bll = null;

    private BLLManager() throws DALException
    {
        dal = DALManager.getInstance();
    }

    public static BLLManager getInstance() throws DALException
    {
        if (bll == null)
        {
            bll = new BLLManager();
        }
        return bll;
    }

    /**
     * Sender det info fra TaskModel "createProject" videre til DAL laget
     *
     * @param clientID
     * @param projectName
     * @param hourlyPay
     * @throws DALException
     */
    public void createProject(int clientID, String projectName, int hourlyPay) throws DALException
    {
        dal.createProject(clientID, projectName, hourlyPay);
    }

    /**
     * Sender det info fra TaskModel "deleteProject" videre til DAL laget
     *
     * @param clientID
     * @param projectName
     * @param hourlyPay
     * @throws DALException
     */
    public void deleteProject(int projectID) throws DALException
    {
        dal.deleteProject(projectID);
    }

    /**
     * opretter og starter en ny task og sender task object retur
     *
     * @param task_name
     * @param billable
     * @param project_id
     * @param person_id
     * @return
     */
    public void createTask(String task_name, boolean billable, int project_id, int person_id) {
        dal.createTask(task_name, billable, project_id, person_id);
    }

    /**
     * starter ny tidstagning på eksisterende task
     *
     * @param task_id
     */
    public void startTask(int task_id)
    {
        dal.startTask(task_id);
    }

    /**
     * pauser/stopper en aktiv task
     *
     * @param task_id
     */
    public void pauseTask(int task_id)
    {
        dal.pauseTask(task_id);

    }

    public Task getTask(int task_id)
    {
        return dal.getTaskbyTaskID(task_id);
    }
    
    /**
     * returnere en liste af Tasks udfra et project_id
     *
     * @param project_id
     * @return
     */
    public List<Task> getTaskById(int project_id)
    {
        return dal.getTaskbyProjectID(project_id);
    }

    /**
     * returnere en liste af Logs udfra et task_id
     *
     * @param task_id
     * @return
     */
    public List<Log> getTaskLogListById(int task_id) {
        
        return dal.getTaskLogListbyTaskID(task_id);
    }
    
    /**
     * returnere en liste af Logs udfra et person_id og dag (0 = idag, 1 = igår osv.)
     * @param task_id
     * @return 
     */
    public List<Log> getTaskLogListByDay(int person_id, int dag) {
        
        return dal.getTaskLogListbyDay(person_id, dag);
    }

    /**
     * Sender det info fra TaskModel "editProject" videre til DAL laget
     *
     * @param clientID
     * @param projectName
     * @param hourlyPay
     * @param projectID
     */
    public void editProject(int clientID, String projectName, int hourlyPay, int projectID)
    {
        dal.editProject(clientID, projectName, hourlyPay, projectID);
    }

    /**
     * Henter listen af projekter nede fra dal laget.
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public List<Project> getProjects() throws DALException, SQLException {
        return dal.getProjects();
    }
    
    public Project getProject(String projectName, int project_rate, int client_id){
        return dal.getProject(projectName, project_rate, client_id);
    }

    /**
     * Sender Client objekt ned til DAL laget som skal oprettes.
     *
     * @param client
     */
    public Client createClient(String navn, int timepris) {
        return  dal.createClient(navn, timepris);
        
    }

    /**
     * Sender Client objekt ned til DAL laget som skal ændres.
     *
     * @param client
     */
    public void editClient(Client client)
    {
        dal.editClient(client);
    }

    /**
     * Sender Client objekt ned til DAL laget som skal slettes.
     *
     * @param client
     */
    public void deleteClient(Client client)
    {
        dal.deleteClient(client);
    }

    /**
     * Henter listen af klienter nede fra DAL laget
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public List<Client> getClients() throws DALException, SQLException
    {
        return dal.getClients();
    }

    /**
     * Sender User objekt ned til DAL laget som skal oprettes.
     *
     * @param client
     */
    public void createUser(User user)
    {
        dal.createUser(user);
    }

    /**
     * Sender User objekt ned til DAL laget som skal ændres.
     *
     * @param client
     */
    public void editUser(User user)
    {
        dal.editUser(user);
    }

    /**
     * Sender Client objekt ned til DAL laget som skal slettes.
     *
     * @param client
     */
    public void deleteUser(User user)
    {
        dal.deleteUser(user);
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
        return dal.getUsers();
    }

    /**
     * Returnerer en liste med Professions fra DAL laget.
     * @return
     * @throws DALException
     * @throws SQLException 
     */
    public List<Profession> getProfessions() throws DALException, SQLException
    {
        return dal.getProfessions();
    }

    public  List<Project> getProjekctsbyClientID(Client client) {
        return dal.getProjectsbyClientID(client);
    }

}
