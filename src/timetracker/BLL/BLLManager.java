/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BLL;

import timetracker.DAL.DALException;
import timetracker.DAL.DALManager;

/**
 *
 * @author Draik
 */
public class BLLManager {
    
    private static DALManager bll = null;
    
    private BLLManager() throws DALException{
        dal = new DALManager();
    }
    
        public static BLLManager getInstance(){
        if (bll == null){
            bll = new BLLManager;
        }
        return bll;
    }

    public void createProjekt(int clientID, String projectName, int hourlyPay) throws DALException {
        DALManager dal = new DALManager();
        
        dal.createProjekt(clientID, projectName, hourlyPay);
    }

    public void deleteProject(int clientID, String projectName, int hourlyPay) throws DALException {
        DALManager dal = new DALManager();
        
        dal.deleteProject(clientID, projectName, hourlyPay);
    }
    
}
