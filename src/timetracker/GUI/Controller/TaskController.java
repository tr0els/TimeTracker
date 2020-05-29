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
    private ProjectModel pModel;
    private UserModel uModel;
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

    private final DateTimeFormatter timeformatter = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter currentFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter newFormat = DateTimeFormatter.ofPattern("dd/MM-yyyy");
    private Image icon_edit = new Image(getClass().getResourceAsStream("/timetracker/GUI/Icons/edit.png"));
    private Image icon_play = new Image(getClass().getResourceAsStream("/timetracker/GUI/Icons/play.png"));
    private Image icon_pause = new Image(getClass().getResourceAsStream("/timetracker/GUI/Icons/pause.png"));
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
    @FXML
    private HBox hbox_head;
    @FXML
    private Label lblnewWarning;
    @FXML
    private JFXButton btnStart;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {



        try {

//            person_id = Bmodel.getUser().getPerson_id();
            person_id = 1; // midlertidigt
            
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
        comboListprojects.setItems(allProjects);
    }

    /**
     * Tager de relevante informationer fra GUI og sender videre. laver check om
     * alle felter er udfyldt.
     */
    public void startTask() throws DALException {

        if (textTaskname.getText().equals("") || comboListprojects.getSelectionModel().isEmpty()) {
            lblnewWarning.setText("alle felter skal udfyldes!");
            lblnewWarning.setAlignment(Pos.CENTER_RIGHT);

        } else {
            lblnewWarning.setText("");
            String task_name = textTaskname.getText().trim();
            int project_id = comboListprojects.getSelectionModel().getSelectedItem().getProjectId();
            boolean billable = checkBillable.isSelected();
            stopTask();
            model.startTask(task_name, billable, project_id, person_id);
            textTaskname.clear();
            checkBillable.setSelected(true);
            comboListprojects.getSelectionModel().clearSelection();
            updateView();

        }

    }

    /**
     * bruges til at starte en opgave baseret på en allerede eksisterende opgave
     *
     * @param t
     * @throws DALException
     */
    public void startActiveTask(Task t) throws DALException {
        String task_name = t.getTaskName();
        boolean billable = t.isBillable();
        int project_id = t.getProjectId();

        stopTask();
        model.startTask(task_name, billable, project_id, person_id);
        updateView();
    }

    /**
     * stop task via task_id
     */
    public void stopTask() throws DALException
    {

        model.stopTask(person_id);
        timerState = false;
    }

    /**
     * Bliver brugt til at vise tiden der er gået siden en aktiv opgave er
     * startet
     */
    private void stopWatch() {
        if (timerState == false) {
            timerState = true;
            Thread t = new Thread(new Runnable() {
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

                                    String timerMinutesStr = String.format("%02d", timerMinutesv);
                                    String timerSecondsvStr = String.format("%02d", timerSecondsv);

                                    lblTime.setText(timerHoursv + ":" + timerMinutesStr + ":" + timerSecondsvStr);

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
        }
    }

    /**
     * bruges til at opdatere vores gui når vi har lavet opdateringer til
     * opgaver
     */
    public void updateView() {
        try {
            createToday();
            createDays();
        } catch (DALException ex) {
            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Opbygger gui med dagens opgaver
     *
     * @throws DALException
     */
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

                LocalDate startDate = LocalDate.parse(arrOfStr[0], currentFormat);

                String formattedDate = startDate.format(newFormat).toString();

                Label date = new Label(formattedDate);

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
                    JFXButton btn = new JFXButton("");

                    if (t.getEndTime() == null) {
                        endtime = "";
                        total_time = lblTime;
                        total_time.setStyle("-fx-font-weight: bold;");

                        Duration diff = Duration.between(t.getStartTime(), LocalDateTime.now());
                        timerHoursv = diff.toHours();
                        timerMinutesv = diff.toMinutesPart();
                        timerSecondsv = diff.toSecondsPart();

                        stopWatch();

                        btn.setGraphic(new ImageView(icon_pause));
                        btn.setOnAction(event -> {
                            try {
                                stopTask();
                                updateView();
                            } catch (DALException ex) {
                                Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });

                    } else {
                        endtime = t.getEndTime().toLocalTime().format(timeformatter);
                        total_time = new Label(t.getTotalTime());

                        btn.setGraphic(new ImageView(icon_play));
                        btn.setOnAction(event -> {
                            try {
                                startActiveTask(t);
                            } catch (DALException ex) {
                                Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
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

                    logHbox.getChildren().addAll(task_name, project_name, task_period, total_time, billable, editbtn, btn);

                    vboxToday.getChildren().addAll(logHbox);
                }

            }
        }
    }

    /**
     * Opbygger gui med opgaver for de seneste dage
     *
     * @throws DALException
     */
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

            LocalDate startDate = LocalDate.parse(arrOfStr[0], currentFormat);

            String formattedDate = startDate.format(newFormat).toString();

            Label date = new Label(formattedDate);
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
                Label total_time;
                JFXButton btn = new JFXButton("");

                if (t.getEndTime() == null) {
                    endtime = "";
                    total_time = lblTime;
                    total_time.setStyle("-fx-font-weight: bold;");

                    Duration diff = Duration.between(t.getStartTime(), LocalDateTime.now());
                    timerHoursv = diff.toHours();
                    timerMinutesv = diff.toMinutesPart();
                    timerSecondsv = diff.toSecondsPart();

                    stopWatch();

                    btn.setGraphic(new ImageView(icon_pause));
                    btn.setOnAction(event -> {
                        try {
                            stopTask();
                            updateView();
                        } catch (DALException ex) {
                            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });

                } else {
                    endtime = t.getEndTime().toLocalTime().format(timeformatter);
                    total_time = new Label(t.getTotalTime());

                    btn.setGraphic(new ImageView(icon_play));
                    btn.setOnAction(event -> {
                        try {
                            startActiveTask(t);
                        } catch (DALException ex) {
                            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
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

                logHbox.getChildren().addAll(task_name, project_name, task_period, total_time, billable, editbtn, btn);

                logVbox.getChildren().addAll(logHbox);
            }

            TitledPane titledPane = new TitledPane();
            titledPane.setGraphic(hboxDaysHeader);
            titledPane.setContent(logVbox);
            titledPane.setExpanded(false);

            vboxDays.getChildren().add(titledPane);
        }

    }

    /**
     * Henter relevante info ang. en opgave og udfylder gui med det.
     *
     * @param taskId
     */
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

    /**
     * håndtere redigering af en opgave, tager de relevante info fra gui og
     * sender dem til db for opdatering
     *
     * @param event
     */
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

    /**
     * bruges til at lukke vores redigeringsvindue
     *
     * @param event
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        skuffen.close();
        skuffen.toBack();

    }

    /**
     * tooltip til vores checkbox i redigerings vinduet
     *
     * @param event
     */
    @FXML
    private void HandleTooltipForBillable(MouseEvent event) {
        Tooltip tip = new Tooltip();

        tip.setText("Vælg om en Opgave skal faktureres eller ej");
        checkBillable.setTooltip(tip);
    }

    /**
     * starter en ny task
     *
     * @param event
     * @throws DALException
     */
    @FXML
    private void handleStartStopTask(ActionEvent event) throws DALException {
        startTask();

    }

    /**
     *
     * @return
     */
    public Pane getTaskView()
    {
        // pane to wrap the final result in
        Pane taskPane = new Pane();

        // vbox to keep nodes below each other
        VBox wrapper = new VBox();
        wrapper.getStyleClass().add("wrapper");
        taskPane.getChildren().add(wrapper);

        for (TaskGroup taskGroup : tasks)
        {
            // build nodes for each task group
            HBox groupNode = buildTaskGroup(taskGroup);
            wrapper.getChildren().add(groupNode);

            // build nodes for each parent (including any children)
            for (TaskParent taskParent : taskGroup.getParents())
            {
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

    private HBox buildTaskGroup(TaskGroup taskGroup)
    {
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

    private TitledPane buildTask(TaskParent taskParent)
    {
        // titledpane
        TitledPane parentNode = new TitledPane();
        parentNode.setExpanded(false);

        // hbox wrapper
        HBox hbox = new HBox();

        // name + num children wrapper
        HBox hboxName = new HBox();
        hboxName.getStyleClass().add("name");

        // show number of children
        if (taskParent.getChildren().size() > 1)
        {
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
        name.setOnAction(e ->
        {
            if (!taskParent.getName().equals(name.getText()))
            {
                taskParent.setName(name.getText());
                UpdateTask(taskParent);
                System.out.println("name changed to: " + name.getText());
            }
        });
        // edit name on unfocus
        name.focusedProperty().addListener((obs, oldVal, newVal) ->
        {
            if (newVal == false && !taskParent.getName().equals(name.getText()))
            {
                taskParent.setName(name.getText());
                UpdateTask(taskParent);
                System.out.println("name changed to: " + name.getText());
            }
        });
        // project
        JFXComboBox<Project> project = new JFXComboBox();
        project.setItems(projects);

        // select the project in combobox that matches the tasks project id
        for (Project p : projects)
        {
            if (p.getProjectId() == taskParent.getProjectId())
            {
                project.getSelectionModel().select(p);
            }
        }
        project.getStyleClass().add("project");
        hbox.getChildren().add(project);

        // edit project
        project.setOnAction(e ->
        {
            int projectId = project.getSelectionModel().getSelectedItem().getProjectId();
            taskParent.setProjectId(projectId);
            UpdateTask(taskParent);
            System.out.println("project changed to: " + projectId);
        });
        // billable
        Label billable = new Label("$");
        billable.getStyleClass().add("billable");

        if (taskParent.isBillable())
        {
            billable.getStyleClass().add("true");
        } else
        {
            billable.getStyleClass().add("false");
        }
        // edit billable
        billable.setOnMouseClicked(e ->
        {
            taskParent.setBillable(!taskParent.isBillable());
            //updateTaskBillable(taskParent);
            System.out.println("Task billable is: " + taskParent.isBillable());

            billable.getStyleClass().clear();
            billable.getStyleClass().add("billable");
            if (taskParent.isBillable())
            {
                billable.getStyleClass().add("true");
            } else
            {
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

        if (taskParent.getChildren().size() > 1)
        {
            for (TaskChild taskChild : taskParent.getChildren())
            {
                childrenWrapper.getChildren().add(buildTask(taskChild));
            }
        }

        parentNode.setContent(childrenWrapper);

        // add hbox to titledpane
        parentNode.setGraphic(hbox);

        return parentNode;
    }

    private TitledPane buildTask(TaskChild taskChild)
    {
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
        name.setOnAction(e ->
        {
            if (!taskChild.getName().equals(name.getText()))
            {
                taskChild.setName(name.getText());
                //updateTaskName(taskParent);
                System.out.println("name changed to: " + name.getText());
            }
        });
        // edit name on unfocus
        name.focusedProperty().addListener((obs, oldVal, newVal) ->
        {
            if (newVal == false && !taskChild.getName().equals(name.getText()))
            {
                taskChild.setName(name.getText());
                //updateTaskName(taskParent);
                System.out.println("name changed to: " + name.getText());
            }
        });
        // project
        JFXComboBox<Project> project = new JFXComboBox();
        project.setItems(projects);

        // select the project in combobox that matches the tasks project id
        for (Project p : projects)
        {
            if (p.getProjectId() == taskChild.getProjectId())
            {
                project.getSelectionModel().select(p);
            }
        }
        project.getStyleClass().add("project");
        hbox.getChildren().add(project);

        // edit project
        project.setOnAction(e ->
        {
            int projectId = project.getSelectionModel().getSelectedItem().getProjectId();
            taskChild.setProjectId(projectId);
            //updateTaskProject(taskParent);
            System.out.println("project changed to: " + projectId);
        });
        // billable
        Label billable = new Label("$");
        billable.getStyleClass().add("billable");

        if (taskChild.isBillable())
        {
            billable.getStyleClass().add("true");
        } else
        {
            billable.getStyleClass().add("false");
        }
        // edit billable
        billable.setOnMouseClicked(e ->
        {
            taskChild.setBillable(!taskChild.isBillable());
            //updateTaskBillable(taskParent);
            System.out.println("Task billable is: " + taskChild.isBillable());

            billable.getStyleClass().clear();
            billable.getStyleClass().add("billable");
            if (taskChild.isBillable())
            {
                billable.getStyleClass().add("true");
            } else
            {
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

    private void UpdateTask(TaskParent taskParent)
    {

    }

    private void UpdateTask(TaskChild taskChild)
    {

    }
}
