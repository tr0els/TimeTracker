/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BLL;

import DAL.DALManager;

/**
 *
 * @author Draik
 */
public class BLLManager {

    public void createProjekt(String client, String projectName, int hourlyPay) {
        DALManager dal = new DALManager();
        
        dal.createProjekt(client, projectName, hourlyPay);
    }
    
}
