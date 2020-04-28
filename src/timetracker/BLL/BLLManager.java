/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BLL;

import timetracker.BE.Task;
import timetracker.DAL.DALException;
import timetracker.DAL.DALManager;

/**
 *
 * @author Draik
 */
public class BLLManager {

    /**
     * Singleton opsætning af vores BLLManager. singleton gør at vores bllmanager ikke vil
     * blive instansieret mere end en gang.
     */
    private static DALManager dal;
    private static BLLManager bll = null;

    private BLLManager() throws DALException {
        dal = DALManager.getInstance();
    }

    public static BLLManager getInstance() throws DALException {
        if (bll == null) {
            bll = new BLLManager();
        }
        return bll;
    }

    /**
     * Sender det info fra TaskModel "createProject" videre til DAL laget
     * @param clientID
     * @param projectName
     * @param hourlyPay
     * @throws DALException 
     */
    public void createProject(int clientID, String projectName, int hourlyPay) throws DALException {
        dal.createProject(clientID, projectName, hourlyPay);
    }

    /**
     * Sender det info fra TaskModel "deleteProject" videre til DAL laget
     * @param clientID
     * @param projectName
     * @param hourlyPay
     * @throws DALException 
     */
    public void deleteProject(int clientID, String projectName, int hourlyPay) throws DALException {
        dal.deleteProject(clientID, projectName, hourlyPay);
    }
    
    /**
     * opretter og starter en ny task og sender task object retur
     * @param task_name
     * @param billable
     * @param project_id
     * @param person_id
     * @return 
     */
    public Task createTask(String task_name, boolean billable, int project_id, int person_id)
    {
        return dal.createTask(task_name, billable, project_id, person_id);
    }
    
    /**
     * starter ny tidstagning på eksisterende task
     * @param task_id 
     */
    public void startTask(int task_id){
        dal.startTask(task_id);
    }
    
    /**
     * pauser/stopper en aktiv task
     * @param task_id 
     */
    public void pauseTask(int task_id){
        dal.pauseTask(task_id);
        
    }

}
