/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import java.awt.event.KeyAdapter;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import timetracker.BE.Client;
import timetracker.BE.Project;
import timetracker.DAL.DALException;
import timetracker.GUI.Model.ClientModel;
import timetracker.GUI.Model.ProjektModel;

/**
 * FXML Controller class
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class ProjektManagerAdminController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private JFXComboBox<Client> combobox;
    @FXML
    private JFXTextField timepris;
    @FXML
    private JFXTextField projektnavn;
    @FXML
    private JFXTreeTableView<Project> treeView;
    @FXML
    private AnchorPane editProjectPane;
    @FXML
    private AnchorPane createProjectPane;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private JFXComboBox<Client> comboboxEdit;
    @FXML
    private JFXTextField timeprisEdit;
    @FXML
    private JFXTextField projektnavnEdit;

    //private static TaskModel model;
    private ProjektModel model;
    private ClientModel cModel;
    private static ProjektManagerAdminController projektController = null;

    public ProjektManagerAdminController() throws DALException, SQLException {
        model = ProjektModel.getInstance();
        cModel = ClientModel.getInstance();
    }

//    Denne skal vel ikke være der ? når det er en controller?????
//    public static ProjektManagerAdminController getInstance() throws DALException, SQLException {
//        if (projektController == null) {
//            projektController = new ProjektManagerAdminController();
//        }
//        return projektController;
//    }
    ObservableList<Client> clients;
    TreeItem<Project> project;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        timepris.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    timepris.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        timeprisEdit.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    timeprisEdit.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        try {
            drawer.close();

            clients = cModel.getClients();
            combobox.setItems(clients);
            comboboxEdit.setItems(clients);

        } catch (DALException ex) {
            Logger.getLogger(ProjektManagerAdminController.class.getName()).log(Level.SEVERE, null, ex);
        }

        populateTreeTable();
    }

    /**
     * sætter det rigtige panel til drawer og åbner den for brugeren.
     *
     * @param event
     */
    @FXML
    private void handleGetCreateAction(ActionEvent event) {
        drawer.close();
        drawer.setSidePane(createProjectPane);
        drawer.open();
        drawer.toFront();
    }

    /**
     * kalder createprojekt når brugeren er klar til at oprette et projekt også
     * lukker den draweren.
     *
     * @param event
     * @throws DALException
     */
    @FXML
    void handleCreateAction(ActionEvent event) throws DALException {
        createProject();
        drawer.close();
    }

    /**
     * kalder deleteprojekt når brugeren trykker på slet knappen og lukker
     * draweren
     *
     * @param event
     * @throws DALException
     */
    @FXML
    private void handleDeleteAction(ActionEvent event) throws DALException {
        deleteProject();
        drawer.close();
    }

    /**
     * kalder editproject når brugeren trykker på edit knappen og lukker
     * draweren
     *
     * @param event
     * @throws DALException
     */
    @FXML
    private void handleEditAction(ActionEvent event) throws DALException {
        editProject();
        drawer.close();
    }

    /**
     * sætter data på textfields når man klikker på et projekt.
     *
     * @param event
     * @throws DALException
     */
    @FXML
    private void handleEditSetup(MouseEvent event) throws DALException {
        drawer.setSidePane(editProjectPane);

        project = treeView.getSelectionModel().getSelectedItem();
        int clientID = project.getValue().getClientId();

        for (int i = 0; i < clients.size(); i++) {
            int cli = clients.get(i).getClientId();
            if (clientID == cli) {
                comboboxEdit.getSelectionModel().select(clients.get(i));
            }
        }
        projektnavnEdit.setText(project.getValue().getProjectName());
        timeprisEdit.setText(project.getValue().getProjectRate() + "");

        drawer.open();
        drawer.toFront();
    }

    /**
     * Tager det info som admin har puttet ind i "Projekt Manager" menuen og
     * sender det ned for at blive gemt på serveren
     *
     * @throws DALException
     */
    public void createProject() throws DALException {
        int clientID = combobox.getSelectionModel().selectedItemProperty().get().getClientId();
        String projectName = projektnavn.getText();
        int hourlyPay = Integer.parseInt(timepris.getText());

        model.createProject(clientID, projectName, hourlyPay);

        populateTreeTable();
    }

    /**
     * Tager det projekt som Admin har valgt i "projekt Manager" menuen og
     * sender det ned til DAL så det kan fjernes fra serveren.
     *
     * projekter kan ikke slettes hvis projekter har tasks tilføjet til sig.
     *
     * @throws DALException
     */
    public void deleteProject() throws DALException {
        project = treeView.getSelectionModel().getSelectedItem();
        project.getParent().getChildren().remove(project);

        model.deleteProject(project.getValue().getProjectId());
    }

    /**
     * Henter projectID fra det projekt som admin har valgt. Tager så det
     * updateret data som er inputet og sender det ned til DAL så infoen på
     * databasen kan updateres
     *
     * @throws DALException
     */
    public void editProject() throws DALException {
        int clientID = comboboxEdit.getSelectionModel().getSelectedItem().getClientId();
        String projectName = projektnavnEdit.getText();
        int hourlyPay = Integer.parseInt(timeprisEdit.getText());
        int projectID = project.getValue().getProjectId();

        model.editProject(clientID, projectName, hourlyPay, projectID);

        treeView.getSelectionModel().getSelectedItem().getValue().setProjectName(projectName);

        populateTreeTable();
    }

    /**
     * oprette coloner i treetableview og sætter listen af projekter fra
     * databasen ind.
     */
    private void populateTreeTable() {

        //opretter colonerne
        JFXTreeTableColumn<Project, String> projectName = new JFXTreeTableColumn<>("Projekt");
        projectName.setPrefWidth(250);
        JFXTreeTableColumn<Project, String> projectClient = new JFXTreeTableColumn<>("Klient");
        projectClient.setPrefWidth(150);
        JFXTreeTableColumn<Project, String> projectRate = new JFXTreeTableColumn<>("Timepris");
        projectRate.setPrefWidth(150);

        //vælger hvilket data der skal vises (i dette tilfælde projekt navnet) og hvilken colone det skal vises i
        projectName.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Project, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Project, String> param) {
                return new SimpleStringProperty(param.getValue().getValue().getProjectName());
            }
        });

        projectClient.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Project, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Project, String> param) {
                return new SimpleStringProperty(param.getValue().getValue().getClientName());
            }
        });

        projectRate.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Project, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Project, String> param) {
                return new SimpleStringProperty(Integer.toString(param.getValue().getValue().getProjectRate()));
            }
        });

        //opretter listen som skal indenholde alle de projekter som skal vises
        ObservableList<Project> projects = FXCollections.observableArrayList();

        //henter det data der skal ind i listen fra databasen
        try {
            projects.addAll(model.getProjects());

        } catch (DALException ex) {
            Logger.getLogger(ProjektManagerAdminController.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ProjektManagerAdminController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        //sætter dataen ind i selve treetableviewet
        final TreeItem<Project> root = new RecursiveTreeItem<Project>(projects, RecursiveTreeObject::getChildren);

        treeView.getColumns().setAll(projectName, projectClient, projectRate);
        treeView.setRoot(root);
        treeView.setShowRoot(false);
    }

}
