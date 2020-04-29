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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.layout.AnchorPane;

import timetracker.DAL.DALException;
import timetracker.GUI.Model.TaskModel;


/**
 * FXML Controller class
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class TaskController implements Initializable {

    @FXML
    private AnchorPane root;

    private TaskModel model;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {


        try {
            model = TaskModel.getInstance();
        } catch (DALException ex) {
            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    @FXML
    private void handleButtonAction(ActionEvent event) {
    }
    
    /**
     * Håndtere start og oprette task knappen action
     * @param event 
     */
    @FXML
    private void handleCreateTask(ActionEvent event)
    {
        createTask();
        
    }
    
     /**
     * Håndtere start eksisterende task knappen action
     * @param event 
     */
    @FXML
    private void handleStartTask(ActionEvent event)
    {
        startTask();
        
    }
    
     /**
     * Håndtere pause igangværende task knappen action
     * @param event 
     */
    @FXML
    private void handlePauseTask(ActionEvent event)
    {
        pauseTask();
        
    }
    
    /**
     * Tager de relevante informationer fra GUI og sender videre.
     */
    public void createTask()
    {
        String task_name = "Får endnu flere grå hår";
        boolean billable = true;
        int project_id = 9;
        int person_id = 1;
        
        model.createTask(task_name, billable, project_id, person_id);
	}
        
   

    public void startTask()
    {
        int task_id = 4;
        model.startTask(task_id);
    }
    
    public void pauseTask()
    {
        int task_id = 4;
        model.pauseTask(task_id);
    }
    
}
