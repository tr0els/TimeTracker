/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Controller;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.sun.javafx.tools.packager.bundlers.BundleParams;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
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

    @FXML
    private AnchorPane root;
    @FXML
    private JFXTreeTableView<User> listUsers;

    private static TaskModel model;

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
}
