/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
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

    /**
     * Singleton opsætning af vores MainController. singleton gør at vores
     * maincontroller ikke vil blive instansieret mere end en gang.
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

    public void handeladminlogin(ActionEvent event) throws IOException {

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

    public void handeluserlogin(ActionEvent event) throws IOException {

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

}
