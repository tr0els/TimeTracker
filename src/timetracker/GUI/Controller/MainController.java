/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import timetracker.DAL.DALException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import timetracker.GUI.Model.BrugerModel;


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
    private static BrugerModel model;
    private static MainController main = null;
    @FXML
    private AnchorPane root;
    @FXML
    private JFXButton adminbtb;
    @FXML
    private JFXButton user;
    @FXML
    private JFXButton adminbtb1;
    @FXML
    private JFXTextField emailTextField;
    @FXML
    private JFXTextField passwordTextField;

    public MainController() throws DALException, SQLException {
        model = BrugerModel.getInstance();
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
     * tager det info som er inputtet i textfields og sender dem til model.
     * 
     * alle passwords er 1234
     *
     * @param event
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    @FXML
    private void handelogin(ActionEvent event) throws NoSuchAlgorithmException, IOException, DALException {
        String email = emailTextField.getText();
        String password = passwordTextField.getText();

        if (model.login(email, password) != null) {
            
            int role = model.login(email, password).getRole_id();

            if (role == 1) {
                handeladminlogin(event);
            }
            if (role == 2) {
                handeluserlogin(event);
            }
        }
    }

    /**
     *
     * @param event
     * @throws IOException Håndtere login af en admin
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
        stage.setTitle("Time Tracker Admin");
        stage.getIcons().add(new Image("/timetracker/GUI/Icons/grumsen.png"));
        Stage Currentstage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Currentstage.close();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    /**
     *
     * @param event
     * @throws IOException Håndtere log in af en alm. user, og fjerne
     * adminknapperne.
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
        controller.getOverblikforAdmin().setVisible(false);
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Time Tracker alm. bruger");
        stage.getIcons().add(new Image("/timetracker/GUI/Icons/grumsen.png"));
        Stage Currentstage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Currentstage.close();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

}
