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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import timetracker.BE.Project;
import timetracker.BE.Task;
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
public class ProjektViewController implements Initializable {

    private TaskModel model;
    private ProjectModel Pmodel;
    private UserModel Bmodel;
    private ChangelogModel Cmodel;
    private int person_id;
    private ObservableList<Project> allProjects;
    private ObservableList<Project> personalProjects;
    private Task edit_task;
    private ObservableList<Task> observablelogs;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM-yyyy HH:mm");
    private Image icon_edit = new Image(getClass().getResourceAsStream("/timetracker/GUI/Icons/edit.png"));
    private Image icon_billable = new Image(getClass().getResourceAsStream("/timetracker/GUI/Icons/billable_active.png"));
    private Image icon_notbillable = new Image(getClass().getResourceAsStream("/timetracker/GUI/Icons/billable_inactive.png"));
    private ImageView billable;

    @FXML
    private AnchorPane root;
    @FXML
    private JFXComboBox<Project> projectMenubox;
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
    private JFXButton btnCancel;
    @FXML
    private VBox vboxTasks;
    @FXML
    private HBox hboxHeader;
    @FXML
    private JFXDatePicker dateFrom;
    @FXML
    private JFXDatePicker dateTo;
    @FXML
    private JFXTimePicker timeFrom;
    @FXML
    private JFXTimePicker timeTo;
    @FXML
    private Pane paneProjectDetails;
    @FXML
    private ScrollPane scrollPaneTask;
    @FXML
    private HBox hbox_head;
    @FXML
    private Label lblClientname;
    @FXML
    private Label lblWarning;

    public ProjektViewController() throws DALException, SQLException {
        model = TaskModel.getInstance();
        Pmodel = ProjectModel.getInstance();
        Bmodel = UserModel.getInstance();
        Cmodel = ChangelogModel.getInstance();
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

            person_id = Bmodel.getUser().getPersonId();
    
            loadProjects();
            showProjects();
            projectListener();
            setHBox();

            projectMenubox.getSelectionModel().select(0);

        } catch (DALException ex) {
            Logger.getLogger(TaskController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ProjektViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * opsætter overskrift på opgave oversigt.
     */
    public void setHBox() {

        Label arrow = new Label(" ");
        arrow.setPrefWidth(25);
        arrow.setMinWidth(25);
        arrow.setAlignment(Pos.CENTER);

        Label taskname = new Label("Opgave navn");
        taskname.prefWidthProperty().bind(paneProjectDetails.widthProperty().multiply(0.5));

        Label totaltime = new Label("Total tid");
        totaltime.setPrefWidth(60);

        Label lastworkedon = new Label("Sidst arbejdet på");
        lastworkedon.prefWidthProperty().bind(paneProjectDetails.widthProperty().multiply(0.23));

        hboxHeader.getChildren().addAll(arrow, taskname, lastworkedon, totaltime);
    }

    /**
     * Laver task overview baseret på project_id
     *
     * @param project_id
     * @throws DALException
     */
    public void createTree(int project_id) throws DALException {
        vboxTasks.getChildren().clear(); //nulstiller vores task view inden vi opbygger den igen.

        for (Map.Entry<Task, List<Task>> entry : model.getTaskbyIDs(project_id, person_id).entrySet()) {
            Task hashTask = entry.getKey();

            HBox headerBox = new HBox();
            Label tasknameHead = new Label(hashTask.getTaskName());
            tasknameHead.prefWidthProperty().bind(paneProjectDetails.widthProperty().multiply(0.5));

            Label totaltimeHead = new Label(hashTask.getTotalTime());
            totaltimeHead.setPrefWidth(60);
            totaltimeHead.setAlignment(Pos.CENTER_RIGHT);

            Label lastworkedonHead = new Label(formatter.format(hashTask.getLastWorkedOn()));
            lastworkedonHead.prefWidthProperty().bind(paneProjectDetails.widthProperty().multiply(0.23).subtract(17));

            Region endspace = new Region();

            headerBox.setHgrow(endspace, Priority.ALWAYS);
            headerBox.getChildren().addAll(tasknameHead, lastworkedonHead, totaltimeHead, endspace);

            VBox logVbox = new VBox();
            logVbox.setId("logVBox");

            for (int i = 0; i < entry.getValue().size(); i++) {

                Task t = entry.getValue().get(i);

                HBox logHbox = new HBox();
                logHbox.setId("logHBox");
                logHbox.setAlignment(Pos.CENTER_LEFT);

                Label log = new Label(t.getStartTime().format(formatter).toString() + " - " + t.getEndTime().format(formatter).toString());
                log.setAlignment(Pos.CENTER_RIGHT);

                Region spacer = new Region();

                Label log_total = new Label(t.getTotalTime());
                log_total.setPrefWidth(60);
                log_total.setStyle("-fx-font-weight: bold;");
                log_total.setAlignment(Pos.CENTER_RIGHT);

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

                logHbox.setHgrow(spacer, Priority.ALWAYS);

                logHbox.setMargin(editbtn, new Insets(0, 5, 0, 0));
                logHbox.setMargin(billable, new Insets(0, 23, 0, 0));
                logHbox.setMargin(log_total, new Insets(0, 4, 0, 0));
                logHbox.setMargin(log, new Insets(0, 0, 0, 30));

                logHbox.getChildren().addAll(log, spacer, log_total, billable, editbtn);

                logVbox.getChildren().add(logHbox);
            }

            TitledPane titledPane = new TitledPane();
            titledPane.setGraphic(headerBox);
            titledPane.setContent(logVbox);
            titledPane.setExpanded(false);

            vboxTasks.getChildren().add(titledPane);

        }

    }

    /**
     * henter den valgte task og indsætter værdierne i edit view.
     *
     * @param task_id
     */
    public void editTask(int task_id) {
        try {
            edit_task = model.getTaskbyID(task_id);

            txtTask_name.setText(edit_task.getTaskName());
            chkboxBillable.setSelected(edit_task.isBillable());

            dateFrom.setValue(edit_task.getStartTime().toLocalDate());
            timeFrom.setValue(edit_task.getStartTime().toLocalTime());

            dateTo.setValue(edit_task.getEndTime().toLocalDate());
            timeTo.setValue(edit_task.getEndTime().toLocalTime());

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
     * udfylder vores combobox med projekter person_id har arbejdet på.
     *
     * @throws DALException
     */
    public void loadProjects() throws DALException {

        personalProjects = Pmodel.getProjectsbyID(person_id);
        projectMenubox.setItems(personalProjects);
    }

    /**
     * henter lister over projekter og putter dem i vores edit combobox
     */
    public void showProjects() throws DALException, SQLException {
        allProjects = Pmodel.getProjectsWithExtraData();
        menuEditProjects.setItems(allProjects);
    }

    /**
     * en listener på vores combobox med projekter, den smider en liste af task
     * ind i et listview baseret på hvilket projekt der er valgt
     */
    public void projectListener() {
        projectMenubox.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Project> observable, Project oldValue, Project newValue)
                -> {
            if (newValue != null) {
                try {
                    lblProjectnavn.setText(newValue.getProjectName());
                    lblProjectTid.setText(newValue.getTotalTime());
                    lblClientname.setText(newValue.getClientName());
                    createTree(newValue.getProjectId());
                } catch (DALException ex) {
                    Logger.getLogger(ProjektViewController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
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
                lblWarning.setText("til-tid kan ikke være før fra-tid!");
            } else {
                lblWarning.setText("");
                Cmodel.changelogTask(edit_task.getTaskId(), person_id);
                model.updateTaskbyID(edit_task);

                loadProjects(); //indlæser opdateret liste af projekter (nye total-tider)

                for (int i = 0; i < personalProjects.size(); i++) { //kører igennem projektlisten for at finde den der matcher den opdatere projekt og vælger den.

                    if (menuEditProjects.getSelectionModel().getSelectedItem().getProjectId() == personalProjects.get(i).getProjectId()) {
                        projectMenubox.getSelectionModel().select(personalProjects.get(i));
                    }

                }

                createTree(menuEditProjects.getSelectionModel().getSelectedItem().getProjectId()); //opdatere gui til det project hvor den opdatere task ligger i.

                showProjects(); //indlæser ny liste til vores editvindue

                skuffen.close();
                skuffen.toBack();
            }
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
        lblWarning.setText("");
        skuffen.close();
        skuffen.toBack();

    }

}
