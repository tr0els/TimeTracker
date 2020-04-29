/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import com.jfoenix.controls.JFXDrawer;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
   import timetracker.GUI.Model.TaskModel;


/**
 *
 * @author Troels Klein
 */
public class MainController implements Initializable {

   

    private TaskModel model;
    @FXML
    private AnchorPane root;
    private JFXDrawer drawer;

 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    setMenu();

    }
    
    public void setMenu(){
       try {
                
           AnchorPane pane = FXMLLoader.load(getClass().getResource("/timetracker/GUI/View/Menubar.fxml"));
           
           drawer.setSidePane(pane);
           drawer.open();
           
            
        } catch (IOException ex) {
            
        }
    
    } 
    
 }    
    
    

//    /**
//     * Tager det info som admin har puttet ind i "Projekt Manager" menuen
//     * og sender det ned for at blive gemt på serveren
//     * @throws DALException 
//     */
//    public void createProject() throws DALException {
//        int clientID = 1;
//        String projectName = "projekt 5";
//        int hourlyPay = 200;
//
//        model = TaskModel.getInstance();
//        model.createProject(clientID, projectName, hourlyPay);
//    }
//
//    /**
//     * Tager det projekt som Admin har valgt i "projekt Manager" menuen
//     * og sender det ned til DAL så det kan fjernes fra serveren.
//     * @throws DALException 
//     */
//    public void deleteProject() throws DALException {
//        int clientID = 1;
//        String projectName = "projekt 2";
//        int hourlyPay = 200;
//
//        model = TaskModel.getInstance();
//        model.deleteProject(clientID, projectName, hourlyPay);
//    }


