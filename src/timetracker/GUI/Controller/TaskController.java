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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import timetracker.BE.Project;
import timetracker.BE.TaskGroup;
import timetracker.DAL.DALException;
import timetracker.GUI.Model.ProjectModel;
import timetracker.GUI.Model.TaskModel;

/**
 * FXML Controller class
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class TaskController implements Initializable
{

    private TaskModel model;
    private ProjectModel pModel;
    private Image imgPlay;
    private Image imgPause;
    private Image imgBillable;
    private Image imgNotBillable;
    private Image imgEdit;
    private int idag = 0;
    private int igår = 1;
    private int person_id;
    int timerSecondsv = 0;
    int timerMinutesv = 0;
    int timerHoursv = 0;
    boolean timerState = false;
    private ObservableList<Project> allProjects = FXCollections.observableArrayList();

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        person_id = 1; //midlertidigt, skal hentes fra login
        try
        {
            model = TaskModel.getInstance();
            pModel = ProjectModel.getInstance();
            allProjects.addAll(pModel.getProjects());
            setTasksGroupedByDate();

        } catch (DALException | SQLException ex)
        {
            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadImages();
        showProjects();
    }

    /**
     * Sætter image variabler op
     */
    public void loadImages()
    {
        imgPlay = new Image(getClass().getResourceAsStream("/timetracker/GUI/Icons/play.png"));
        imgPause = new Image(getClass().getResourceAsStream("/timetracker/GUI/Icons/pause.png"));
        imgBillable = new Image(getClass().getResourceAsStream("/timetracker/GUI/Icons/billable_active.png"));
        imgNotBillable = new Image(getClass().getResourceAsStream("/timetracker/GUI/Icons/billable_inactive.png"));
        imgEdit = new Image(getClass().getResourceAsStream("/timetracker/GUI/Icons/edit.png"));
    }

    /**
     * Henter en liste over projekter og smider dem i vores combobox
     */
    public void showProjects()
    {
        comboListprojects.setItems(allProjects);
    }

    /**
     * Tager de relevante informationer fra GUI og sender videre.
     */
    public void startTask() throws DALException
    {
        String task_name = textTaskname.getText(); //valideres og trimmes!
        boolean billable = checkBillable.isSelected();
        int project_id = comboListprojects.getSelectionModel().getSelectedItem().getProjectId();

        model.startTask(task_name, billable, project_id, person_id);
        textTaskname.clear();
        checkBillable.setSelected(true);
        comboListprojects.getSelectionModel().clearSelection();
    }

    /**
     * stop task via task_id
     */
    public void stopTask() throws DALException
    {

        model.stopTask(person_id);
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
     * Sætter listen af brugers tasks i vores Scrollpane
     *
     * @throws DALException
     * @throws SQLException
     */
    public void setTasksGroupedByDate() throws DALException, SQLException
    {
        // Popullerer listen med brugers tasks sorteret efter dato
        List<TaskGroup> tasks = model.getTasksGroupedByDate(1, "DATE", true, true);
        // Bygger task view
        Pane taskPane = TaskUtil.getView(tasks, allProjects);
        // Putter task view i scrollpane       
        taskScrollPane.setContent(taskPane);
    }
}
