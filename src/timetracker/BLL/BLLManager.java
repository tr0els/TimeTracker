/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BLL;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import timetracker.BE.Task;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import timetracker.BE.Client;
import timetracker.BE.Profession;
import timetracker.BE.Project;
import timetracker.BE.Task.Log;
import timetracker.BE.User;
import timetracker.DAL.DALException;
import timetracker.DAL.DALManager;
import timetracker.DAL.GetDataFacadeimpl;
import timetracker.DAL.IgetDataFacadeInterface;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class BLLManager {

    /**
     * Singleton opsætning af vores BLLManager. singleton gør at vores
     * bllmanager ikke vil blive instansieret mere end en gang.
     */
    //private static DALManager dal;
    private static BLLManager bll = null;
    private final IgetDataFacadeInterface iGetData;

    private BLLManager() throws DALException {
        // dal = DALManager.getInstance();
        iGetData = new GetDataFacadeimpl();
    }

    public static BLLManager getInstance() throws DALException {
        if (bll == null) {
            bll = new BLLManager();
        }
        return bll;
    }

    //-----TASK------
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
        iGetData.startTask(task_name, billable, project_id, person_id);
    }

    /**
     * pauser/stopper en aktiv task
     *
     * @param person_id
     */
    public void stopTask(int person_id) throws DALException {
        iGetData.stopTask(person_id);

    }

    public List<Project> getProjectsbyID(int person_id) throws DALException {
        return iGetData.getProjectsbyID(person_id);
    }

    /**
     * returnere en liste af Tasks udfra et project_id
     *
     * @param project_id
     * @return
     */
    public HashMap<Task, List<Task>> getTaskbyIDs(int project_id, int person_id) throws DALException  {
        return iGetData.getTaskbyIDs(project_id, person_id);
    }

    /**
     * returnere en specifik task udfra task_id
     * @param task_id
     * @return
     * @throws DALException 
     */
    public Task getTaskbyID(int task_id) throws DALException {
        return iGetData.getTaskbyID(task_id);
    }

    /**
     * Sender en task til db for at opdatere den.
     * @param task
     * @throws DALException 
     */
    public void updateTaskbyID(Task task) throws DALException{
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
    public void createProject(int clientID, String projectName, int hourlyPay) throws DALException {
        iGetData.createProject(clientID, projectName, hourlyPay);

    }

    /**
     * Sender det info fra TaskModel "deleteProject" videre til DAL laget
     *
     * @param projectID
     * @throws DALException
     */
    public void deleteProject(int projectID) throws DALException {
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
    public void editProject(int clientID, String projectName, int hourlyPay, int projectID) throws DALException {
        iGetData.editProject(clientID, projectName, hourlyPay, projectID);
    }

    /**
     * Henter listen af projekter nede fra dal laget.
     *
     * @return
     * @throws DALException
     */
    public List<Project> getProjects() throws DALException {
        return iGetData.getProjects();
    }

    public Project getProject(String projectName, int project_rate, int client_id) throws DALException {
        return iGetData.getProject(projectName, project_rate, client_id);
    }

    public List<Project> getProjekctsbyClientID(Client client) throws DALException {
        return iGetData.getProjectsbyClientID(client);
    }

    public List<Project> getProjectsWithExtradata() throws DALException {
        return iGetData.getProjectWithExtraData();
    }

       
      
    public List<Project> getProjectsToFilter(User comboUser, Client comboClient, String fradato, String tildato, String monthStart, String monthEnd) throws DALException {
        return iGetData.getProjectsToFilter(comboUser, comboClient, fradato, tildato, monthStart, monthEnd);
	}

//    public List<Project> getProjectsForEmploy(int medarbejder_id) throws DALException {
//        return iGetData.getProjectsForEmploy(medarbejder_id);
//
//    }

    //-----KLIENTER-----
    /**
     * Sender Client objekt ned til DAL laget som skal oprettes.
     *
     * @param navn
     * @param timepris
     * @return
     * @throws timetracker.DAL.DALException
     */
    public Client createClient(String navn, int timepris) throws DALException {
        return iGetData.createClient(navn, timepris);

    }

    /**
     * Sender Client objekt ned til DAL laget som skal ændres.
     *
     * @param client
     */
    public void editClient(Client client) throws DALException {
        iGetData.editClient(client);
    }

    /**
     * Sender Client objekt ned til DAL laget som skal slettes.
     *
     * @param client
     */
    public void deleteClient(Client client) throws DALException {
        iGetData.deleteClient(client);
    }

    /**
     * Henter listen af klienter nede fra DAL laget
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public List<Client> getClients() throws DALException {
        return iGetData.getClients();
    }

    //------BRUGER------
    /**
     * Sender User objekt ned til DAL laget som skal oprettes.
     *
     * @param client
     */
    public void createUser(User user) {
        {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            try {

                final MessageDigest md = MessageDigest.getInstance("SHA-512");
                md.update(salt);

                final byte[] HashedPassword = md.digest(user.getPassword().getBytes(StandardCharsets.UTF_8));

                iGetData.createUser(user, HashedPassword, salt);

            } catch (final Exception e) {
            }
        }

    }

    /**
     * Sender User objekt ned til DAL laget som skal ændres.
     *
     * @param client
     */
    public void editUser(User user) throws DALException {
        iGetData.editUser(user);
    }

    /**
     * Sender Client objekt ned til DAL laget som skal slettes.
     *
     * @param client
     */
    public void deleteUser(User user) throws DALException {
        iGetData.deleteUser(user);
    }

    /**
     * Henter listen af Users nede fra DAL laget
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public List<User> getUsers() throws DALException {
        return iGetData.getUsers();
    }

    /**
     * Returnerer en liste med Professions fra DAL laget.
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public List<Profession> getProfessions() throws DALException {
        return iGetData.getProfessions();
    }

    /**
     * tager det input som kommer fra modelen og hasher passworded, sender det
     * videre til dal.
     *
     * @param email
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     */
    public User login(String email, String password) throws NoSuchAlgorithmException, DALException {
        byte[] salt = iGetData.getSalt(email);

        final MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt);

        final byte[] HashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

        return iGetData.login(email, HashedPassword);

    }


}
