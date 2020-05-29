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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.SECONDS;
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
import javafx.scene.image.ImageView;
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

    private final DateTimeFormatter timeformatter = DateTimeFormatter.ofPattern("HH:mm");
    private Image icon_edit = new Image(getClass().getResourceAsStream("/timetracker/GUI/Icons/edit.png"));
    private Task edit_task;
    private ObservableList<Project> allProjects;
    private int person_id;
    long timerSecondsv = 0;
    long timerMinutesv = 0;
    long timerHoursv = 0;
    boolean timerState = false;
    private TaskModel model;
    private ProjektModel Pmodel;
    private BrugerModel Bmodel;
    private ChangelogModel Cmodel;
    @FXML
    private JFXTextField textTaskname;
    @FXML
    private JFXCheckBox checkBillable;
    @FXML
    private JFXComboBox<Project> comboListprojects;
    @FXML
    private Label timerHours;
    @FXML
    private Label timerMinutes;
    @FXML
    private Label timerSeconds;

    @FXML
    private JFXButton timerButton;

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
    @FXML
    private AnchorPane paneEdit;
    @FXML
    private JFXTextField txtTask_name;
    @FXML
    private JFXCheckBox chkboxBillable;
    @FXML
    private JFXComboBox<Project> menuEditProjects;
    @FXML
    private JFXButton btnEdit;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private JFXDatePicker dateTo;
    @FXML
    private JFXTimePicker timeTo;
    @FXML
    private JFXDatePicker dateFrom;
    @FXML
    private JFXTimePicker timeFrom;
    @FXML
    private Label lblWarning;
    @FXML
    private JFXDrawer skuffen;
    @FXML
    private VBox vboxContainer;
    @FXML
    private Label lblTime;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        person_id = 1; //midlertidigt, skal hentes fra login

        try {

            skuffen.setSidePane(paneEdit);
            skuffen.toBack();
            skuffen.close();

            model = TaskModel.getInstance();
            Pmodel = ProjektModel.getInstance();
            Bmodel = BrugerModel.getInstance();
            Cmodel = ChangelogModel.getInstance();

            createToday();
            createDays();
            showProjects();

        } catch (DALException | SQLException ex) {
            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * henter lister over projekter og putter dem i vores edit combobox
     */
    public void showProjects() throws DALException, SQLException {
        allProjects = Pmodel.getProjectsWithExtraData();
        menuEditProjects.setItems(allProjects);
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

    private void stopWatch(long hour, long minute, long second) {
        if (timerState == false) {
            timerState = true;
            timerSecondsv = second;
            timerMinutesv = minute;
            timerHoursv = hour;
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

//                                    if (timerSecondsv < 10) {
//                                        timerSecondsv = '0' + timerSecondsv;
//                                    }
//                                    if (timerMinutesv < 10) {
//                                        timerMinutesv = '0' + timerMinutesv;
//                                    } 
//                                    if (timerHoursv < 10) {
//                                        timerHoursv = '0' + timerHoursv;
//                                    } 
                                    String timerMinutesStr = String.format("%02d", timerMinutesv);
                                    String timerSecondsvStr = String.format("%02d", timerSecondsv);

                                    lblTime.setText(timerHoursv + ":" + timerMinutesStr + ":" + timerSecondsvStr);

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
        }
    }

    public void updateView() {
        try {
            createToday();
            createDays();
        } catch (DALException ex) {
            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createToday() throws DALException {
        hboxToday.getChildren().clear();
        vboxToday.getChildren().clear();
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
                    logHbox.setId("logHBoxToday");
                    logHbox.setAlignment(Pos.CENTER_LEFT);

                    Label task_name = new Label(t.getTaskName());
                    task_name.setPrefWidth(150);

                    Label project_name = new Label(t.getProjectName());
                    project_name.setPrefWidth(100);

                    String endtime;
                    Label total_time;

                    if (t.getEndTime() == null) {
                        endtime = "";
                        total_time = lblTime;
                        total_time.setStyle("-fx-font-weight: bold;");

                        Duration diff = Duration.between(t.getStartTime(), LocalDateTime.now());
                        long hour = diff.toHours();
                        long minute = diff.toMinutesPart();
                        long second = diff.toSecondsPart();

                        stopWatch(hour, minute, second);

                    } else {
                        endtime = t.getEndTime().toLocalTime().format(timeformatter);
                        total_time = new Label(t.getTotalTime());
                    }

                    total_time.setPrefWidth(100);

                    Label task_period = new Label(t.getStartTime().toLocalTime().format(timeformatter) + " - " + endtime);
                    task_period.setPrefWidth(180);

                    Label billable = new Label("$");
                    billable.getStyleClass().add("billable");

                    if (t.isBillable()) {
                        billable.getStyleClass().add("true");
                    } else {
                        billable.getStyleClass().add("false");
                    }

                    JFXButton editbtn = new JFXButton("");
                    editbtn.setGraphic(new ImageView(icon_edit));
                    editbtn.setOnAction(event -> editTask(t.getTaskId()));

                    logHbox.getChildren().addAll(task_name, project_name, task_period, total_time, billable, editbtn);

                    vboxToday.getChildren().addAll(logHbox);
                }

            }
        }
    }

    public void createDays() throws DALException {
        hboxDays.getChildren().clear();
        vboxDays.getChildren().clear();

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
                task_name.setPrefWidth(150);

                Label project_name = new Label(t.getProjectName());
                project_name.setPrefWidth(100);

                String endtime;

                if (t.getEndTime() == null) {
                    endtime = "";
                } else {
                    endtime = t.getEndTime().toLocalTime().format(timeformatter);
                }

                Label task_period = new Label(t.getStartTime().toLocalTime().format(timeformatter) + " - " + endtime);
                task_period.setPrefWidth(180);

                Label total_time = new Label(t.getTotalTime());
                total_time.setPrefWidth(100);

                Label billable = new Label("$");
                billable.getStyleClass().add("billable");

                if (t.isBillable()) {
                    billable.getStyleClass().add("true");
                } else {
                    billable.getStyleClass().add("false");
                }

                JFXButton editbtn = new JFXButton("");
                editbtn.setGraphic(new ImageView(icon_edit));
                editbtn.setOnAction(event -> editTask(t.getTaskId()));

                logHbox.getChildren().addAll(task_name, project_name, task_period, total_time, billable, editbtn);

                logVbox.getChildren().addAll(logHbox);
            }

            TitledPane titledPane = new TitledPane();
            titledPane.setGraphic(hboxDaysHeader);
            titledPane.setContent(logVbox);
            titledPane.setExpanded(false);

            vboxDays.getChildren().add(titledPane);
        }

    }

    private void editTask(int taskId) {
        try {
            edit_task = model.getTaskbyID(taskId);

            txtTask_name.setText(edit_task.getTaskName());
            chkboxBillable.setSelected(edit_task.isBillable());

            dateFrom.setValue(edit_task.getStartTime().toLocalDate());
            timeFrom.setValue(edit_task.getStartTime().toLocalTime());

            if (edit_task.getEndTime() != null) {
                dateTo.setValue(edit_task.getEndTime().toLocalDate());
                timeTo.setValue(edit_task.getEndTime().toLocalTime());
            }

            for (int i = 0; i < allProjects.size(); i++) {

                if (edit_task.getProjectId() == allProjects.get(i).getProjectId()) {
                    menuEditProjects.getSelectionModel().select(allProjects.get(i));
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

            LocalDate datefrom = dateFrom.getValue();
            LocalTime timefrom = timeFrom.getValue();
            LocalDateTime ldt_from = LocalDateTime.of(datefrom, timefrom);

            LocalDate dateto = dateTo.getValue();
            LocalTime timeto = timeTo.getValue();
            LocalDateTime ldt_to = LocalDateTime.of(dateto, timeto);

            edit_task.setStartTime(ldt_from);
            edit_task.setEndTime(ldt_to);

            edit_task.setTaskName(txtTask_name.getText());
            edit_task.setBillable(chkboxBillable.isSelected());
            edit_task.setProjectId(menuEditProjects.getSelectionModel().getSelectedItem().getProjectId());

            if (ldt_from.compareTo(ldt_to) == 1) {
                lblWarning.setText("fra-tid kan ikke være før til-tid!");
            } else {

                Cmodel.changelogTask(edit_task, person_id);
                model.updateTaskbyID(edit_task);

                updateView();

                showProjects(); //indlæser ny liste til vores editvindue

                skuffen.close();
                skuffen.toBack();
            }
        } catch (DALException | SQLException ex) {
            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void handleCancel(ActionEvent event) {
        skuffen.close();
        skuffen.toBack();

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

    }
}
