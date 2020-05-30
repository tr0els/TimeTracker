/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.SQLException;
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
import timetracker.GUI.Model.BrugerModel;
import timetracker.GUI.Model.ChangelogModel;
import timetracker.GUI.Model.ProjektModel;
import timetracker.GUI.Model.TaskModel;

/**
 * FXML Controller class
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class TaskController implements Initializable {

    private TaskModel tModel;
    private ProjektModel pModel;
    private BrugerModel bModel;
    private ChangelogModel cModel;

    private int personId;
    private ObservableList<Project> projects = FXCollections.observableArrayList();
    private List<TaskGroup> tasks;
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
    private JFXComboBox<Project> comboListprojects;
    @FXML
    private JFXButton btnCreateTask;
    @FXML
    private AnchorPane paneToday;
    @FXML
    private AnchorPane paneYesterday;
    @FXML
    private Label timerHours;
    @FXML
    private Label timerMinutes;
    @FXML
    private Label timerSeconds;
    @FXML
    private JFXButton timerButton;
    @FXML
    private ScrollPane taskScrollPane;

    public TaskController() throws DALException, SQLException {
        tModel = TaskModel.getInstance();
        pModel = ProjektModel.getInstance();
        bModel = BrugerModel.getInstance();
        cModel = ChangelogModel.getInstance();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            personId = bModel.getUser().getPersonId();
            projects.addAll(pModel.getProjects());
            setTasksGroupedByDate();
        } catch (DALException | SQLException ex) {
            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
        }
        showProjects();
    }

    /**
     * henter en liste over projekter og smider dem i vores combobox
     */
    public void showProjects() {
        comboListprojects.setItems(projects);
    }

    /**
     * Tager de relevante informationer fra GUI og sender videre.
     */
    public void startTask() throws DALException {
        String task_name = textTaskname.getText(); //valideres og trimmes!
        boolean billable = checkBillable.isSelected();
        int project_id = comboListprojects.getSelectionModel().getSelectedItem().getProjectId();

        tModel.startTask(task_name, billable, project_id, personId);
        textTaskname.clear();
        checkBillable.setSelected(true);
        comboListprojects.getSelectionModel().clearSelection();
    }

    /**
     * stop task via task_id
     */
    public void stopTask() throws DALException {

        tModel.stopTask(personId);
    }

    @FXML
    private void HandleTooltipForBillable(MouseEvent event) {
        Tooltip tip = new Tooltip();

        tip.setText("Vælg om en Opgave skal være 'Billable' eller ej");
        checkBillable.setTooltip(tip);
    }

    @FXML
    private void handleStartStopTask(ActionEvent event) throws DALException {
        startTask();
        stopWatch();
    }

    private void stopWatch() {
        if (timerState == false) {
            timerState = true;
            timerSecondsv = 0;
            timerMinutesv = 0;
            timerHoursv = 0;
            timerButton.setText("Stop");
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
            timerState = false;

            timerButton.setText("Start");
        }
    }

    /**
     *
     * @throws DALException
     * @throws SQLException
     */
    public void setTasksGroupedByDate() throws DALException, SQLException {
        try {
            // Show data is loading
            taskScrollPane.setContent(getLoadingMessage());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Platform.runLater(() -> {
            try {
                // Get users tasks grouped by date
                tasks = tModel.getTasksGroupedByDate(1, "DATE", true, true);

                // Build task view
                Pane taskPane = getTaskView();

                // Put task view in scrollpane
                taskScrollPane.setContent(taskPane);
            } catch (DALException ex) {
                Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
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
        edit.getStyleClass().add("edit");
        hbox.getChildren().add(edit);
        
        // continue timer button
        Button continueTimer = new Button();
        continueTimer.getStyleClass().add("continueTimer");
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

    /**
     * Opdater ændringer på en task Parent ved at ændre alle stacked Children
     *
     * @param taskParent er den overordnede task med en liste af children
     */
    private void updateTask(TaskParent taskParent) {
        for (TaskChild taskChild : taskParent.getChildren()) {
            updateTask(taskChild);
        }
    }

    private void updateTask(TaskChild taskChild) {
        try {
            tModel.updateTask(taskChild);
            cModel.changelogTask(taskChild.getId(), personId);
            System.out.println("Updated");
            // update view
        } catch (DALException ex) {
            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Pane getLoadingMessage() throws FileNotFoundException {
        Pane loadingPane = new Pane();
        loadingPane.getStyleClass().add("loading");
        
        Label loadingText = new Label("Henter data...");

        //Image image = new Image(new FileInputStream("../Icons/loading.gif"));
        ///ImageView imageView = new ImageView(image);
        
        loadingPane.getChildren().add(loadingText);
        //loadingPane.getChildren().add(imageView);
        
        return loadingPane;
    }
}
