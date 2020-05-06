/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javax.swing.JOptionPane;
import timetracker.BE.User;
import timetracker.DAL.DALException;
import timetracker.GUI.Model.TaskModel;

/**
 * FXML Controller class
 *
 * @author Charlotte
 */
public class UserManagerAdminController implements Initializable
{

    private static TaskModel model;

    @FXML
    private AnchorPane root;
    @FXML
    private JFXTreeTableView<User> listUsers;
    @FXML
    private JFXTextField textfieldName;
    @FXML
    private JFXTextField textfieldSurname;
    @FXML
    private JFXTextField textfieldEmail;
    @FXML
    private JFXComboBox<String> listProfessions;
    @FXML
    private JFXCheckBox checkboxAdminRole;

    public UserManagerAdminController() throws DALException, SQLException
    {
        model = TaskModel.getInstance();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        
        try
        {
            populateTreeTable();
            listProfessions.setItems(model.getProfessions());
            
        } catch (DALException | SQLException ex)
        {
            Logger.getLogger(UserManagerAdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void populateTreeTable() throws DALException, SQLException
    {
        JFXTreeTableColumn<User, String> userName = new JFXTreeTableColumn<>("Navn");
        userName.setPrefWidth(75);
        userName.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<User, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<User, String> param)
            {
                return new SimpleStringProperty(param.getValue().getValue().getName());
            }
        });

        JFXTreeTableColumn<User, String> userSurName = new JFXTreeTableColumn<>("Efternavn");
        userSurName.setPrefWidth(100);
        userSurName.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<User, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<User, String> param)
            {
                return new SimpleStringProperty(param.getValue().getValue().getSurname());
            }
        });

        JFXTreeTableColumn<User, String> userEmail = new JFXTreeTableColumn<>("Email");
        userEmail.setPrefWidth(150);
        userEmail.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<User, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<User, String> param)
            {
                return new SimpleStringProperty(param.getValue().getValue().getEmail());
            }
        });

        JFXTreeTableColumn<User, String> userProfession = new JFXTreeTableColumn<>("Profession");
        userProfession.setPrefWidth(150);
        userProfession.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<User, String>, ObservableValue<String>>()
        {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<User, String> param)
            {
                return new SimpleStringProperty(param.getValue().getValue().getProfession());
            }
        });

        final TreeItem<User> root = new RecursiveTreeItem<User>(model.getUsers(), RecursiveTreeObject::getChildren);
        listUsers.getColumns().setAll(userName, userSurName, userEmail, userProfession);
        listUsers.setRoot(root);
        listUsers.setShowRoot(false);
    }

    @FXML
    private void handleUpdateUser(ActionEvent event) throws DALException, SQLException
    {
        User user = new User();
        user.setPerson_id(listUsers.getSelectionModel().getSelectedItem().getValue().getPerson_id());
        user.setName(textfieldName.getText());
        user.setSurname(textfieldSurname.getText());
        user.setEmail(textfieldEmail.getText());
//        user.setProfession(listProfessions.getSelectionModel().getSelectedItem());
        if (!checkboxAdminRole.isSelected()){
            user.setRole_id(2);
        }
        else {
            user.setRole_id(1);
        }
        System.out.println("Så langt så godt.");
        model.editUser(user);
        
        populateTreeTable();
    }

    @FXML
    private void handleCreateUser(ActionEvent event) throws DALException, SQLException
    {
        User user = new User();
        user.setName(textfieldName.getText());
        user.setSurname(textfieldSurname.getText());
        user.setEmail(textfieldEmail.getText());
        user.setProfession(listProfessions.getSelectionModel().getSelectedItem());
        if (!checkboxAdminRole.isSelected()){
            user.setRole_id(2);
        }
        else {
            user.setRole_id(1);
        }
        String inputPassword = JOptionPane.showInputDialog("Ønkset password");
        user.setPassword(inputPassword);
        model.createUser(user);
        populateTreeTable();
    }

    @FXML
    private void handleSelectionTreeTable(MouseEvent event)
    {
        TreeItem<User> chosenUser;
        chosenUser = listUsers.getSelectionModel().getSelectedItem();
        textfieldName.setText(chosenUser.getValue().getName());
        textfieldSurname.setText(chosenUser.getValue().getSurname());
        textfieldEmail.setText(chosenUser.getValue().getEmail());
        listProfessions.setValue(chosenUser.getValue().getProfession());
        checkboxAdminRole.setSelected(chosenUser.getValue().isAdminCheck(chosenUser));
    }
}
