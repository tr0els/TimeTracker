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
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javax.swing.JOptionPane;
import timetracker.BE.Profession;
import timetracker.BE.User;
import timetracker.DAL.DALException;
import timetracker.GUI.Model.BrugerModel;
import timetracker.GUI.Model.TaskModel;

/**
 * FXML Controller class
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class UserManagerAdminController implements Initializable
{

    private static BrugerModel model;
    private ObservableList<Profession> allProf;

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
    private JFXComboBox<Profession> listProfessions;
    @FXML
    private JFXCheckBox checkboxAdminRole;

    /**
     * Constructor for UserManagerAdminController.
     *
     * @throws DALException
     * @throws SQLException
     */
    public UserManagerAdminController() throws DALException, SQLException
    {
        model = BrugerModel.getInstance();
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
            allProf = model.getProfessions();
            listProfessions.setItems(allProf);

        } catch (DALException | SQLException ex)
        {
            Logger.getLogger(UserManagerAdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sætter JFXTreeTable op med de kolonner og populerer den med data.
     *
     * @throws DALException
     * @throws SQLException
     */
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

    /**
     * Opdaterer en User med de inputs der er tastet.
     *
     * @param event
     * @throws DALException
     * @throws SQLException
     */
    @FXML
    private void handleUpdateUser(ActionEvent event) throws DALException, SQLException
    {
        User user = new User();
        user.setPerson_id(listUsers.getSelectionModel().getSelectedItem().getValue().getPerson_id());
        user.setName(textfieldName.getText());
        user.setSurname(textfieldSurname.getText());
        user.setEmail(textfieldEmail.getText());
        user.setProfession_id(listProfessions.getSelectionModel().getSelectedItem().getProfession_id());
        if (!checkboxAdminRole.isSelected())
        {
            user.setRole_id(2);
        } else
        {
            user.setRole_id(1);
        }
        System.out.println("Så langt så godt.");
        model.editUser(user);

        populateTreeTable();
    }

    /**
     * Opretter en User med de inputs der er tastet.
     *
     * @param event
     * @throws DALException
     * @throws SQLException
     */
    @FXML
    private void handleCreateUser(ActionEvent event) throws DALException, SQLException
    {
        User user = new User();
        user.setName(textfieldName.getText());
        user.setSurname(textfieldSurname.getText());
        user.setEmail(textfieldEmail.getText());
        user.setProfession_id(listProfessions.getSelectionModel().getSelectedItem().getProfession_id());
        if (!checkboxAdminRole.isSelected())
        {
            user.setRole_id(2);
        } else
        {
            user.setRole_id(1);
        }
        String inputPassword = JOptionPane.showInputDialog("Ønkset password");
        user.setPassword(inputPassword);
        model.createUser(user);
        System.out.println("Controller");
        populateTreeTable();
    }

    /**
     * Opdaterer de forskellige input-felter med den data der tilhører det
     * objekt der bliver trykket på i JFXTreeTable.
     *
     * @param event
     * @throws DALException
     * @throws SQLException
     */
    @FXML
    private void handleSelectionTreeTable(MouseEvent event) throws DALException, SQLException
    {
        TreeItem<User> chosenUser;
        chosenUser = listUsers.getSelectionModel().getSelectedItem();
        textfieldName.setText(chosenUser.getValue().getName());
        textfieldSurname.setText(chosenUser.getValue().getSurname());
        textfieldEmail.setText(chosenUser.getValue().getEmail());

        int profession_id = listUsers.getSelectionModel().getSelectedItem().getValue().getProfession_id();

        for (int i = 0; i < allProf.size(); i++)
        {
            int prof = allProf.get(i).getProfession_id();
            if (profession_id == prof)
            {
                listProfessions.getSelectionModel().select(allProf.get(i));
            }
        }
        checkboxAdminRole.setSelected(chosenUser.getValue().isAdminCheck(chosenUser));
    }
}
