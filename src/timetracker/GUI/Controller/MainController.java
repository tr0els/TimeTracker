/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import BLL.BLLManager;
import DAL.DALException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 *
 * @author Troels Klein
 */
public class MainController implements Initializable {
    
    @FXML
    private Label label;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void createProject() throws DALException{
        int clientID = 1;
        String projectName = "projekt 1";
        int hourlyPay = 200;
        
        BLLManager bll = new BLLManager();
        
        bll.createProjekt(clientID, projectName, hourlyPay);   
    }
    
    public void deleteProject() throws DALException{
        int clientID = 1;
        String projectName = "projekt 2";
        int hourlyPay = 200;
        
        BLLManager bll = new BLLManager();
        
        bll.deleteProject(clientID, projectName, hourlyPay);
    }
    
}
