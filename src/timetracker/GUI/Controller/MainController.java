/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import timetracker.DAL.DALException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import timetracker.GUI.Model.TaskModel;

/**
 *
 * @author Troels Klein
 */
public class MainController implements Initializable {

    @FXML
    private Label label;

    
     /**
     * Singleton opsætning af vores MainController. singleton gør at vores maincontroller ikke vil
     * blive instansieret mere end en gang.
     */
    private static TaskModel model;
    private static MainController main = null;

    private MainController() throws DALException {
        model = TaskModel.getInstance();
    }

    public static MainController getInstance() throws DALException {
        if (main == null) {
            main = new MainController();
        }
        return main;
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    /**
     * Tager det info som admin har puttet ind i "Projekt Manager" menuen og
     * sender det ned for at blive gemt på serveren
     *
     * @throws DALException
     */
    public void createProject() throws DALException {
        int clientID = 1;
        String projectName = "projekt 8";
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
