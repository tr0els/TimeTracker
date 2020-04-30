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
import com.jfoenix.controls.JFXTreeTableView;
import javafx.event.ActionEvent;
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
    private JFXComboBox<?> combobox;

    @FXML
    private JFXTextField timepris;

    @FXML
    private JFXTextField projektnavn;

    @FXML
    private JFXTreeTableView<?> treeView;

    @FXML
    private JFXDrawer drawer;
    
        private static TaskModel model;

    @FXML
    void handleProjektAction(ActionEvent event) {

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
        /**
     * Tager det info som admin har puttet ind i "Projekt Manager" menuen og
     * sender det ned for at blive gemt på serveren
     *
     * @throws DALException
     */
    public void createProject() throws DALException {
        int clientID = 1;
        String projectName = "projekt 9";
        int hourlyPay = 200;

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
