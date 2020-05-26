/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.DAL;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import timetracker.BE.Client;
import timetracker.BE.Profession;
import timetracker.BE.Project;
import timetracker.BE.Task;
import timetracker.BE.TaskForDataView;
import timetracker.BE.User;

/**
 *
 * @author Charlotte
 */
public class GetDataFacadeimpl implements IgetDataFacadeInterface {

    private BrugerDAO brugerdao;
    private ClientDAO clientdao; 
    private ProjectDAO projectdao;
    private TaskDAO taskdao;
    private ChangelogDAO changelogdao;

    public GetDataFacadeimpl() throws DALException {
        brugerdao = new BrugerDAO();
        clientdao = new ClientDAO();
        projectdao = new ProjectDAO();
        taskdao = new TaskDAO();
        changelogdao = new ChangelogDAO();
    }

    //Projekter 
    @Override
    public void createProject(int clientId, String projectName, int hourlyPay) {
        projectdao.createProject(clientId, projectName, hourlyPay);
    }

    @Override
    public void deleteProject(int projectId) throws DALException {
        projectdao.deleteProject(projectId);
    }

    @Override
    public void editProject(int clientId, String projectName, int hourlyPay, int projectId) throws DALException {
        projectdao.editProject(clientId, projectName, hourlyPay, projectId);

    }

    @Override
    public List<Project> getProjects() throws DALException {
        return projectdao.getProjects();
    }

    @Override
    public Project getProject(String projectName, int ProjectRate, int ClientId) throws DALException {
        return projectdao.getProject(projectName, ProjectRate, ClientId);
    }

    @Override
    public List<Project> getProjectsbyClientID(Client client) throws DALException {
        return projectdao.getProjectsbyClientID(client);
    }

    @Override
    public List<Project> getProjectsbyID(int personId) throws DALException {
        return projectdao.getProjectsbyID(personId);
    }

    @Override
    public List<Project> getProjectWithExtraData() throws DALException {
        return projectdao.getProjectsWithExtraData();
    }

    @Override
    public List<Project> getProjectsToFilter(User comboUser, Client comboClient, String fradato, String tildato, String monthStart, String monthEnd) throws DALException {
        return projectdao.getProjectsToFilter(comboUser, comboClient, fradato, tildato, monthStart, monthEnd);
    }

//    @Override
//    public List<Project> getProjectsForEmploy(int medarbejder_id) throws DALException {
//        return projectdao.getProjectsForEmploy(medarbejder_id);
//
//    }
    //Task
    @Override
    public void startTask(String task_name, boolean billable, int project_id, int person_id) throws DALException {
        taskdao.startTask(task_name, billable, project_id, person_id);
    }

    @Override
    public void stopTask(int person_id) throws DALException {
        taskdao.stopTask(person_id);
    }

    @Override
    public TreeMap<Task, List<Task>> getTaskbyIDs(int project_id, int person_id) throws DALException {
        return taskdao.getTaskbyIDs(project_id, person_id);
    }
    
    @Override
    public Task getTaskbyID(int task_id) throws DALException {
        return taskdao.getTaskbyID(task_id);
    }
    
    public void updateTaskbyID(Task task) throws DALException{
        taskdao.updateTaskbyID(task);
    }
    
    @Override
     public List<TaskForDataView> getListOfTaskForDataView(Project project, User user) throws DALException{
      return taskdao.getListOfTaskForDataView(project, user);
     } 

    //Klient
    @Override
    public Client createClient(String name, int timepris) throws DALException {
        return clientdao.createClient(name, timepris);
    }

    @Override
    public void editClient(Client client) throws DALException {
        clientdao.editClient(client);
    }

    @Override
    public void deleteClient(Client client) throws DALException {
        clientdao.deleteClient(client);
    }

    @Override
    public List<Client> getClients() throws DALException {
        return clientdao.getClients();
    }

    //Bruger
    @Override
    public void createUser(User user, byte[] HashedPassword, byte[] salt) throws DALException {
        brugerdao.createUser(user, HashedPassword, salt);
    }

    @Override
    public void editUser(User user) throws DALException {
        brugerdao.editUser(user);
    }

    @Override
    public void deleteUser(User user) throws DALException {
        brugerdao.deleteUser(user);
    }

    @Override
    public List<User> getUsers() throws DALException {
        return brugerdao.getUsers();
    }

    @Override
    public User login(String email, byte[] hashedPassword) throws DALException {
        return brugerdao.login(email, hashedPassword);
    }

    @Override
    public byte[] getSalt(String email) throws DALException {
        return brugerdao.getSalt(email);
    }

    @Override
    public List<Profession> getProfessions() throws DALException {
        return brugerdao.getProfessions();
    }

    @Override
    public boolean validateExistingEmail(String email) {
       return brugerdao.validateExistingEmail(email);
    }

    @Override
    public boolean valExistingEmailEdit(int person_id, String email) {
        return brugerdao.valExistingEmailEdit(person_id, email);
    }

    @Override
    public void disableUser(User disableUser) {
        brugerdao.disableUser(disableUser);
    }

    public void changelogTask(Task task, int person_id) {
        try
        {
            changelogdao.changelogTask(task, person_id);
        } catch (DALException ex)
        {
            Logger.getLogger(GetDataFacadeimpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
