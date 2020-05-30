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
import timetracker.GUI.Model.UserModel;

/**
 * FXML Controller class
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class BrugerManagerAdminController implements Initializable
{

    private static UserModel model;
    private ObservableList<Profession> allProf;
    private final int ADMINROLEID = 1;
    private final int USERROLEID = 2;

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
    public BrugerManagerAdminController() throws DALException, SQLException
    {
        model = UserModel.getInstance();
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
            Logger.getLogger(BrugerManagerAdminController.class.getName()).log(Level.SEVERE, null, ex);
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
     * Opdaterer en User med de inputs der er tastet. tjekker også om der er
     * indtastet valide fornavne, efternavne og email
     *
     * @param event
     * @throws DALException
     * @throws SQLException
     */
    @FXML
    private void handleUpdateUser(ActionEvent event) throws DALException, SQLException
    {
        User user = new User();

        if (model.valName(textfieldName.getText()) && model.valName(textfieldSurname.getText()) && model.valEmail(textfieldEmail.getText()))
        {
            if (model.valExistingEmailEdit(listUsers.getSelectionModel().getSelectedItem().getValue().getPersonId(), textfieldEmail.getText()))
            {
                user.setPersonId(listUsers.getSelectionModel().getSelectedItem().getValue().getPersonId());
                user.setName(textfieldName.getText());
                user.setSurname(textfieldSurname.getText());
                user.setEmail(textfieldEmail.getText());
                user.setProfessionId(listProfessions.getSelectionModel().getSelectedItem().getProfessionId());
                if (!checkboxAdminRole.isSelected())
                {
                    user.setRoleId(USERROLEID);
                } else
                {
                    user.setRoleId(ADMINROLEID);
                }
                model.editUser(user);
                populateTreeTable();
            } else
            {
                textfieldEmail.setText("Email Already Exist");
                textfieldEmail.setStyle("-fx-text-inner-color: red");
            }

        } else
        {
            if (model.valName(textfieldName.getText()) == false)
            {
                textfieldName.setText("Invalid Name");
                textfieldName.setStyle("-fx-text-inner-color: red");
            }
            if (model.valName(textfieldSurname.getText()) == false)
            {
                textfieldSurname.setText("Invalid Surname");
                textfieldSurname.setStyle("-fx-text-inner-color: red");
            }
            if (model.valEmail(textfieldEmail.getText()) == false)
            {
                textfieldEmail.setText("Invalid Email");
                textfieldEmail.setStyle("-fx-text-inner-color: red");
            }
        }

    }

    /**
     * Opretter en User med de inputs der er tastet. tjekker også om der er
     * indtastet valide fornavne, efternavne og email
     *
     * @param event
     * @throws DALException
     * @throws SQLException
     */
    @FXML
    private void handleCreateUser(ActionEvent event) throws DALException, SQLException
    {
        User user = new User();

        if (model.valName(textfieldName.getText()))
        {
            user.setName(textfieldName.getText());
        } else
        {
            textfieldName.setText("Invalid Name");
            textfieldName.setStyle("-fx-text-inner-color: red");
        }

        if (model.valName(textfieldSurname.getText()))
        {
            user.setSurname(textfieldSurname.getText());
        } else
        {
            textfieldSurname.setText("Invalid Surname");
            textfieldSurname.setStyle("-fx-text-inner-color: red");
        }

        if (model.valEmail(textfieldEmail.getText()))
        {
            user.setEmail(textfieldEmail.getText());
        } else
        {
            textfieldEmail.setText("Invalid Email");
            textfieldEmail.setStyle("-fx-text-inner-color: red");
        }

        if (user.getName() != null && user.getSurname() != null && user.getEmail() != null)
        {
            if (model.valExistingEmail(textfieldEmail.getText()))
            {
                user.setProfessionId(listProfessions.getSelectionModel().getSelectedItem().getProfessionId());
                if (!checkboxAdminRole.isSelected())
                {
                    user.setRoleId(USERROLEID);
                } else
                {
                    user.setRoleId(ADMINROLEID);
                }
                String inputPassword = JOptionPane.showInputDialog("Ønkset password");
                user.setPassword(inputPassword);
                model.createUser(user);
                populateTreeTable();
            } else
            {
                textfieldEmail.setText("Email Already Exist");
                textfieldEmail.setStyle("-fx-text-inner-color: red");
            }

        }

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

        int profession_id = listUsers.getSelectionModel().getSelectedItem().getValue().getProfessionId();

        for (int i = 0; i < allProf.size(); i++)
        {
            int prof = allProf.get(i).getProfessionId();
            if (profession_id == prof)
            {
                listProfessions.getSelectionModel().select(allProf.get(i));
            }
        }
        checkboxAdminRole.setSelected(chosenUser.getValue().isAdminCheck(chosenUser));
    }

    /**
     * Markerer alt i feltet for bedre brugervenlighed
     *
     * @param event
     */
    @FXML
    private void handleNameClicked(MouseEvent event)
    {
        textfieldName.selectAll();
        textfieldName.setStyle("-fx-text-inner-color: black");
    }

    /**
     * Markerer alt i feltet for bedre brugervenlighed
     *
     * @param event
     */
    @FXML
    private void handleSurnameClicked(MouseEvent event)
    {
        textfieldSurname.selectAll();
        textfieldSurname.setStyle("-fx-text-inner-color: black");
    }

    /**
     * Markerer alt i feltet for bedre brugervenlighed
     *
     * @param event
     */
    @FXML
    private void handleEmailClick(MouseEvent event)
    {
        textfieldEmail.selectAll();
        textfieldEmail.setStyle("-fx-text-inner-color: black");
    }

    /**
     * Metode til at fjerne en bruger med pop-up bekræftelse
     *
     * @param event
     */
    @FXML
    private void handleDisableUser(ActionEvent event)
    {
        int opt = JOptionPane.showConfirmDialog(null, "Vil du fjerne denne bruger?", "Fjern Bruger", JOptionPane.YES_NO_OPTION);
        if (opt == 0)
        {
            User disableUser = new User();
            TreeItem c = (TreeItem) listUsers.getSelectionModel().getSelectedItem();
            disableUser = listUsers.getSelectionModel().getSelectedItem().getValue();

            model.disableUser(disableUser);
            boolean remove = c.getParent().getChildren().remove(c);
        }

    }

}
