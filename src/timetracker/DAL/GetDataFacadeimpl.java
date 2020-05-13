/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.DAL;

import java.util.List;
import timetracker.BE.Client;
import timetracker.BE.Profession;
import timetracker.BE.Project;
import timetracker.BE.Task;
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
    
    
    public GetDataFacadeimpl() throws DALException{
        brugerdao = new BrugerDAO();
        clientdao = new ClientDAO();
        projectdao = new ProjectDAO();
        taskdao = new TaskDAO();
        
    
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
    public Project getProject(String projectName, int ProjectRate, int ClientId) throws DALException{
    return projectdao.getProject(projectName, ProjectRate, ClientId);
    }
    
    @Override
    public List<Project> getProjectsbyClientID(Client client)throws DALException {
    return projectdao.getProjectsbyClientID(client);
    }
    
    @Override
    public List<Project> getProjectsbyID(int personId) throws DALException {
    return projectdao.getProjectsbyID(personId);
    }
    //Task
    @Override
    public void startTask(String task_name, boolean billable, int project_id, int person_id) throws DALException{
    taskdao.startTask(task_name, billable, project_id, person_id);
    }

    @Override
    public void stopTask(int person_id) throws DALException {
    taskdao.stopTask(person_id);
    }

    @Override
    public List<Task> getTaskbyIDs(int project_id, int person_id) throws DALException {
    return taskdao.getTaskbyIDs(project_id, person_id);
    }

    @Override
    public List<Task.Log> getLogsbyID(int task_id) throws DALException {
    return taskdao.getLogsbyID(task_id);
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
    public void deleteClient(Client client) throws DALException{
    clientdao.deleteClient(client);
    }

    @Override
    public List<Client> getClients()throws DALException {
    return clientdao.getClients();
    }
    //Bruger
    @Override
    public void createUser(User user, byte[] HashedPassword, byte[] salt) throws DALException{
    brugerdao.createUser(user, HashedPassword, salt);
    }

    @Override
    public void editUser(User user) throws DALException{
    brugerdao.editUser(user);
    }

    @Override
    public void deleteUser(User user) throws DALException {
    brugerdao.deleteUser(user);
    }

    @Override
    public List<User> getUsers() throws DALException{
    return brugerdao.getUsers();
    }


    @Override
    public User login(String email, byte[] hashedPassword) throws DALException {
    return brugerdao.login(email, hashedPassword);
    }

    @Override
    public byte[] getSalt(String email)throws DALException{
    return brugerdao.getSalt(email);
    }

    @Override
    public List<Profession> getProfessions()throws DALException {
    return brugerdao.getProfessions();
    }



  

    
}
