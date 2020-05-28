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
import timetracker.GUI.Model.ProjektModel;
import timetracker.GUI.Model.TaskModel;

/**
 * FXML Controller class
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class TaskController implements Initializable {

    private TaskModel model;
    private ProjektModel pModel;
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

    private Image imgPlay;
    private Image imgPause;
    private Image imgBillable;
    private Image imgNotBillable;
    private Image imgEdit;

    private int idag = 0;
    private int igår = 1;
    private int person_id;
    @FXML
    private Label timerHours;
    @FXML
    private Label timerMinutes;
    @FXML
    private Label timerSeconds;

    int timerSecondsv = 0;
    int timerMinutesv = 0;
    int timerHoursv = 0;

    boolean timerState = false;
    @FXML
    private JFXButton timerButton;
    @FXML
    private ScrollPane taskScrollPane;
    
    private ObservableList<Project> allProjects = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        person_id = 1; //midlertidigt, skal hentes fra login

        try {
            model = TaskModel.getInstance();
            pModel = ProjektModel.getInstance();
            setTasksGroupedByDate();
            allProjects.addAll(pModel.getProjects());

        } catch (DALException | SQLException ex) {
            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadImages();
        showProjects();
//        taskLogsbyDay(paneToday, idag);
//        taskLogsbyDay(paneYesterday, igår);
    }

    /**
     * Håndtere start og oprette task knappen action
     *
     * @param event
     */
    @FXML
    private void handleCreateTask(ActionEvent event) throws DALException {
        startTask();
    }

    /**
     * sætter image variabler op
     */
    public void loadImages() {
        imgPlay = new Image(getClass().getResourceAsStream("/timetracker/GUI/Icons/play.png"));
        imgPause = new Image(getClass().getResourceAsStream("/timetracker/GUI/Icons/pause.png"));
        imgBillable = new Image(getClass().getResourceAsStream("/timetracker/GUI/Icons/billable_active.png"));
        imgNotBillable = new Image(getClass().getResourceAsStream("/timetracker/GUI/Icons/billable_inactive.png"));
        imgEdit = new Image(getClass().getResourceAsStream("/timetracker/GUI/Icons/edit.png"));
    }

    /**
     * henter en liste over projekter og smider dem i vores combobox
     */
    public void showProjects() {
        comboListprojects.setItems(pModel.getProjectsCache());
    }

    /**
     * Tager de relevante informationer fra GUI og sender videre.
     */
    public void startTask() throws DALException {
        String task_name = textTaskname.getText(); //valideres og trimmes!
        boolean billable = checkBillable.isSelected();
        int project_id = comboListprojects.getSelectionModel().getSelectedItem().getProjectId();

        model.startTask(task_name, billable, project_id, person_id);
        textTaskname.clear();
        checkBillable.setSelected(true);
        comboListprojects.getSelectionModel().clearSelection();
//        taskLogsbyDay(paneToday, 0);

    }

    /**
     * stop task via task_id
     */
    public void stopTask() throws DALException {

        model.stopTask(person_id);
    }

    @FXML
    private void HandleTooltipForBillable(MouseEvent event) {
        Tooltip tip = new Tooltip();

        tip.setText("Vælg om en Opgave skal være 'Billable' eller ej");
        checkBillable.setTooltip(tip);
    }

    @FXML
    private void handleStartStopTask(ActionEvent event) {

        if (timerState == false) {
            timerState = true;
            timerSecondsv = 0;
            timerMinutesv = 0;
            timerHoursv = 0;
            timerButton.setText("Stop Task");
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

                                    if (timerSecondsv < 10){
                                    timerSeconds.setText("0"+timerSecondsv + "");
                                    }else{
                                    timerSeconds.setText(timerSecondsv + "");
                                    }
                                    if (timerMinutesv < 10){
                                    timerMinutes.setText("0"+timerMinutesv + ":");
                                    }else{
                                    timerMinutes.setText(timerMinutesv + ":");
                                    }
                                    if (timerHoursv < 10){
                                    timerHours.setText("0"+timerHoursv + ":");
                                    }else{
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

            timerButton.setText("Start Task");
        }

    }

//    /**
//     * opbygger view med task for den valgte dag idag = 0, igår = 1
//     *
//     */
//    public void taskLogsbyDay(AnchorPane currentPane, int dag) {
//
//        AnchorPane pane = currentPane;
//
//        List<Log> logList = new ArrayList<>();
//
//        logList = model.getTaskLogListByDay(person_id, dag);
//
//        pane.getChildren().clear();
//
//        int Y = 10; //padding
//        int taskLineHeight = 22;
//        int scrollpaneHeight = logList.size() * taskLineHeight + (2 * Y);
//
//        pane.setPrefHeight(scrollpaneHeight);
//        for (Log log : logList) {
//
//            Label lblTaskname = new Label(log.getTask_name());
//            lblTaskname.setTranslateY(Y);
//            lblTaskname.setTranslateX(10);
//
//            Label lblProject = new Label(log.getProject_name());
//            lblProject.setTranslateY(Y);
//            lblProject.setTranslateX(140);
//
//            Label btnBillable = new Label();
//            if (log.isBillable() == true) {
//                btnBillable.setGraphic(new ImageView(imgBillable));
//            } else {
//                btnBillable.setGraphic(new ImageView(imgNotBillable));
//            }
//            btnBillable.setTranslateY(Y);
//            btnBillable.setTranslateX(250);
//
//            String tid = "0:00";
//            if (log.getTotal_tid() != null) {
//                DateFormat total_tid = new SimpleDateFormat("H:mm");
//                tid = total_tid.format(log.getTotal_tid());
//            }
//            Label lblTotaltid = new Label(tid);
//            lblTotaltid.setTranslateY(Y);
//            lblTotaltid.setTranslateX(290);
//            lblTotaltid.setStyle("-fx-font-weight: bold");
//
//            String startTid = log.getStart_time().format(DateTimeFormatter.ofPattern("HH:mm"));
//            Label lblstarttid = new Label(startTid);
//            lblstarttid.setTranslateY(Y);
//            lblstarttid.setTranslateX(340);
//
//            JFXButton btnStart = new JFXButton();
//            btnStart.setGraphic(new ImageView(imgPause));
//            btnStart.setOnAction(event -> {
//                stopTask();
//                taskLogsbyDay(paneToday, 0);
//            });
//            btnStart.setTranslateY(Y - 4);
//            btnStart.setTranslateX(490);
//
//            String slutTid = "";
//            if (log.getEnd_time() != null) {
//                slutTid = log.getEnd_time().format(DateTimeFormatter.ofPattern("HH:mm"));
//                btnStart.setGraphic(new ImageView(imgPlay));
//                btnStart.setOnAction(event -> {
//                    startTask(); // Troels broke it! SORRY :) --> startTask(log.getTask_id());
//                    taskLogsbyDay(paneToday, 0);
//                });
//            }
//            Label lblSluttid = new Label(slutTid);
//            lblSluttid.setTranslateY(Y);
//            lblSluttid.setTranslateX(390);
//
//            JFXButton btnEdit = new JFXButton();
//            btnEdit.setGraphic(new ImageView(imgEdit));
//            btnEdit.setTranslateY(Y - 4);
//            btnEdit.setTranslateX(525);
//
//            Y = Y + taskLineHeight; //Sørger for at tasks flyttes ned til næste linje
//
//            pane.getChildren().add(lblProject);
//            pane.getChildren().add(btnBillable);
//            pane.getChildren().add(lblTotaltid);
//            pane.getChildren().add(lblstarttid);
//            pane.getChildren().add(lblSluttid);
//            pane.getChildren().add(btnStart);
//            pane.getChildren().add(btnEdit);
//            pane.getChildren().add(lblTaskname);
//        }
//    }
    
    public void setTasksGroupedByDate() throws DALException, SQLException {

        // Get users tasks grouped by date
        List<TaskGroup> tasks = model.getTasksGroupedByDate(1, "DATE", true, true);
        
        // Build task view
	Pane taskPane = TaskUtil.getView(tasks, allProjects);

        // put pane in scrollpane
        taskScrollPane.setContent(taskPane);      
    }
}
