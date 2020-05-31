/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import timetracker.BE.Project;
import timetracker.BE.TaskBase;
import timetracker.BE.TaskChild;
import timetracker.BE.TaskGroup;
import timetracker.BE.TaskParent;
import timetracker.DAL.DALException;
import timetracker.GUI.Model.UserModel;
import timetracker.GUI.Model.ChangelogModel;
import timetracker.GUI.Model.ProjectModel;
import timetracker.GUI.Model.TaskModel;

/**
 * FXML Controller class
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class TaskController implements Initializable {

    private TaskModel tModel;
    private ProjectModel pModel;
    private UserModel uModel;
    private ChangelogModel cModel;
    private ObservableList<Project> projects = FXCollections.observableArrayList();
    private List<TaskGroup> tasks;
    private int personId;    
    private TaskChild currentlySelectedTask;

    private Image imgPlay;
    private Image imgPause;
    private Image imgBillable;
    private Image imgNotBillable;
    private Image imgEdit;
    private int timerSecondsv = 0;
    private int timerMinutesv = 0;
    private int timerHoursv = 0;
    private boolean timerState = false;

    @FXML
    private JFXTextField textTaskname;
    @FXML
    private JFXCheckBox checkBillable;
    @FXML
    private JFXComboBox<Project> taskProject;
    @FXML
    private Label timerHours;
    @FXML
    private Label timerMinutes;
    @FXML
    private Label timerSeconds;
    @FXML
    private JFXButton timerButton;
    @FXML
    private JFXDrawer drawerEditTask;
    @FXML
    private AnchorPane paneEditTask;
    @FXML
    private ScrollPane taskScrollPane;
    @FXML
    private JFXTextField editName;
    @FXML
    private JFXCheckBox editBillable;
    @FXML
    private JFXComboBox<Project> editProject;
    @FXML
    private JFXButton btnEdit;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private JFXDatePicker editDateTo;
    @FXML
    private JFXTimePicker editTimeTo;
    @FXML
    private JFXDatePicker editDateFrom;
    @FXML
    private JFXTimePicker editTimeFrom;
    @FXML
    private Label lblWarning;

    public TaskController() throws DALException, SQLException {
        tModel = TaskModel.getInstance();
        pModel = ProjectModel.getInstance();
        uModel = UserModel.getInstance();
        cModel = ChangelogModel.getInstance();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // get id of logged in user
            personId = 1; // uModel.getUser().getPersonId();
            
            // get list of all projects
            projects.addAll(pModel.getProjects());
            
            // add projects to comboboxes
            taskProject.setItems(projects);
            editProject.setItems(projects);           
            
            // setup edit task menu
            drawerEditTask.setSidePane(paneEditTask);

            // show users tasks grouped by date
            setTasksGroupedByDate();

        } catch (DALException | SQLException ex) {
            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    /**
     * Starter en ny task
     */
    public void startTask() throws DALException {
        String name = textTaskname.getText();
        boolean billable = checkBillable.isSelected();
        int projectId = taskProject.getSelectionModel().getSelectedItem().getProjectId();

        tModel.startTask(name, billable, projectId, personId);
    }
    
    /**
     * Starter en ny task baseret på en eksisterende task
     */
    public void continueTask(TaskChild taskChild) throws DALException, SQLException {
        textTaskname.setText(taskChild.getName());
        
        if(taskChild.isBillable()) {
            checkBillable.setSelected(true);
        } else {
            checkBillable.setSelected(false);
        }
        
        for (Project p : projects) {
            if (p.getProjectId() == taskChild.getProjectId()) {
                taskProject.getSelectionModel().select(p);
            }
        }
        
        stopWatch();
    }

    /**
     * stop task via task_id
     */
    public void stopTask() throws DALException {
        tModel.stopTask(personId);
    }

    /**
     * Sætter tooltip event ved mouseover som giver info omkring Billable
     *
     * @param event
     */
    @FXML
    private void HandleTooltipForBillable(MouseEvent event) {
        Tooltip tip = new Tooltip();

        tip.setText("Vælg om en Opgave skal være 'Billable' eller ej");
        checkBillable.setTooltip(tip);
    }

    /**
     * Starter eller stopper task og det tilhørende stopur
     */
    @FXML
    private void stopWatch() throws DALException, SQLException {
        // start
        if (timerState == false) {
            startTask();
            
            timerState = true;
            timerSecondsv = 0;
            timerMinutesv = 0;
            timerHoursv = 0;
            timerButton.setText("Stop");
            timerButton.getStyleClass().clear();
            timerButton.getStyleClass().add("stopButton");

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (timerState) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (timerSecondsv >= 60) {
                                        timerSecondsv = 0;
                                        timerMinutesv++;
                                    }
                                    if (timerMinutesv >= 60) {
                                        timerSecondsv = 0;
                                        timerMinutesv = 0;
                                        timerHoursv++;
                                    }
                                    if (timerSecondsv < 10) {
                                        timerSeconds.setText("0" + timerSecondsv + "");
                                    } else {
                                        timerSeconds.setText(timerSecondsv + "");
                                    }
                                    if (timerMinutesv < 10) {
                                        timerMinutes.setText("0" + timerMinutesv + ":");
                                    } else {
                                        timerMinutes.setText(timerMinutesv + ":");
                                    }
                                    if (timerHoursv < 10) {
                                        timerHours.setText("0" + timerHoursv + ":");
                                    } else {
                                        timerHours.setText(timerHoursv + ":");
                                    }
                                    timerSecondsv++;
                                } catch (Exception e) {
                                }
                            }
                        });
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            break;
                        }
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        } else {
            // stop stopur og bil
            stopTask();
            timerState = false;
            timerButton.setText("Start");
            timerButton.getStyleClass().clear();
            timerButton.getStyleClass().add("startButton");
            textTaskname.setText("Hvad arbejder du på?");
            checkBillable.setSelected(true);
            taskProject.getSelectionModel().clearSelection();
            timerSeconds.setText("00");
            timerMinutes.setText("00");
            timerHours.setText("00");
            
            // reload task view
            setTasksGroupedByDate();
        }
    }

    /**
     *
     * @throws DALException
     * @throws SQLException
     */
    public void setTasksGroupedByDate() throws DALException, SQLException {

        // Get users tasks grouped by date
        tasks = tModel.getTasksGroupedByDate(1, "DATE", true, true);

        // Build task view
        Pane taskPane = getTaskView();

        // Put task view in scrollpane
        taskScrollPane.setContent(taskPane);
    }

    /**
     *
     * @return
     */
    public Pane getTaskView() {
        // pane to wrap the final result in
        Pane taskPane = new Pane();

        // vbox to keep nodes below each other
        VBox wrapper = new VBox();
        wrapper.getStyleClass().add("wrapper");
        taskPane.getChildren().add(wrapper);

        for (TaskGroup taskGroup : tasks) {

            // build nodes for each task group
            HBox groupNode = buildTaskGroup(taskGroup);
            wrapper.getChildren().add(groupNode);

            // build nodes for each parent (including any children)
            for (TaskParent taskParent : taskGroup.getParents()) {
                TitledPane taskNode = buildTask(taskParent);
                wrapper.getChildren().add(taskNode);
            }

            // add spacing between groups here
            Pane spacer = new Pane();
            spacer.getStyleClass().add("spacer");
            wrapper.getChildren().add(spacer);
        }

        return taskPane;
    }

    private HBox buildTaskGroup(TaskGroup taskGroup) {

        // hbox
        HBox groupNode = new HBox();
        groupNode.getStyleClass().add("group");

        // name
        Label name = new Label(taskGroup.getName());
        name.getStyleClass().add("name");
        groupNode.getChildren().add(name);

        // hack to use 100% width
        Pane filler = new Pane();
        groupNode.setHgrow(filler, Priority.ALWAYS);
        name.getStyleClass().add("name");
        groupNode.getChildren().add(filler);

        // total
        Label total = new Label("Total:  ");
        total.getStyleClass().add("total");
        groupNode.getChildren().add(total);

        // time
        Label time = new Label(taskGroup.getTime());
        time.getStyleClass().add("time");
        groupNode.getChildren().add(time);

        return groupNode;
    }

    private TitledPane buildTask(TaskBase taskBase) {

        // titledpane
        TitledPane parentNode = new TitledPane();
        parentNode.setCollapsible(false);

        // hbox wrapper
        HBox hbox = new HBox();

        // name + num children wrapper
        HBox hboxName = new HBox();
        hboxName.getStyleClass().add("name");

        // show the parents number of children
        if (taskBase instanceof TaskParent && ((TaskParent)taskBase).getChildren().size() > 1) {
            Label numChildren = new Label(((TaskParent)taskBase).getChildren().size() + "");

            numChildren.getStyleClass().add("numChildren");
            hboxName.getChildren().add(numChildren);
            parentNode.setExpanded(false);
            parentNode.setCollapsible(true);
        }

        // name
        Label name = new Label(taskBase.getName());
        name.setTooltip(new Tooltip(taskBase.getName()));
        name.getStyleClass().add("name");
        hboxName.getChildren().add(name);
        hbox.getChildren().add(hboxName);

        // project
        Label project = new Label();

        for (Project p : projects) {
            String projectText = p.getProjectName() + " (" + p.getClientName() + ")";

            if (p.getProjectId() == taskBase.getProjectId()) {
                project.setText(projectText);
                project.setTooltip(new Tooltip(projectText));
            }
        }

        project.getStyleClass().add("project");
        hbox.getChildren().add(project);

        // billable
        Label billable = new Label("$");
        billable.getStyleClass().add("billable");

        if (taskBase.isBillable()) {
            billable.getStyleClass().add("true");
        } else {
            billable.getStyleClass().add("false");
        }
        hbox.getChildren().add(billable);

        // start
        Label start = new Label(taskBase.getStart().format(DateTimeFormatter.ofPattern("HH:mm")));
        start.getStyleClass().add("start");
        hbox.getChildren().add(start);

        // dash
        hbox.getChildren().add(new Label("-"));

        // end
        Label end = new Label(taskBase.getEnd().format(DateTimeFormatter.ofPattern("HH:mm")));
        end.getStyleClass().add("end");
        hbox.getChildren().add(end);

        // time
        Label time = new Label(taskBase.getTime());
        time.getStyleClass().add("time");
        hbox.getChildren().add(time);

        // edit
        Button edit = new Button();
        
        if (taskBase instanceof TaskParent && ((TaskParent)taskBase).getChildren().size() > 1) {
            edit.getStyleClass().add("editDisabled");
            edit.setDisable(true);
        } else {
            edit.getStyleClass().add("edit");
            edit.setOnAction(e -> {
                // if task parent with 1 child use that child
                if(taskBase instanceof TaskParent && ((TaskParent)taskBase).getChildren().size() == 1) {
                    currentlySelectedTask = ((TaskParent)taskBase).getChildren().get(0);
                // task must be child
                } else {
                    currentlySelectedTask = (TaskChild)taskBase;                    
                }
                drawerSelectTaskForEditing();
            });
        }        
        
        hbox.getChildren().add(edit);

        // continue timer button
        Button continueTimer = new Button();
        continueTimer.getStyleClass().add("continueTimer");
        continueTimer.setOnAction(e -> {
            
                // if task parent with 1 child use that child
                if(taskBase instanceof TaskParent && ((TaskParent)taskBase).getChildren().size() >= 1) {
                    try {
                        continueTask(((TaskParent)taskBase).getChildren().get(0));
                        // task must be child
                    } catch (DALException | SQLException ex) {
                        Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {                    
                        continueTask(((TaskChild)taskBase));
                    } catch (DALException | SQLException ex) {
                        Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        hbox.getChildren().add(continueTimer);


        // stack each child under the parents content
        VBox childrenWrapper = new VBox();

        if (taskBase instanceof TaskParent && ((TaskParent)taskBase).getChildren().size() > 1) {
            for (TaskChild taskChild : ((TaskParent)taskBase).getChildren()) {
                childrenWrapper.getChildren().add(buildTask(taskChild));
            }
        }

        parentNode.setContent(childrenWrapper);

        // add hbox to titledpane
        parentNode.setGraphic(hbox);

        return parentNode;
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        drawerClose();
    }
    
    /**
     * henter den valgte task og indsætter værdierne i edit view.
     *
     * @param task_id
     */
    public void drawerSelectTaskForEditing() {
        
        // set values for edit field
        editName.setText(currentlySelectedTask.getName());
        editBillable.setSelected(currentlySelectedTask.isBillable());
        editDateFrom.setValue(currentlySelectedTask.getStart().toLocalDate());
        editTimeFrom.setValue(currentlySelectedTask.getStart().toLocalTime());
        editDateTo.setValue(currentlySelectedTask.getEnd().toLocalDate());
        editTimeTo.setValue(currentlySelectedTask.getEnd().toLocalTime());

        for (Project project : projects) {
            if (currentlySelectedTask.getProjectId() == project.getProjectId()) {
                editProject.getSelectionModel().select(project);
            }            
        }

        // make sure any warnings are reset
        lblWarning.setText("");

        drawerOpen();
    }
    
    /**
     * opdatere vores task der skal redigeres med de relevante data og sender
     * den afsted for at blive opdateret i db
     *
     * @param event
     */
    @FXML
    private void handleUpdateTask(ActionEvent event) {

        // convert task date and time
        LocalDateTime editTaskStart = LocalDateTime.of(editDateFrom.getValue(), editTimeFrom.getValue());
        LocalDateTime editTaskEnd = LocalDateTime.of(editDateTo.getValue(), editTimeTo.getValue());

        // validate edited time
        if (editTaskStart.compareTo(editTaskEnd) == 1) {
            lblWarning.setText("til-tid kan ikke være før fra-tid!");
        } else { 

            // update task with edited values
            currentlySelectedTask.setStart(LocalDateTime.of(editDateFrom.getValue(), editTimeFrom.getValue()));
            currentlySelectedTask.setEnd(LocalDateTime.of(editDateTo.getValue(), editTimeTo.getValue()));
            currentlySelectedTask.setName(editName.getText());
            currentlySelectedTask.setBillable(editBillable.isSelected());
            currentlySelectedTask.setProjectId(editProject.getSelectionModel().getSelectedItem().getProjectId());
            
            try {
                // log changes to task
                cModel.changelogTask(currentlySelectedTask.getId(), personId);
                
                // update task in database with edited values
                tModel.updateTask(currentlySelectedTask);    

                // reload task view to show changes
                setTasksGroupedByDate();
            } catch (SQLException | DALException ex) {
                Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            drawerClose();
        }
    }
    
    public void drawerOpen() {
        drawerEditTask.toFront();
        drawerEditTask.open();
    }
    
    public void drawerClose() {
        drawerEditTask.close();
        drawerEditTask.toBack();        
    }
}
