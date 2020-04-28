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
    
    private DALManager dal;
    
    public BLLManager() throws DALException{
        dal = new DALManager();
    }

    public void createProjekt(int clientID, String projectName, int hourlyPay) throws DALException {
        DALManager dal = new DALManager();
        
        dal.createProjekt(clientID, projectName, hourlyPay);
    }

    public void deleteProject(int clientID, String projectName, int hourlyPay) throws DALException {
        DALManager dal = new DALManager();
        
        dal.deleteProject(clientID, projectName, hourlyPay);
    }
    
    /**
     * opretter og starter en ny task
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
