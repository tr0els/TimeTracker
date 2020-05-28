/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Model;

import java.sql.SQLException;
import java.util.Comparator;
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
public class ProjektModel {

    private ObservableList<Project> projectsbyID;
    private ObservableList<Project> allProjects;
    private ObservableList<Project> allProjectsWitExtraData;
    private static BLLManager bll;
    private static ProjektModel model = null;

    public static ProjektModel getInstance() throws DALException, SQLException {
        if (model == null) {
            model = new ProjektModel();
        }
        return model;
    }

    public ProjektModel() throws DALException, SQLException {
        bll = BLLManager.getInstance();
        allProjects = FXCollections.observableArrayList();
        allProjects.addAll(bll.getProjects()); // kan fjernes da den alligevel kaldes ved getProjects? - Troels
        allProjectsWitExtraData = FXCollections.observableArrayList();
        projectsbyID = FXCollections.observableArrayList();

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
     *
     * @param projectID
     * @throws DALException
     */
    public void deleteProject(int projectID) throws DALException {
        bll.deleteProject(projectID);
    }

    /**
     * Sender det info fra MainControllerens "editProject" videre til DAL laget
     *
     * @param clientID
     * @param projectName
     * @param hourlyPay
     * @param projectID
     * @throws timetracker.DAL.DALException
     */
    public void editProject(int clientID, String projectName, int hourlyPay, int projectID) throws DALException {
        bll.editProject(clientID, projectName, hourlyPay, projectID);
    }

    /**
     * retunere en liste af alle projekter.
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public ObservableList<Project> getProjects() throws DALException, SQLException {
        allProjects.clear();
        allProjects.addAll(bll.getProjects());
        Comparator<Project> byName = (Project cl1, Project cl2) -> cl1.getProjectName().compareTo(cl2.getProjectName());
        allProjects.sort(byName);

        return allProjects;
    }
    
    // Get the existing list of projects without reloading from database
    public ObservableList<Project> getProjectsCache() {
        return allProjects;
    }

    /**
     * Returnere en liste af projects hvor person_id har lavet tasks på
     * @param person_id
     * @return
     * @throws DALException 
     */
    public ObservableList<Project> getProjectsbyID(int person_id) throws DALException {
        projectsbyID.clear();
        projectsbyID.addAll(bll.getProjectsbyID(person_id));
        return projectsbyID;
    }

    public ObservableList<Project> getProjectsWithExtraData() throws DALException {
        allProjectsWitExtraData.clear();
        allProjectsWitExtraData.addAll(bll.getProjectsWithExtradata());
        return allProjectsWitExtraData;
    }

    
      
    public ObservableList<Project> getProjectsToFilter(User comboUser, Client comboClient, String fradato, String tildato, String monthStart, String monthEnd ) throws DALException {
        allProjectsWitExtraData.clear();
        allProjectsWitExtraData.addAll(bll.getProjectsToFilter(comboUser ,comboClient, fradato, tildato, monthStart, monthEnd));
        return allProjectsWitExtraData;
    }

    
    
}
