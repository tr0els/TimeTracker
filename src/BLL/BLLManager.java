/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

import DAL.DALException;
import DAL.DALManager;

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
    
}
