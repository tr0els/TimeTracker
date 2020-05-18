/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.DAL;

import java.util.HashMap;
import java.util.List;
import timetracker.BE.Client;
import timetracker.BE.Profession;
import timetracker.BE.Project;
import timetracker.BE.Task;
import timetracker.BE.Task.Log;
import timetracker.BE.User;

/**
 *
 * @author Charlotte
 */
public interface IgetDataFacadeInterface {

    //Projekter 
    void createProject(int clientId, String projectName, int hourlyPay);

    void deleteProject(int projectId) throws DALException;

    void editProject(int clientId, String projectName, int hourlyPay, int projectId) throws DALException;

    List<Project> getProjects() throws DALException;

    List<Project> getProjectsbyID(int personId) throws DALException;

    Project getProject(String projectName, int ProjectRate, int ClientId) throws DALException;

    List<Project> getProjectWithExtraData() throws DALException;

    
    List<Project> getProjectsToFilter(User comboUser, Client comboClient, String fradato, String tildato,  String monthStart, String monthEnd) throws DALException;
        
    


 //   List<Project> getProjectsForEmploy(int medarbejder_id) throws DALException;


    //Task
    void startTask(String task_name, boolean billable, int project_id, int person_id) throws DALException;
    
    void stopTask(int person_id)throws DALException;
          
    HashMap<Task, List<Task>> getTaskbyIDs(int project_id, int person_id)throws DALException;
       
    public Task getTaskbyID(int task_id) throws DALException;
    
    public void updateTaskbyID(Task task) throws DALException;
            

    //Klient
    Client createClient(String name, int timepris) throws DALException;

    void editClient(Client client) throws DALException;

    void deleteClient(Client client) throws DALException;

    List<Client> getClients() throws DALException;

    //Bruger
    void createUser(User user, byte[] HashedPassword, byte[] salt) throws DALException;

    void editUser(User user) throws DALException;

    void deleteUser(User user) throws DALException;

    List<User> getUsers() throws DALException;

    List<Project> getProjectsbyClientID(Client client) throws DALException;

    User login(String email, byte[] hashedPassword) throws DALException;

    byte[] getSalt(String email) throws DALException;

    //Bruger --> profession
    List<Profession> getProfessions() throws DALException;
    
    boolean validateExistingEmail(String email);

    public boolean valExistingEmailEdit(int person_id, String email);



}
