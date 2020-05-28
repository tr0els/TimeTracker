/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BLL;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import timetracker.BE.Task;
import java.sql.SQLException;
import java.util.List;
import java.util.TreeMap;
import timetracker.BE.Client;
import timetracker.BE.Profession;
import timetracker.BE.Project;
import timetracker.BE.TaskForDataView;
import timetracker.BE.TaskGroup;
import timetracker.BE.User;
import timetracker.DAL.DALException;
import timetracker.DAL.GetDataFacadeimpl;
import timetracker.DAL.IgetDataFacadeInterface;

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
    private static BLLManager bll = null;
    private final IgetDataFacadeInterface iGetData;

    /**
     * Constructor for BLLManager
     *
     * @throws DALException
     */
    private BLLManager() throws DALException
    {
        iGetData = new GetDataFacadeimpl();
    }

    /**
     * Returnerer vores BLLManager objekt, hvis den allerede kører, og ellers
     * starter den en ny instans og returnerer den.
     *
     * @return
     * @throws DALException
     */
    public static BLLManager getInstance() throws DALException
    {
        if (bll == null)
        {
            bll = new BLLManager();
        }
        return bll;
    }

    //-----TASK------
    /**
     * 
     * @param personId
     * @param groupBy
     * @param includeTaskParents
     * @param includeTaskChildren
     * @return
     * @throws DALException 
     */
    public List<TaskGroup> getTasksGroupedByDate(int personId, String groupBy, boolean includeTaskParents, boolean includeTaskChildren) throws DALException {
        return iGetData.getTasksGroupedByDate(personId, groupBy, includeTaskParents, includeTaskChildren);
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
        iGetData.startTask(task_name, billable, project_id, person_id);
    }

    /**
     * Pauser/stopper en aktiv task
     *
     * @param person_id
     */
    public void stopTask(int person_id) throws DALException
    {
        iGetData.stopTask(person_id);

    }

    
    public List<Project> getProjectsbyID(int person_id) throws DALException
    {
        return iGetData.getProjectsbyID(person_id);
    }

    /**
     * returnere en liste af Tasks udfra et project_id
     *
     * @param project_id
     * @return
     */
    public TreeMap<Task, List<Task>> getTaskbyIDs(int project_id, int person_id) throws DALException
    {
        return iGetData.getTaskbyIDs(project_id, person_id);
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
        return iGetData.getTaskbyID(task_id);
    }

    /**
     * Sender en task til db for at opdatere den.
     *
     * @param task
     * @throws DALException
     */
    public void updateTaskbyID(Task task) throws DALException
    {
        iGetData.updateTaskbyID(task);
    }

    //------PROJEKTER----- 
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
        iGetData.createProject(clientID, projectName, hourlyPay);

    }

    /**
     * Sender det info fra TaskModel "deleteProject" videre til DAL laget
     *
     * @param projectID
     * @throws DALException
     */
    public void deleteProject(int projectID) throws DALException
    {
        iGetData.deleteProject(projectID);
    }

    /**
     * Sender det info fra TaskModel "editProject" videre til DAL laget
     *
     * @param clientID
     * @param projectName
     * @param hourlyPay
     * @param projectID
     * @throws timetracker.DAL.DALException
     */
    public void editProject(int clientID, String projectName, int hourlyPay, int projectID) throws DALException
    {
        iGetData.editProject(clientID, projectName, hourlyPay, projectID);
    }

    /**
     * Henter listen af projekter nede fra dal laget.
     *
     * @return
     * @throws DALException
     */
    public List<Project> getProjects() throws DALException
    {
        return iGetData.getProjects();
    }

    /**
     * Returnerer en liste med projects som tilhører Client objektet.
     * @param client
     * @return
     * @throws DALException 
     */
    public List<Project> getProjekctsbyClientID(Client client) throws DALException
    {
        return iGetData.getProjectsbyClientID(client);
    }

    public List<Project> getProjectsWithExtradata() throws DALException
    {
        return iGetData.getProjectWithExtraData();
    }

    public List<Project> getProjectsToFilter(User comboUser, Client comboClient, String fradato, String tildato, String monthStart, String monthEnd) throws DALException
    {
        return iGetData.getProjectsToFilter(comboUser, comboClient, fradato, tildato, monthStart, monthEnd);
    }


    //-----KLIENTER-----
    /**
     * Sender Client objekt ned til DAL laget som skal oprettes.
     *
     * @param navn
     * @param timepris
     * @return
     * @throws timetracker.DAL.DALException
     */
    public Client createClient(String navn, int timepris) throws DALException
    {
        return iGetData.createClient(navn, timepris);

    }

    /**
     * Sender Client objekt ned til DAL laget som skal ændres.
     *
     * @param client
     */
    public void editClient(Client client) throws DALException
    {
        iGetData.editClient(client);
    }

    /**
     * Sender Client objekt ned til DAL laget som skal slettes.
     *
     * @param client
     */
    public void deleteClient(Client client) throws DALException
    {
        iGetData.deleteClient(client);
    }

    /**
     * Henter listen af klienter nede fra DAL laget
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public List<Client> getClients() throws DALException
    {
        return iGetData.getClients();
    }

    //------BRUGER------
    /**
     * Sender User objekt ned til DAL laget som skal oprettes.
     *
     * @param client
     */
    public void createUser(User user)
    {
        {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            try
            {

                final MessageDigest md = MessageDigest.getInstance("SHA-512");
                md.update(salt);

                final byte[] HashedPassword = md.digest(user.getPassword().getBytes(StandardCharsets.UTF_8));

                iGetData.createUser(user, HashedPassword, salt);

            } catch (final Exception e)
            {
            }
        }

    }

    /**
     * Sender User objekt ned til DAL laget som skal ændres.
     *
     * @param client
     */
    public void editUser(User user) throws DALException
    {
        iGetData.editUser(user);
    }

    /**
     * Sender Client objekt ned til DAL laget som skal slettes.
     *
     * @param client
     */
    public void deleteUser(User user) throws DALException
    {
        iGetData.deleteUser(user);
    }

    /**
     * Henter listen af Users nede fra DAL laget
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public List<User> getUsers() throws DALException
    {
        return iGetData.getUsers();
    }

    /**
     * Returnerer en liste med Professions fra DAL laget.
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public List<Profession> getProfessions() throws DALException
    {
        return iGetData.getProfessions();
    }

    /**
     * Fjerner en User 
     * @param disableUser 
     */
    public void disableUser(User disableUser)
    {
        iGetData.disableUser(disableUser);
    }

    public List<TaskForDataView> getListOfTaskForDataView(Project project, User user, String fradato, String tildato, String monthStart, String monthEnd) throws DALException
    {
        return iGetData.getListOfTaskForDataView(project, user, fradato, tildato, monthStart, monthEnd);
    }

    public void changelogTask(Task task, int person_id) throws DALException
    {
        iGetData.changelogTask(task, person_id);
    }

}
