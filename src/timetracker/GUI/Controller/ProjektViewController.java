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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
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
    private TreeTableColumn<Task, LocalDateTime> colTask_start;
    @FXML
    private TreeTableColumn<Task, LocalDateTime> colTask_end;
    @FXML
    private TreeTableColumn<Task, String> colTotal_time;
    @FXML
    private TreeTableColumn<Task, LocalDateTime> colLast_worked_on;
    @FXML
    private TreeTableColumn<Task, String> colBillable;

    private ObservableList<Project> projects;

    private Task edit_task;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    @FXML
    private JFXButton btnCancel;
    @FXML
    private VBox vboxTasks;
    @FXML
    private HBox hboxHeader;

    private ObservableList<Task> observablelogs;

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

            setHBox();

        } catch (DALException ex) {
            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ProjektViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setHBox() {

        Label arrow = new Label("#");
        arrow.setPrefWidth(25);
//        arrow.setMaxWidth(25);
        arrow.setAlignment(Pos.CENTER);

        Label taskname = new Label("Opgave navn");
        taskname.setPrefWidth(150);

        Label totaltime = new Label("Total tid");
        totaltime.setPrefWidth(120);

        Label lastworkedon = new Label("Sidst arbejdet på");
        lastworkedon.setPrefWidth(150);

        hboxHeader.getChildren().addAll(arrow, taskname, totaltime, lastworkedon);
    }

    /**
     * Laver treeTableview fra hashMap
     *
     * @param project_id
     * @throws DALException
     */
    public void createTree(int project_id) throws DALException {
        vboxTasks.getChildren().clear();
        for (Map.Entry<Task, List<Task>> entry : model.getTaskbyIDs(project_id, person_id).entrySet()) {
            Task hashTask = entry.getKey();

            HBox headerBox = new HBox();
            Label tasknameHead = new Label(hashTask.getTask_name());
            tasknameHead.setPrefWidth(150);

            Label totaltimeHead = new Label(hashTask.getTotal_tid());
            totaltimeHead.setPrefWidth(120);

            Label lastworkedonHead = new Label(formatter.format(hashTask.getLast_worked_on()));
            lastworkedonHead.setPrefWidth(150);

            headerBox.getChildren().addAll(tasknameHead, totaltimeHead, lastworkedonHead);

            List<Task> logs = entry.getValue();
            observablelogs = FXCollections.observableArrayList();
            observablelogs.addAll(logs);

            TableView tableView = new TableView<>(observablelogs);

            TableColumn<Task, LocalDateTime> starttime = new TableColumn("Start");
            starttime.setCellValueFactory(new PropertyValueFactory<>("start_time"));

            TableColumn<Task, LocalDateTime> endtime = new TableColumn("Slut");
            endtime.setCellValueFactory(new PropertyValueFactory<>("end_time"));

            TableColumn<Task, String> totaltime = new TableColumn("Total tid");
            totaltime.setCellValueFactory(new PropertyValueFactory<>("total_tid"));

            TableColumn<Task, String> billable = new TableColumn("Faktureres");
            billable.setCellValueFactory(new PropertyValueFactory<>("stringBillable"));

            TableColumn<Task, Integer> redigere = new TableColumn("Redigeres");
            redigere.setCellValueFactory(new PropertyValueFactory<>("task_id"));
            redigere.setCellFactory(param -> new TableCell<Task, Integer>() {
                private final JFXButton editbtn = new JFXButton("redigere");

                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText("");
                    } else {
                        setGraphic(editbtn);
                    }
                    editbtn.setOnAction(event -> editTask(item.intValue()));
                }
            });

            tableView.getColumns().addAll(starttime, endtime, totaltime, billable, redigere);
            tableView.setFixedCellSize(25);
            double tableHeight = observablelogs.size() * tableView.getFixedCellSize() + tableView.getFixedCellSize();
            tableView.setPrefHeight(tableHeight);

            TitledPane titledPane = new TitledPane();
            titledPane.setGraphic(headerBox);
            titledPane.setContent(tableView);
            titledPane.setExpanded(false);

            vboxTasks.getChildren().add(titledPane);

        }

//        TreeItem ttRoot = new TreeItem("Tasks");
//
//        for (Map.Entry<Task, List<Task>> entry : model.getTaskbyIDs(project_id, person_id).entrySet()) {
//            Task hashTask = entry.getKey();
//            TreeItem task = new TreeItem<Task>(hashTask);
//            ttRoot.getChildren().add(task);
//
//            List<Task> logs = entry.getValue();
//
//            for (int j = 0; j < logs.size(); j++) {
//                TreeItem<Task> log = new TreeItem<Task>(logs.get(j));
//                task.getChildren().add(log);
//
//            }
//            colTask_name.setCellValueFactory(new TreeItemPropertyValueFactory<>("task_name"));
//            colTotal_time.setCellValueFactory(new TreeItemPropertyValueFactory<>("total_tid"));
//
//            colLast_worked_on.setCellValueFactory(new TreeItemPropertyValueFactory<>("last_worked_on"));
//            colLast_worked_on.setCellFactory(column -> {
//                return new TreeTableCell<Task, LocalDateTime>() {
//
//                    @Override
//                    protected void updateItem(LocalDateTime item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (item == null || empty) {
//                            setText("");
//                        } else {
//                            setText(formatter.format(item));
//                        }
//                    }
//
//                };
//            });
//
//            colTask_start.setCellValueFactory(new TreeItemPropertyValueFactory<>("start_time"));
//            colTask_start.setCellFactory(column -> {
//                return new TreeTableCell<Task, LocalDateTime>() {
//
//                    @Override
//                    protected void updateItem(LocalDateTime item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (item == null || empty) {
//                            setText("");
//                        } else {
//                            setText(formatter.format(item));
//                        }
//                    }
//
//                };
//            });
//
//            colTask_end.setCellValueFactory(new TreeItemPropertyValueFactory<>("end_time"));
//            colTask_end.setCellFactory(column -> {
//                return new TreeTableCell<Task, LocalDateTime>() {
//
//                    @Override
//                    protected void updateItem(LocalDateTime item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (item == null || empty) {
//                            setText("");
//                        } else {
//                            setText(formatter.format(item));
//                        }
//                    }
//
//                };
//            });
//
//            colTotal_time.setCellValueFactory(new TreeItemPropertyValueFactory<>("total_tid"));
//            colBillable.setCellValueFactory(new TreeItemPropertyValueFactory<>("stringBillable"));
//            
//            treeTasks.getSortOrder().add(colLast_worked_on);
//            treeTasks.setRoot(ttRoot);
//            treeTasks.setShowRoot(false);
//
//        }
    }

    public void editTask(int task_id) {
        try {
            edit_task = model.getTaskbyID(task_id);

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

    /**
     * henter lister over projekter og smider dem i de rigtige menuer (combobox)
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

    /**
     * Åbner vores redigeringsvindue/skuffe og henter de relevante data ind i
     * deres felter
     *
     * @param event
     */
    @FXML
    private void handleOpenEdit(ActionEvent event) {

    }

    /**
     * opdatere vores task der skal redigeres med de relevante data og sender
     * den afsted for at blive opdateret i db
     *
     * @param event
     */
    @FXML
    private void handleEdit(ActionEvent event) {
        try {

            edit_task.setTask_name(txtTask_name.getText());
            edit_task.setBillable(chkboxBillable.isSelected());
            edit_task.setProject_id(menuEditProjects.getSelectionModel().getSelectedItem().getProject_id());

            model.updateTaskbyID(edit_task);

            int p_ID = menuEditProjects.getSelectionModel().getSelectedItem().getProject_id();
            String p_tid = menuEditProjects.getSelectionModel().getSelectedItem().getTotal_tid(); //henter gammel værdi! :(
            String p_navn = menuEditProjects.getSelectionModel().getSelectedItem().getProject_name();

            lblProjectTid.setText(p_tid);
            lblProjectnavn.setText(p_navn);

            createTree(p_ID);
            showProjects();
            
            skuffen.close();
            skuffen.toBack();
        } catch (DALException | SQLException ex) {
            Logger.getLogger(ProjektViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Lukker vores redigeringsvindue
     *
     * @param event
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        skuffen.close();
        skuffen.toBack();

    }

}
