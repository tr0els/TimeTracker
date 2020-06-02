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
import java.time.Duration;
import java.time.LocalDate;
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
import javafx.scene.image.ImageView;
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
import timetracker.BLL.DateUtil;
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

    private int loggedInPersonId;
    private ObservableList<Project> projects = FXCollections.observableArrayList();
    private List<TaskGroup> tasks;
    private TaskChild currentlySelectedTask;

    private int timerElapsedtime;
    private boolean timerStarted = false;
    private Thread currentTimerThread;

    @FXML
    private JFXTextField txtTaskName;
    @FXML
    private JFXCheckBox chkTaskBillable;
    @FXML
    private JFXComboBox<Project> cbTaskProject;
    @FXML
    private Label timerText;
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

    /**
     * Constructor for TaskController
     *
     * @throws DALException
     * @throws SQLException
     */
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
            loggedInPersonId = uModel.getUser().getPersonId();

            // get list of all projects
            projects.addAll(pModel.getProjects());

            // add projects to comboboxes
            cbTaskProject.setItems(projects);
            editProject.setItems(projects);

            // setup edit task menu
            drawerEditTask.setSidePane(paneEditTask);

            // resume any started task
            resumeStartedTask();

            // show users tasks grouped by date
            setTasksGroupedByDate();

        } catch (DALException | SQLException ex) {
            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Gemmer den startede task i databasen
     */
    public void saveTaskToDatabase() throws DALException {
        String name = txtTaskName.getText();
        boolean billable = chkTaskBillable.isSelected();
        int projectId = cbTaskProject.getSelectionModel().getSelectedItem().getProjectId();

        tModel.startTask(name, billable, projectId, loggedInPersonId);
    }

    /**
     * Sætter ny opgave input felter baseret eksisterende task
     */
    public void setExistingTask(TaskChild taskChild) throws DALException, SQLException {
        txtTaskName.setText(taskChild.getName());

        if (taskChild.isBillable()) {
            chkTaskBillable.setSelected(true);
        } else {
            chkTaskBillable.setSelected(false);
        }

        for (Project p : projects) {
            if (p.getProjectId() == taskChild.getProjectId()) {
                cbTaskProject.getSelectionModel().select(p);
            }
        }
    }

    /**
     * Sæt sluttidspunkt i databasen for den startede opgave
     */
    public void updateTaskEndToDatabase() throws DALException {
        tModel.stopTask(loggedInPersonId);
    }

    /**
     * Starter en ny opgave med værdier fra inputfelterne
     */
    @FXML
    private void startTimerButton() throws DALException, SQLException {
        if (isTimerInputValid()) {
            saveTaskToDatabase();
            startTimer();
        }
    }

    /**
     * Stopper den ingangværende opgave
     */
    @FXML
    private void stopTimerButton() throws DALException, SQLException {
        updateTaskEndToDatabase();
        stopTimer();
    }

    /**
     * Validering af værdier i ny opgaves inputfelter
     */
    private boolean isTimerInputValid() {
        if (txtTaskName.getText().trim().isEmpty() || cbTaskProject.getSelectionModel().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Starter stopuret i en ny tråd og viser stop knap
     */
    @FXML
    private void startTimer() throws DALException, SQLException {

        // start timer
        timerStarted = true;

        // set stop button
        timerButton.setText("Stop");
        timerButton.getStyleClass().clear();
        timerButton.getStyleClass().add("stopButton");
        timerButton.setOnAction(e
                -> {
            try {
                stopTimerButton();
            } catch (DALException | SQLException ex) {
                Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        // start timer in new thread
        currentTimerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (timerStarted) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            timerElapsedtime++;
                            timerText.setText(getTimerElapsedTimeAsString());
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
        currentTimerThread.setDaemon(true);
        currentTimerThread.start();
    }

    /**
     * Stopper og nulstiller ingangværende stopur og viser startknap
     */
    private void stopTimer() throws DALException, SQLException {

        timerStarted = false;
        timerElapsedtime = 0;
        timerText.setText(getTimerElapsedTimeAsString());

        // set start button
        timerButton.setText("Start");
        timerButton.getStyleClass().clear();
        timerButton.getStyleClass().add("startButton");
        timerButton.setOnAction(e -> {
            try {
                startTimerButton();
            } catch (DALException | SQLException ex) {
                Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        txtTaskName.setText("");
        chkTaskBillable.setSelected(true);
        cbTaskProject.getSelectionModel().clearSelection();

        // reload task view
        setTasksGroupedByDate();
    }

    /**
     * Genoptager opgave hvis brugeren har en der er uafsluttet
     */
    private void resumeStartedTask() throws DALException, SQLException {

        TaskChild taskChild = null;
        taskChild = tModel.getStartedTask(loggedInPersonId);

        if (taskChild != null) {
            // set timer with offset based on the task start time
            Duration duration = Duration.between(taskChild.getStart(), LocalDateTime.now());
            timerElapsedtime = 0 + (int) duration.getSeconds();

            // resume task
            setExistingTask(taskChild);
            startTimer();
        }
    }

    /**
     * Visuel repræsentation af tiden der er gået for en opgave
     *
     * @return tiden der er gået som tekststreng
     */
    private String getTimerElapsedTimeAsString() {
        return String.format("%02d:%02d:%02d", timerElapsedtime / 3600, (timerElapsedtime % 3600) / 60, timerElapsedtime % 60);
    }

    /**
     * Henter og viser en liste af brugerens opgaver grupperet efter dato, med
     * samlet tidsforbrug for dage og opgaver. hver parent opgave har de opgaver
     * den består af grupperet under sig (stacked).
     */
    public void setTasksGroupedByDate() throws DALException, SQLException {

        // get users tasks grouped by date
        tasks = tModel.getTasksGroupedByDate(loggedInPersonId, "DATE", true, true);

        // if user has any tasks
        if (!tasks.isEmpty()) {

            // build task-view and show
            Pane taskPane = getTaskView();
            taskScrollPane.setContent(taskPane);
        } else {
            // build no-tasks-yet-view and show
            Pane taskPane = getNoTasksYetView();
            taskScrollPane.setContent(taskPane);
        }
    }

    /**
     * Bygger den visuelle del til visning af brugerens opgaver
     *
     * @return Pane indeholdende stackede opgaver
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

    /**
     * Bygger den visuelle del for dato (overskrift)
     *
     * @param taskGroup er opgave objektet der bruges
     * @return javafx node til brug i gui
     */
    private HBox buildTaskGroup(TaskGroup taskGroup) {

        // hbox
        HBox groupNode = new HBox();
        groupNode.getStyleClass().add("group");

        // name
        LocalDate date = LocalDate.parse(taskGroup.getName());
        Label name = new Label(DateUtil.formatDateConvenient(date));
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

    /**
     * Bygger den visuelle del for opgaver
     *
     * @param taskBase er parent eller child opgaven
     * @return javafx node til brug i gui
     */
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
        if (taskBase instanceof TaskParent && ((TaskParent) taskBase).getChildren().size() > 1) {
            Label numChildren = new Label(((TaskParent) taskBase).getChildren().size() + "");

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
            billable.setTooltip(new Tooltip("Faktureres"));
        } else {
            billable.getStyleClass().add("false");
            billable.setTooltip(new Tooltip("Faktureres ikke"));
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
        edit.setTooltip(new Tooltip("Rediger"));

        if (taskBase instanceof TaskParent && ((TaskParent) taskBase).getChildren().size() > 1) {
            edit.getStyleClass().add("editDisabled");
            edit.setDisable(true);
        } else {
            edit.getStyleClass().add("edit");
            edit.setOnAction(e -> {

                // if task parent with 1 child use that child
                if (taskBase instanceof TaskParent && ((TaskParent) taskBase).getChildren().size() == 1) {
                    currentlySelectedTask = ((TaskParent) taskBase).getChildren().get(0);

                    // task must be child
                } else {
                    currentlySelectedTask = (TaskChild) taskBase;
                }
                drawerSelectTaskForEditing();
            });
        }

        hbox.getChildren().add(edit);

        // continue timer button
        Button continueTimer = new Button();
        continueTimer.getStyleClass().add("continueTimer");

        continueTimer.setTooltip(new Tooltip("Fortsæt opgave"));
        continueTimer.setOnAction(e -> {
            // stop any tasks already started 
            if (timerStarted) {
                try {
                    stopTimerButton();
                } catch (DALException | SQLException ex) {
                    Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
                }
                currentTimerThread.interrupt();
            }

            // if task parent with 1 child use that child
            if (taskBase instanceof TaskParent && ((TaskParent) taskBase).getChildren().size() >= 1) {
                try {
                    setExistingTask(((TaskParent) taskBase).getChildren().get(0));
                    startTimerButton();
                } catch (DALException | SQLException ex) {
                    Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
                }
                // task must be child
            } else {
                try {
                    setExistingTask(((TaskChild) taskBase));
                    startTimerButton();
                } catch (DALException | SQLException ex) {
                    Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        hbox.getChildren().add(continueTimer);

        // stack each child under the parents content
        VBox childrenWrapper = new VBox();

        if (taskBase instanceof TaskParent && ((TaskParent) taskBase).getChildren().size() > 1) {
            for (TaskChild taskChild : ((TaskParent) taskBase).getChildren()) {
                childrenWrapper.getChildren().add(buildTask(taskChild));
            }
        }

        parentNode.setContent(childrenWrapper);

        // add hbox to titledpane
        parentNode.setGraphic(hbox);

        return parentNode;
    }

    /**
     * Visuel del der hjælper brugeren med at oprette første opgave
     *
     * @return javafx pane med billede
     */
    public Pane getNoTasksYetView() {

        Pane taskPane = new Pane();

        Image imgStartFirstTask = new Image(getClass().getResourceAsStream("/timetracker/GUI/Icons/start-first-task.png"));
        ImageView startFirstTask = new ImageView(imgStartFirstTask);
        taskPane.getChildren().add(startFirstTask);

        return taskPane;
    }

    /**
     * Afbryd redigering af opgave
     *
     * @param event
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        drawerClose();
    }

    /**
     * Viser redigering af den valgte opgave
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
     * Opdaterer ændringer i redigeret opgave i databasen, loggen og gui
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
            lblWarning.setText("fra tidspunkt er større end til!");
        } else {

            // update task with edited values
            currentlySelectedTask.setStart(LocalDateTime.of(editDateFrom.getValue(), editTimeFrom.getValue()));
            currentlySelectedTask.setEnd(LocalDateTime.of(editDateTo.getValue(), editTimeTo.getValue()));
            currentlySelectedTask.setName(editName.getText());
            currentlySelectedTask.setBillable(editBillable.isSelected());
            currentlySelectedTask.setProjectId(editProject.getSelectionModel().getSelectedItem().getProjectId());

            try {
                // log changes to task
                cModel.changelogTask(currentlySelectedTask.getId(), loggedInPersonId);

                // update task in database with edited values
                tModel.editTask(currentlySelectedTask);

                // reload task view to show changes
                setTasksGroupedByDate();
            } catch (SQLException | DALException ex) {
                Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
            }

            drawerClose();
        }
    }

    /**
     * Slider visning af rediger opgave frem
     */
    public void drawerOpen() {
        drawerEditTask.toFront();
        drawerEditTask.open();
    }

    /**
     * Slider visning af rediger opgave tilbage
     */
    public void drawerClose() {
        drawerEditTask.close();
        drawerEditTask.toBack();
    }
}
