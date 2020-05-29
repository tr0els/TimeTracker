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
import java.net.URL;
import java.sql.SQLException;
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
public class TaskController implements Initializable
{

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
     * Henter en liste over projekter og smider dem i vores combobox
     */
    public void showProjects() {
        comboListprojects.setItems(projects);
    }

    /**
     * Tager de relevante informationer fra GUI og sender videre.
     */
    public void startTask() throws DALException
    {
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
    public void stopTask() throws DALException
    {

        tModel.stopTask(personId);
    }

    /**
     * Sætter tooltip event ved mouseover som giver info omkring Billable
     *
     * @param event
     */
    @FXML
    private void HandleTooltipForBillable(MouseEvent event)
    {
        Tooltip tip = new Tooltip();

        tip.setText("Vælg om en Opgave skal være 'Billable' eller ej");
        checkBillable.setTooltip(tip);
    }

    /**
     * Starter eller stopper task og det tilhørende stopur
     *
     * @param event
     * @throws DALException
     */
    @FXML
    private void handleStartStopTask(ActionEvent event) throws DALException
    {
        startTask();
        stopWatch();
    }

    /**
     * Stopur til at se hvor lang tid der er brugt på en task
     */
    private void stopWatch()
    {
        if (timerState == false)
        {
            timerState = true;
            timerSecondsv = 0;
            timerMinutesv = 0;
            timerHoursv = 0;
            timerButton.setText("Stop");
            Thread t = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    while (timerState)
                    {
                        Platform.runLater(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    if (timerSecondsv >= 60)
                                    {
                                        timerSecondsv = 0;
                                        timerMinutesv++;
                                    }
                                    if (timerMinutesv >= 60)
                                    {
                                        timerSecondsv = 0;
                                        timerMinutesv = 0;
                                        timerHoursv++;
                                    }
                                    if (timerSecondsv < 10)
                                    {
                                        timerSeconds.setText("0" + timerSecondsv + "");
                                    } else
                                    {
                                        timerSeconds.setText(timerSecondsv + "");
                                    }
                                    if (timerMinutesv < 10)
                                    {
                                        timerMinutes.setText("0" + timerMinutesv + ":");
                                    } else
                                    {
                                        timerMinutes.setText(timerMinutesv + ":");
                                    }
                                    if (timerHoursv < 10)
                                    {
                                        timerHours.setText("0" + timerHoursv + ":");
                                    } else
                                    {
                                        timerHours.setText(timerHoursv + ":");
                                    }
                                    timerSecondsv++;
                                } catch (Exception e)
                                {
                                }
                            }
                        });
                        try
                        {
                            Thread.sleep(1000);
                        } catch (Exception e)
                        {
                            break;
                        }
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        } else
        {
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
        
        // empty filler
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
    
    private TitledPane buildTask(TaskParent taskParent) {
        
        // titledpane
        TitledPane parentNode = new TitledPane();
        parentNode.setExpanded(false);
        
        // hbox wrapper
        HBox hbox = new HBox();
        
        // name + num children wrapper
        HBox hboxName = new HBox();
        hboxName.getStyleClass().add("name");

        // show number of children
        if(taskParent.getChildren().size() > 1) {
            Label numChildren = new Label(taskParent.getChildren().size() + "");
            numChildren.getStyleClass().add("numChildren");
            hboxName.getChildren().add(numChildren);
        }
        
        // name
        JFXTextField name = new JFXTextField(taskParent.getName());
        name.getStyleClass().add("name");
        hboxName.getChildren().add(name);
        hbox.getChildren().add(hboxName);

        // edit name on enter pressed
        name.setOnAction(e -> {
            if(!taskParent.getName().equals(name.getText())) {
                taskParent.setName(name.getText());
                UpdateTask(taskParent);
                System.out.println("name changed to: " + name.getText());
            }
        });
        
        // edit name on unfocus
        name.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal == false && !taskParent.getName().equals(name.getText())) {
                taskParent.setName(name.getText());
                UpdateTask(taskParent);                
                System.out.println("name changed to: " + name.getText());
            }
        });

        // project
        JFXComboBox<Project> project = new JFXComboBox();
        project.setItems(projects);
        
        // select the project in combobox that matches the tasks project id
        for (Project p : projects) {
            if(p.getProjectId() == taskParent.getProjectId()) {
                project.getSelectionModel().select(p);
            }
        }
                  
        project.getStyleClass().add("project");
        hbox.getChildren().add(project);
        
        // edit project
        project.setOnAction(e -> {
            int projectId = project.getSelectionModel().getSelectedItem().getProjectId();
            taskParent.setProjectId(projectId);
            UpdateTask(taskParent);
            System.out.println("project changed to: " + projectId);
        });
        
        // billable
        Label billable = new Label("$");
        billable.getStyleClass().add("billable");
        
        if(taskParent.isBillable()) {
            billable.getStyleClass().add("true");
        } else {
            billable.getStyleClass().add("false");
        }
        
        // edit billable
        billable.setOnMouseClicked(e -> {
            taskParent.setBillable(!taskParent.isBillable());
            //updateTaskBillable(taskParent);
            System.out.println("Task billable is: " + taskParent.isBillable());
            
            billable.getStyleClass().clear();
            billable.getStyleClass().add("billable");
            if(taskParent.isBillable()) {
                billable.getStyleClass().add("true");
            } else {
                billable.getStyleClass().add("false");
            }
        });        
        
        hbox.getChildren().add(billable);
        
        // start
        JFXTextField start = new JFXTextField(taskParent.getStart().toLocalTime().toString());
        start.getStyleClass().add("start");
        hbox.getChildren().add(start);
        
        // dash
        hbox.getChildren().add(new Label("-"));
        
        // end
        JFXTextField end = new JFXTextField(taskParent.getEnd().toLocalTime().toString());
        end.getStyleClass().add("end");
        hbox.getChildren().add(end);
        
        // datepicker
        //hbox.getChildren().add(new JFXDatePicker());
     
        // time
        Label time = new Label(taskParent.getTime());
        time.getStyleClass().add("time");
        hbox.getChildren().add(time);
        
        // continue timer button
        Button continueTimer = new Button();
        continueTimer.getStyleClass().add("continueTimer");
        hbox.getChildren().add(continueTimer);
              
        // stack each child under the parents content
        VBox childrenWrapper = new VBox();
  
        if(taskParent.getChildren().size() > 1) {
            for (TaskChild taskChild : taskParent.getChildren()) {
                childrenWrapper.getChildren().add(buildTask(taskChild));
            }
        }
       
        parentNode.setContent(childrenWrapper);
        
        // add hbox to titledpane
        parentNode.setGraphic(hbox);

        return parentNode;
    }

     private TitledPane buildTask(TaskChild taskChild) {

        // titledpane
        TitledPane childNode = new TitledPane();
        
        // hbox wrapper
        HBox hbox = new HBox();
        
        // name + num children wrapper
        HBox hboxName = new HBox();
        hboxName.getStyleClass().add("name");

        // name
        JFXTextField name = new JFXTextField(taskChild.getName());
        name.getStyleClass().add("name");
        hboxName.getChildren().add(name);
        hbox.getChildren().add(hboxName);

        // edit name on enter pressed
        name.setOnAction(e -> {
            if(!taskChild.getName().equals(name.getText())) {
                taskChild.setName(name.getText());
                //updateTaskName(taskParent);
                System.out.println("name changed to: " + name.getText());
            }
        });
        
        // edit name on unfocus
        name.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal == false && !taskChild.getName().equals(name.getText())) {
                taskChild.setName(name.getText());
                //updateTaskName(taskParent);
                System.out.println("name changed to: " + name.getText());
            }
        });

        // project
        JFXComboBox<Project> project = new JFXComboBox();
        project.setItems(projects);
        
        // select the project in combobox that matches the tasks project id
        for (Project p : projects) {
            if(p.getProjectId() == taskChild.getProjectId()) {
                project.getSelectionModel().select(p);
            }
        }
                  
        project.getStyleClass().add("project");
        hbox.getChildren().add(project);
        
        // edit project
        project.setOnAction(e -> {
            int projectId = project.getSelectionModel().getSelectedItem().getProjectId();
            taskChild.setProjectId(projectId);
            //updateTaskProject(taskParent);
            System.out.println("project changed to: " + projectId);
        });
        
        // billable
        Label billable = new Label("$");
        billable.getStyleClass().add("billable");
        
        if(taskChild.isBillable()) {
            billable.getStyleClass().add("true");
        } else {
            billable.getStyleClass().add("false");
        }
        
        // edit billable
        billable.setOnMouseClicked(e -> {
            taskChild.setBillable(!taskChild.isBillable());
            //updateTaskBillable(taskParent);
            System.out.println("Task billable is: " + taskChild.isBillable());
            
            billable.getStyleClass().clear();
            billable.getStyleClass().add("billable");
            if(taskChild.isBillable()) {
                billable.getStyleClass().add("true");
            } else {
                billable.getStyleClass().add("false");
            }
        });        
        
        hbox.getChildren().add(billable);
        
        // start
        JFXTextField start = new JFXTextField(taskChild.getStart().toLocalTime().toString());
        start.getStyleClass().add("start");
        hbox.getChildren().add(start);
        
        // dash
        hbox.getChildren().add(new Label("-"));
        
        // end
        JFXTextField end = new JFXTextField(taskChild.getEnd().toLocalTime().toString());
        end.getStyleClass().add("end");
        hbox.getChildren().add(end);
        
        // datepicker
        //hbox.getChildren().add(new JFXDatePicker());
        
        // time
        Label time = new Label(taskChild.getTime());
        time.getStyleClass().add("time");
        hbox.getChildren().add(time);
        
        // continue timer button
        Button continueTimer = new Button();
        continueTimer.getStyleClass().add("continueTimer");
        hbox.getChildren().add(continueTimer);
        
        // add hbox to titledpane
        childNode.setGraphic(hbox);
        
        return childNode;
    }
     
    private void UpdateTask(TaskParent taskParent) {
        
    }
    
    private void UpdateTask(TaskChild taskChild) {
        
    }
}
