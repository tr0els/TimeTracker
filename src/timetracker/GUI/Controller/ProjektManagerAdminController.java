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
import javafx.scene.input.MouseEvent;
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
    private JFXComboBox<Client> combobox;

    @FXML
    private JFXTextField timepris;

    @FXML
    private JFXTextField projektnavn;

    @FXML
    private JFXTreeTableView<Project> treeView;


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
    
    ObservableList<Client> clients;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            clients = model.getClients();
            combobox.setItems(clients);
        } catch (DALException ex) {
            Logger.getLogger(ProjektManagerAdminController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ProjektManagerAdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        populateTreeTable();
    }
    
        @FXML
    void handleProjektAction(ActionEvent event) throws DALException {
        createProject();
    }
    
        @FXML
    private void handleDeleteAction(ActionEvent event) throws DALException {
        deleteProject();
    }
    
    
    @FXML
    private void handleEditSetup(MouseEvent event) throws DALException {
        TreeItem<Project> project = treeView.getSelectionModel().getSelectedItem();
        int clientID = project.getValue().getClient_id();
        
        for (int i = 0; i < clients.size(); i++) {
            int cli = clients.get(i).getClient_id();
            
            if(clientID == cli)
            {
                combobox.getSelectionModel().select(clients.get(i));
            }
        }


        projektnavn.setText(project.getValue().getProject_name());
        timepris.setText(project.getValue().getProject_rate() + "");
        
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
        TreeItem<Project> project = treeView.getSelectionModel().getSelectedItem();
        int projectID = project.getValue().getProject_id();

        model.deleteProject(projectID);
    }

    /**
     * Henter projectID fra det projekt som admin har valgt. Tager så det
     * updateret data som er inputet og sender det ned til DAL så infoen på
     * databasen kan updateres
     *
     * @throws DALException
     */
    public void editProject() throws DALException {
        TreeItem<Project> project = treeView.getSelectionModel().getSelectedItem();
        
        int clientID = combobox.getSelectionModel().getSelectedItem().getClient_id();
        String projectName = projektnavn.getText();
        int hourlyPay = Integer.parseInt(timepris.getText());
        int projectID = project.getValue().getProject_id();
        
        model.editProject(clientID, projectName, hourlyPay, projectID);
    }
    
    private void populateTreeTable(){
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




}
