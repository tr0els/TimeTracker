/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Model;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import timetracker.BE.Client;
import timetracker.BE.Project;
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

    private TaskModel() throws DALException, SQLException {
        bll = BLLManager.getInstance();
        allProjects = FXCollections.observableArrayList();
        allProjects.addAll(bll.getProject());
        allClients = FXCollections.observableArrayList();
        allClients.addAll(bll.getClients());
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
    public List<Client> getClients() throws DALException, SQLException {
        Comparator<Client> byName = (Client cl1, Client cl2) -> cl1.getClient_name().compareTo(cl2.getClient_name());
        allClients.sort(byName);
        return allClients;
    }
}
