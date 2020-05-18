/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import timetracker.BE.Project;
import timetracker.BE.Task;
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
    private Label lblProjectnavn;
    @FXML
    private Label lblProjectTid;
    @FXML
    private JFXButton btnEdit;
    @FXML
    private JFXDrawer skuffen;
    @FXML
    private JFXTextField txtTask_name;
    @FXML
    private JFXCheckBox chkboxBillable;
    @FXML
    private AnchorPane paneEdit;
    @FXML
    private JFXComboBox<Project> menuEditProjects;
    @FXML
    private TreeTableView<Task> treeTasks;
    @FXML
    private TreeTableColumn<Task, String> colTask_name;
    @FXML
    private TreeTableColumn<Task, String> colTask_start;
    @FXML
    private TreeTableColumn<Task, String> colTask_end;
    @FXML
    private TreeTableColumn<Task, String> colTotal_time;
    @FXML
    private TreeTableColumn<Task, String> colLast_worked_on;
    @FXML
    private TreeTableColumn<Task, String> colBillable;

    private ObservableList<Project> projects;

    private Task edit_task;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    @FXML
    private JFXButton btnCancel;

    public ProjektViewController() throws DALException, SQLException {

        model = TaskModel.getInstance();
        Pmodel = ProjektModel.getInstance();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            skuffen.setSidePane(paneEdit);
            skuffen.toBack();
            skuffen.close();

            person_id = 1;
            showProjects();
            projectListener();

        } catch (DALException ex) {
            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ProjektViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void createTree(int project_id) throws DALException {

        TreeItem ttRoot = new TreeItem("Tasks");

        for (Map.Entry<Task, List<Task>> entry : model.getTaskbyIDs(project_id, person_id).entrySet()) {
            Task hashTask = entry.getKey();
            TreeItem task = new TreeItem<Task>(hashTask);
            ttRoot.getChildren().add(task);

            List<Task> logs = entry.getValue();

            for (int j = 0; j < logs.size(); j++) {
                TreeItem<Task> log = new TreeItem<Task>(logs.get(j));
                task.getChildren().add(log);

            }
            colTask_name.setCellValueFactory(new TreeItemPropertyValueFactory<>("task_name"));
            colTotal_time.setCellValueFactory(new TreeItemPropertyValueFactory<>("total_tid"));
            colLast_worked_on.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Task, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Task, String> param) {

                    if (param.getValue().getValue().getLast_worked_on() == null) {
                        return new SimpleStringProperty("");
                    } else {
                        return new SimpleStringProperty(param.getValue().getValue().getLast_worked_on().format(formatter));
                    }
                }
            });

            colTask_start.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Task, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Task, String> param) {

                    if (param.getValue().getValue().getStart_time() == null) {
                        return new SimpleStringProperty("");
                    } else {
                        return new SimpleStringProperty(param.getValue().getValue().getStart_time().format(formatter));
                    }
                }
            });

            colTask_end.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Task, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Task, String> param) {

                    if (param.getValue().getValue().getEnd_time() == null) {
                        return new SimpleStringProperty("");
                    } else {
                        return new SimpleStringProperty(param.getValue().getValue().getEnd_time().format(formatter));
                    }
                }
            });
            colTotal_time.setCellValueFactory(new TreeItemPropertyValueFactory<>("total_tid"));
            colBillable.setCellValueFactory(new TreeItemPropertyValueFactory<>("stringBillable"));

            treeTasks.setRoot(ttRoot);
            treeTasks.setShowRoot(false);

        }
    }

    /**
     * henter en liste over projekter og smider dem i vores combobox
     */
    public void showProjects() throws DALException, SQLException {

        projectMenubox.setItems(Pmodel.getProjectsbyID(person_id));

        projects = Pmodel.getProjectsWithExtraData();
        menuEditProjects.setItems(projects);

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
    private void handleOpenEdit(ActionEvent event) {
        int task_id;

        try { // checker om der er valgt en log eller task, hvis det ikke er en log(leaf) så vælger den den føste log der er under den valgte task.
            if (!treeTasks.getSelectionModel().selectedItemProperty().getValue().isLeaf()) {
                task_id = treeTasks.getSelectionModel().getSelectedItem().getChildren().get(0).getValue().getTask_id();
                treeTasks.getSelectionModel().getSelectedItem().setExpanded(true);
                treeTasks.getSelectionModel().select(treeTasks.getSelectionModel().getSelectedItem().getChildren().get(0));
                edit_task = model.getTaskbyID(task_id);

            } else {
                task_id = treeTasks.getSelectionModel().getSelectedItem().getValue().getTask_id();
                edit_task = model.getTaskbyID(task_id);
            }

            txtTask_name.setText(edit_task.getTask_name());
            chkboxBillable.setSelected(edit_task.isBillable());

            for (int i = 0; i < projects.size(); i++) {

                if (edit_task.getProject_id() == projects.get(i).getProject_id()) {
                    menuEditProjects.getSelectionModel().select(projects.get(i));
                }

            }

        } catch (DALException ex) {
            Logger.getLogger(ProjektViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

        skuffen.open();
        skuffen.toFront();
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        try {

            edit_task.setTask_name(txtTask_name.getText());
            edit_task.setBillable(chkboxBillable.isSelected());
            edit_task.setProject_id(menuEditProjects.getSelectionModel().getSelectedItem().getProject_id());

            model.updateTaskbyID(edit_task);

            createTree(menuEditProjects.getSelectionModel().getSelectedItem().getProject_id());
            lblProjectTid.setText(menuEditProjects.getSelectionModel().getSelectedItem().getTotal_tid());
            lblProjectnavn.setText(menuEditProjects.getSelectionModel().getSelectedItem().getProject_name());

            projectMenubox.getSelectionModel().clearSelection();

            skuffen.close();
            skuffen.toBack();
        } catch (DALException ex) {
            Logger.getLogger(ProjektViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        skuffen.close();
        skuffen.toBack();

    }

}
