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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import timetracker.BE.Project;
import timetracker.BE.Task;
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
    
    private final DateTimeFormatter timeformatter = DateTimeFormatter.ofPattern("HH:mm");
    private TaskModel model;
    private ProjektModel pModel;
    @FXML
    private JFXTextField textTaskname;
    @FXML
    private JFXCheckBox checkBillable;
    @FXML
    private JFXComboBox<Project> comboListprojects;
    
    private Image imgPlay;
    private Image imgPause;
    private Image imgBillable;
    private Image imgNotBillable;
    private Image imgEdit;
    
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
    
    private ObservableList<Project> allProjects = FXCollections.observableArrayList();
    @FXML
    private HBox hboxToday;
    @FXML
    private ScrollPane scrollpaneToday;
    @FXML
    private VBox vboxToday;
    @FXML
    private HBox hboxDays;
    @FXML
    private ScrollPane scrollpaneDays;
    @FXML
    private VBox vboxDays;
    @FXML
    private Pane paneTaskview;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        person_id = 1; //midlertidigt, skal hentes fra login

        try {
            model = TaskModel.getInstance();
            pModel = ProjektModel.getInstance();
            allProjects.addAll(pModel.getProjects());
            
            createToday();
            createDays();
            
        } catch (DALException | SQLException ex) {
            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadImages();
        showProjects();
        
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
        comboListprojects.setItems(allProjects);
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
    
    public void createToday() throws DALException {
        
        if (model.getTaskbyDays(0, person_id).size() < 1) {
            Label notaskstoday = new Label("Ingen opgaver idag");
            hboxToday.getChildren().add(notaskstoday);
            Label gettowork = new Label("kom i sving!");
            vboxToday.getChildren().add(gettowork);
        } else {
            
            for (Map.Entry<String, List<Task>> entry : model.getTaskbyDays(0, person_id).entrySet()) {
                
                String key = entry.getKey();
                
                String[] arrOfStr = key.split("@", 2);
                
                Label date = new Label(arrOfStr[0]);
                
                Region dividerToday = new Region();
                
                Label total = new Label("total tid: " + arrOfStr[1]);
                
                hboxToday.setHgrow(dividerToday, Priority.ALWAYS);
                hboxToday.getChildren().addAll(date, dividerToday, total);
                
                for (int i = 0; i < entry.getValue().size(); i++) {
                    
                    Task t = entry.getValue().get(i);
                    
                    HBox logHbox = new HBox();
                    logHbox.setId("logHBox");
                    Label task_name = new Label(t.getTaskName());
                    task_name.setPrefWidth(100);
                    
                    Label task_start = new Label(t.getStartTime().toLocalTime().format(timeformatter));
                    task_start.setPrefWidth(100);
                    
                    String endtime;
                    
                    if (t.getEndTime() == null) {
                        endtime = "";
                    } else {
                        endtime = t.getEndTime().toLocalTime().format(timeformatter);
                    }
                    
                    Label task_end = new Label(endtime);
                    task_end.setPrefWidth(100);
                    
                    Label total_time = new Label(t.getTotalTime());
                    
                    Label billable = new Label("$");
                    billable.getStyleClass().add("billable");
                    
                    if (t.isBillable()) {
                        billable.getStyleClass().add("true");
                    } else {
                        billable.getStyleClass().add("false");
                    }
                    
                    logHbox.getChildren().addAll(task_name, task_start, task_end, total_time, billable);
                    
                    vboxToday.getChildren().addAll(logHbox);
                }
                
            }
        }
    }
    
    public void createDays() throws DALException {
        
        int days = 30;
        
        Label info = new Label("opgaver fra de sidste " + days + " dage");
        hboxDays.getChildren().add(info);
        
        for (Map.Entry<String, List<Task>> entry : model.getTaskbyDays(days, person_id).entrySet()) {
            
            HBox hboxDaysHeader = new HBox();
            
            String key = entry.getKey();
            
            String[] arrOfStr = key.split("@", 2);
            
            Label date = new Label(arrOfStr[0]);
            date.setPrefWidth(250);
            
            Region dividerToday = new Region();
            dividerToday.setPrefWidth(128);
            
            Label total = new Label(arrOfStr[1] + " total");
            total.setPrefWidth(150);
            total.setAlignment(Pos.CENTER_RIGHT);
            
            HBox.setHgrow(date, Priority.ALWAYS);
            hboxDaysHeader.getChildren().addAll(date, dividerToday, total);
            
            VBox logVbox = new VBox();
            logVbox.setId("logVBox");
            
            for (int i = 0; i < entry.getValue().size(); i++) {
                
                Task t = entry.getValue().get(i);
                
                HBox logHbox = new HBox();
                logHbox.setId("logHBox");
                logHbox.setAlignment(Pos.CENTER_LEFT);
                
                Label task_name = new Label(t.getTaskName());
                task_name.setPrefWidth(100);
                
                String endtime;
                
                if (t.getEndTime() == null) {
                    endtime = "";
                } else {
                    endtime = t.getEndTime().toLocalTime().format(timeformatter);
                }
                
                Label task_period = new Label(t.getStartTime().toLocalTime().format(timeformatter) + " - " + endtime);
                task_period.setPrefWidth(180);
                
                Label total_time = new Label(t.getTotalTime());
                
                Label billable = new Label("$");
                billable.getStyleClass().add("billable");
                
                if (t.isBillable()) {
                    billable.getStyleClass().add("true");
                } else {
                    billable.getStyleClass().add("false");
                }
                
                logHbox.getChildren().addAll(task_name, task_period, total_time, billable);
                
                logVbox.getChildren().addAll(logHbox);
            }
            
            TitledPane titledPane = new TitledPane();
            titledPane.setGraphic(hboxDaysHeader);
            titledPane.setContent(logVbox);
            titledPane.setExpanded(false);
            
            vboxDays.getChildren().add(titledPane);
        }
        
    }
    
}
