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
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import timetracker.BE.Client;
import timetracker.BE.Project;
import timetracker.DAL.DALException;
import timetracker.GUI.Model.TaskModel;

/**
 * FXML Controller class
 *
 * @author Charlotte
 */
public class ProjektManagerAdminController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private AnchorPane viewholder;

    @FXML
    private JFXComboBox<Client> combobox;

    @FXML
    private JFXTextField timepris;

    @FXML
    private JFXTextField projektnavn;

    @FXML
    private JFXTreeTableView<Project> treeView;

    @FXML
    private JFXDrawer drawer;

    private static TaskModel model;
    private static ProjektManagerAdminController projektController = null;

    public ProjektManagerAdminController() throws DALException, SQLException {
        model = TaskModel.getInstance();
    }

    public static ProjektManagerAdminController getInstance() throws DALException, SQLException {
        if (projektController == null) {
            projektController = new ProjektManagerAdminController();
        }
        return projektController;
    }

    @FXML
    void handleProjektAction(ActionEvent event) throws DALException {
        createProject();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            combobox.setItems(model.getClients());
        } catch (DALException ex) {
            Logger.getLogger(ProjektManagerAdminController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ProjektManagerAdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        JFXTreeTableColumn<Project, String> projectName = new JFXTreeTableColumn<>("projekt");
        projectName.setPrefWidth(150);
        JFXTreeTableColumn<Project, String> totalTidBrugt = new JFXTreeTableColumn<>("Total Tid Brugt");
        totalTidBrugt.setPrefWidth(150);
        JFXTreeTableColumn<Project, String> sidstArbejdetPå = new JFXTreeTableColumn<>("Sidst Arbejdet På");
        sidstArbejdetPå.setPrefWidth(150);

        projectName.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Project, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Project, String> param) {
                return new SimpleStringProperty(param.getValue().getValue().project_name);
            }
        });

        ObservableList<Project> projects = FXCollections.observableArrayList();
        
        try {
            projects.addAll(model.getProjects());
        } catch (DALException ex) {
            Logger.getLogger(ProjektManagerAdminController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ProjektManagerAdminController.class.getName()).log(Level.SEVERE, null, ex);
        }

        final TreeItem<Project> root = new RecursiveTreeItem<Project>(projects, RecursiveTreeObject::getChildren);

        treeView.getColumns().setAll(projectName, totalTidBrugt, sidstArbejdetPå);
        treeView.setRoot(root);
        treeView.setShowRoot(false);

    }

    /**
     * Tager det info som admin har puttet ind i "Projekt Manager" menuen og
     * sender det ned for at blive gemt på serveren
     *
     * @throws DALException
     */
    public void createProject() throws DALException {
        int clientID = combobox.getSelectionModel().selectedItemProperty().get().getClient_id();
        String projectName = projektnavn.getText();
        int hourlyPay = Integer.parseInt(timepris.getText());

        model.createProject(clientID, projectName, hourlyPay);
    }

    /**
     * Tager det projekt som Admin har valgt i "projekt Manager" menuen og
     * sender det ned til DAL så det kan fjernes fra serveren.
     *
     * @throws DALException
     */
    public void deleteProject() throws DALException {
        int clientID = 1;
        String projectName = "projekt 2";
        int hourlyPay = 200;

        model.deleteProject(clientID, projectName, hourlyPay);
    }

    /**
     * Henter projectID fra det projekt som admin har valgt. Tager så det
     * updateret data som er inputet og sender det ned til DAL så infoen på
     * databasen kan updateres
     *
     * @throws DALException
     */
    public void editProject() throws DALException {
        int clientID = 1;
        String projectName = "projekt 2";
        int hourlyPay = 300;
        int projectID = 6;

        model.editProject(clientID, projectName, hourlyPay, projectID);
    }

}
