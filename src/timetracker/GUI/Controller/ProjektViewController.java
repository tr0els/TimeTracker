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
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.AnchorPane;
import timetracker.BE.Project;
import timetracker.BE.Task;
import timetracker.BE.Task.Log;
import timetracker.DAL.DALException;
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
    @FXML
    private TreeTableColumn<Task, Log> colObject;

    private int person_id;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            model = TaskModel.getInstance();

        } catch (DALException | SQLException ex) {
            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
        }
        person_id = 1;
        showProjects();
        taskListener();
        projectListener();

    }

    /**
     * henter en liste over projekter og smider dem i vores combobox
     */
    public void showProjects() {
        projectMenubox.setItems(model.getProjectsbyID(person_id));
    }

    /**
     * henter en liste af task udfra et project_id og smider dem i et listview
     *
     * @param project_id
     */
    public void showTaskbyId(int project_id) {

        listTaskbyId.setItems(model.getTaskbyIDs(project_id, person_id));
    }

    /**
     * henter en liste af logs udfra et task_id og smider dem i et listview
     *
     * @param task_id
     */
    public void showTaskLogById(int task_id) {

        listTasklogbyId.setItems(model.getLogsbyID(task_id));

    }

    /**
     * en listener på vores combobox med projekter, den smider en liste af task
     * ind i et listview baseret på hvilket projekt der er valgt
     */
    public void projectListener() {
        projectMenubox.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Project> observable, Project oldValue, Project newValue) -> {
            if (newValue != null) {
                listTasklogbyId.getItems().clear();
                lblTasktotalTid.setText("");
                lblProjecttotalTid.setText(newValue.getTotal_tid());
                showTaskbyId(newValue.getProject_id());

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
                lblTasktotalTid.setText(newValue.getTotal_tid());
                showTaskLogById(newValue.getTask_id());

            }
        });
    }

}
