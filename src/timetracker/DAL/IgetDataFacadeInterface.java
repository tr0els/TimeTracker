/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.DAL;

import java.util.List;
import java.util.TreeMap;
import timetracker.BE.Client;
import timetracker.BE.Profession;
import timetracker.BE.Project;
import timetracker.BE.Task;
import timetracker.BE.TaskForDataView;
import timetracker.BE.TaskGroup;
import timetracker.BE.User;

/**
 *
 * @author @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public interface IgetDataFacadeInterface
{

    //Project 
    void createProject(int clientId, String projectName, int hourlyPay);

    void deleteProject(int projectId) throws DALException;

    void editProject(int clientId, String projectName, int hourlyPay, int projectId) throws DALException;

    List<Project> getProjects() throws DALException;

    List<Project> getProjectsbyID(int personId) throws DALException;

    List<Project> getProjectWithExtraData() throws DALException;

    List<Project> getProjectsToFilter(User comboUser, Client comboClient, String fradato, String tildato, String monthStart, String monthEnd) throws DALException;

    //Task
    public List<TaskGroup> getTasksGroupedByDate(int personId, String groupBy, boolean includeTaskParents, boolean includeTaskChildren) throws DALException;

    void startTask(String task_name, boolean billable, int project_id, int person_id) throws DALException;

    void stopTask(int person_id) throws DALException;

    TreeMap<Task, List<Task>> getTaskbyIDs(int project_id, int person_id) throws DALException;
    
    TreeMap<String, List<Task>> getTaskbyDays(int days, int person_id) throws DALException;

    public Task getTaskbyID(int task_id) throws DALException;

    public void updateTaskbyID(Task task) throws DALException;

    public List<TaskForDataView> getListOfTaskForDataView(Project project, User user, String fradato, String tildato, String monthStart, String monthEnd) throws DALException;

    //Client
    Client createClient(String name, int timepris) throws DALException;

    void editClient(Client client) throws DALException;

    void deleteClient(Client client) throws DALException;

    List<Client> getClients() throws DALException;

    //User
    void createUser(User user, byte[] HashedPassword, byte[] salt) throws DALException;

    void editUser(User user) throws DALException;

    void deleteUser(User user) throws DALException;

    List<User> getUsers() throws DALException;

    List<Project> getProjectsbyClientID(Client client) throws DALException;

    User login(String email, byte[] hashedPassword) throws DALException;

    byte[] getSalt(String email) throws DALException;

    //User --> Profession
    List<Profession> getProfessions() throws DALException;

    boolean validateExistingEmail(String email);

    public boolean valExistingEmailEdit(int person_id, String email);

    public void disableUser(User disableUser);

    //Changelog
    public void changelogTask(Task task, int person_id) throws DALException;

}
