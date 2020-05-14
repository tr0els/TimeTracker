/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
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

    private int person_id;
    @FXML
    private TreeView<Log> treeView;
    @FXML
    private Label lblProjectnavn;
    @FXML
    private Label lblProjectTid;
    @FXML
    private JFXButton btnEdit;

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
            projectListener();

        } catch (DALException | SQLException ex) {
            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void createTree(int project_id) throws DALException {

        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isLeaf()) {
                treeView.getSelectionModel().select(newValue.getChildren().get(0));
            }
            if (newValue != null && newValue.isLeaf()) {
                treeView.getSelectionModel().select(newValue);
            }
            if (newValue != null && !newValue.isLeaf() && oldValue != null && oldValue.isLeaf()) {
                if (oldValue == newValue.getChildren().get(0)) {
                } else {
                    treeView.getSelectionModel().select(newValue.getChildren().get(0));
                }
            }
            
            
                        
        });

        TreeItem treeRoot = new TreeItem("Tasks");

        for (Map.Entry<Task, List<Log>> entry : model.getTaskbyIDs(project_id, person_id).entrySet()) {
            Task hashTask = entry.getKey();
            TreeItem task = new TreeItem<Task>(hashTask);
            treeRoot.getChildren().add(task);

            List<Log> logs = entry.getValue();

            for (int j = 0; j < logs.size(); j++) {
                TreeItem<Log> log = new TreeItem<Log>(logs.get(j));
                task.getChildren().add(log);

            }

        }

        treeView.setRoot(treeRoot);
        treeView.setShowRoot(false);
    }

    /**
     * henter en liste over projekter og smider dem i vores combobox
     */
    public void showProjects() throws DALException {
        projectMenubox.setItems(Pmodel.getProjectsbyID(person_id));
    }

    /**
     * en listener på vores combobox med projekter, den smider en liste af task
     * ind i et listview baseret på hvilket projekt der er valgt
     */
    public void projectListener() {
        projectMenubox.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Project> observable, Project oldValue, Project newValue) -> {
            if (newValue != null) {
                try {
                    lblProjectnavn.setText(newValue.getProject_name());
                    lblProjectTid.setText(newValue.getTotal_tid());
                    createTree(newValue.getProject_id());
                } catch (DALException ex) {
                    Logger.getLogger(ProjektViewController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }

    @FXML
    private void handleEditTask(ActionEvent event) {
        
        System.out.println(treeView.getSelectionModel().getSelectedItem().getValue().getTask_id());

    }

}
