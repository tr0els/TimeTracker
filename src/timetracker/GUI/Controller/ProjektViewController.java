/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import timetracker.BE.Project;
import timetracker.BE.Task;
import timetracker.BE.Task.Log;
import timetracker.DAL.DALException;
import timetracker.GUI.Model.ProjektModel;
import timetracker.GUI.Model.TaskModel;

/**
 * FXML Controller class
 *
 * @author Charlotte
 */
public class ProjektViewController implements Initializable {

    @FXML
    private AnchorPane root;

    private TaskModel model;
    private ProjektModel Pmodel;
    @FXML
    private JFXComboBox<Project> projectMenubox;
    @FXML
    private JFXListView<Task> listTaskbyId;
    @FXML
    private JFXListView<Log> listTasklogbyId;

    @FXML
    private Label lblTasktotalTid;
    @FXML
    private Label lblProjecttotalTid;

    private int person_id;
    @FXML
    private TreeView<String> treeView;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
          
                model = TaskModel.getInstance();
                Pmodel = ProjektModel.getInstance();
                  person_id = 1;
            showProjects();
            taskListener();
            projectListener();
            createTree();
            } catch (DALException | SQLException ex) {
                Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
            }
          
            
       

    }

    
    public void createTree() throws DALException{
    
        TreeItem root = new TreeItem("Tasks");

        
        for (int i = 0; i < model.getTaskbyIDs(9, person_id).size(); i++) {
            TreeItem task = new TreeItem<Task>(model.getTaskbyIDs(9, person_id).get(i));
            root.getChildren().add(task);
                        
            int task_id = model.getTaskbyIDs(9, person_id).get(i).getTask_id();
            for (int j = 0; j < model.getLogsbyID(task_id).size(); j++) {
            TreeItem log = new TreeItem<Log> (model.getLogsbyID(task_id).get(j));
            task.getChildren().add(log);
                
            }
            
            
        }
        
        treeView.setRoot(root);
        treeView.setShowRoot(false);
    }
    
    
    /**
     * henter en liste over projekter og smider dem i vores combobox
     */
    public void showProjects() throws DALException {
        projectMenubox.setItems(Pmodel.getProjectsbyID(person_id));
    }

    /**
     * henter en liste af task udfra et project_id og smider dem i et listview
     *
     * @param project_id
     */
    public void showTaskbyId(int project_id) throws DALException {

        listTaskbyId.setItems(model.getTaskbyIDs(project_id, person_id));
    }

    /**
     * henter en liste af logs udfra et task_id og smider dem i et listview
     *
     * @param task_id
     */
    public void showTaskLogById(int task_id) throws DALException {

        listTasklogbyId.setItems(model.getLogsbyID(task_id));

    }

    /**
     * en listener på vores combobox med projekter, den smider en liste af task
     * ind i et listview baseret på hvilket projekt der er valgt
     */
    public void projectListener() {
        projectMenubox.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Project> observable, Project oldValue, Project newValue) -> {
            if (newValue != null) {
                try {
                    listTasklogbyId.getItems().clear();
                    lblTasktotalTid.setText("");
                    lblProjecttotalTid.setText(newValue.getTotal_tid());
                    showTaskbyId(newValue.getProject_id());
                } catch (DALException ex) {
                    Logger.getLogger(ProjektViewController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }

    /**
     * en listener på vores listview med task, den smider logs ind i et andet
     * listview baseret på det valgte task.
     */
    public void taskListener() {
        listTaskbyId.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listTaskbyId.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Task> observable, Task oldValue, Task newValue) -> {

            if (newValue != null) {
                try {
                    lblTasktotalTid.setText(newValue.getTotal_tid());
                    showTaskLogById(newValue.getTask_id());
                } catch (DALException ex) {
                    Logger.getLogger(ProjektViewController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }

}
