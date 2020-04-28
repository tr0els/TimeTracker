/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Model;

import timetracker.BLL.BLLManager;
import timetracker.DAL.DALException;

/**
 *
 * @author Troels Klein
 */
public class TaskModel {

    /**
     * Singleton opsætning af vores model. singleton gør at vores model ikke vil
     * blive instansieret mere end en gang.
     */
    private static BLLManager bll;
    private static TaskModel model = null;

    private TaskModel() throws DALException {
        bll = BLLManager.getInstance();
    }

    public static TaskModel getInstance() throws DALException {
        if (model == null) {
            model = new TaskModel();
        }
        return model;
    }

    /**
     * Sender det info fra MainControllerens "createProject" videre til DAL laget
     * @param clientID
     * @param projectName
     * @param hourlyPay
     * @throws DALException 
     */
    public void createProject(int clientID, String projectName, int hourlyPay) throws DALException {
        bll.createProject(clientID, projectName, hourlyPay);
    }

    /**
     * Sender det info fra MainControllerens "deleteProject" videre til DAL laget
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
     * @param clientID
     * @param projectName
     * @param hourlyPay
     * @param projectID 
     */
    public void editProject(int clientID, String projectName, int hourlyPay, int projectID) {
        bll.editProject(clientID, projectName, hourlyPay, projectID);
    }

}
