/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;


import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import timetracker.DAL.DALException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import timetracker.GUI.Model.TaskModel;


/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class MainController implements Initializable {

    @FXML
    private JFXButton adminbtb;
    @FXML
    private JFXButton user;
    @FXML
    private AnchorPane root;

    
     /**
     * Singleton opsætning af vores MainController. singleton gør at vores maincontroller ikke vil
     * blive instansieret mere end en gang.
     */
    private static TaskModel model;
    private static MainController main = null;

    public MainController() throws DALException, SQLException {
        model = TaskModel.getInstance();
    } 

    public static MainController getInstance() throws DALException, SQLException {
        if (main == null) {
            main = new MainController();
        }
        return main;
    }
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
 
 

    }

    /**
     * 
     * @param event
     * @throws IOException
     * Håndtere login af en admin
     */
    
   @FXML
    private void handeladminlogin(ActionEvent event) throws IOException {
        
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/timetracker/GUI/View/Menubar.fxml"));
            loader.load();
            Parent root = loader.getRoot();
            
            //MenubarController controller = loader.getController();
            
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Time Tracker");
            Stage Currentstage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Currentstage.close();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        
    }
    
    /**
     * 
     * @param event
     * @throws IOException 
     * Håndtere log in af en alm. user, og fjerne adminknapperne. 
     */

    @FXML
    private void handeluserlogin(ActionEvent event) throws IOException {
        
         
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/timetracker/GUI/View/Menubar.fxml"));
            loader.load();
            Parent root = loader.getRoot();
            
            MenubarController controller = loader.getController();
            controller.getBrugermanagerbtb().setVisible(false);
            controller.getKlientmanagerbtb().setVisible(false);
            controller.getProjektbtb().setVisible(false);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Time Tracker");
            Stage Currentstage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Currentstage.close();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        
        
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

