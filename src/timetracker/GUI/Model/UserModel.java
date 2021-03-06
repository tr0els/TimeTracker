/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Model;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.YearMonth;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import timetracker.BE.Profession;
import timetracker.BE.User;
import timetracker.BLL.BLLManager;
import timetracker.BLL.InputValidator;
import timetracker.DAL.DALException;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class UserModel
{

    private ObservableList<Profession> allProfessions;
    private ObservableList<User> allUsers;
    private ObservableList<Month> listOfMonths;
    private ObservableList<YearMonth> listOfMonthswithYears;
    private static UserModel model = null;
    private static BLLManager bll;
    private static InputValidator validator;
    private User user;

    /**
     * Constructor for UserModel
     *
     * @throws DALException
     */
    public UserModel() throws DALException
    {
        bll = BLLManager.getInstance();
        validator = InputValidator.getInstance();
        allUsers = FXCollections.observableArrayList();
        allUsers.addAll(bll.getUsers());
        allProfessions = FXCollections.observableArrayList();
        allProfessions.addAll(bll.getProfessions());
        listOfMonths = FXCollections.observableArrayList();
        listOfMonthswithYears = FXCollections.observableArrayList();
    }

    /**
     * Returnerer den allerede startet instans af klassen, eller starter en ny.
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public static UserModel getInstance() throws DALException, SQLException
    {
        if (model == null)
        {
            model = new UserModel();
        }
        return model;
    }

    /**
     * sender det info fra MainControlleren videre til BLLManager
     *
     * @param email
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     * @throws timetracker.DAL.DALException
     */
    public User login(String email, String password) throws NoSuchAlgorithmException, DALException
    {
        user = validator.login(email, password);
        return user;
    }

    /**
     * Returnerer den User der er logget ind i programmet.
     *
     * @return
     */
    public User getUser()
    {
        return user;
    }

    /**
     * Sender User objekt ned til DAL laget som skal oprettes.
     *
     * @param user
     */
    public void createUser(User user)
    {
        bll.createUser(user);
    }

    /**
     * Sender User objekt ned til DAL laget som skal ændres.
     *
     * @param user
     * @throws timetracker.DAL.DALException
     */
    public void editUser(User user) throws DALException
    {
        bll.editUser(user);
    }

    /**
     * Sender Client objekt ned til DAL laget som skal slettes.
     *
     * @param user
     */
    public void deleteUser(User user) throws DALException
    {
        bll.deleteUser(user);
    }

    /**
     * Henter listen af Users nede fra DAL laget
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public ObservableList<User> getUsers() throws DALException
    {
        allUsers.clear();
        allUsers.addAll(bll.getUsers());
        Comparator<User> byName = (User cl1, User cl2) -> cl1.getName().compareTo(cl2.getName());
        allUsers.sort(byName);
        return allUsers;
    }

    /**
     * Returnerer en liste med de forskellige Professions.
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public ObservableList<Profession> getProfessions() throws DALException
    {
        return allProfessions;
    }

    /**
     * sender en inputet email ned til vores validator for at tjekke om den har
     * en email struktur
     *
     * @param email
     * @return
     */
    public boolean valEmail(String email)
    {
        return validator.valEmail(email);
    }

    /**
     * sender det inputtet navn/efternavn ned for at tjekke at det ikke har
     * numrer og spaces.
     *
     * @param name
     * @return
     */
    public boolean valName(String name)
    {
        return validator.valName(name);
    }

    /**
     * Tjekker om den indtastet email allerede er oprettet.
     *
     * @param email
     * @return
     */
    public boolean valExistingEmail(String email)
    {
        return validator.valExistingEmail(email);
    }

    /**
     * Returnerer en liste med de sidste 12 måneder.
     *
     * @return
     */
    public ObservableList<YearMonth> getListOfPeriods()
    {
        for (int i = 0; i < 12; i++)
        {
            YearMonth monthwithyear = YearMonth.now().minus(Period.ofMonths(i));
            Month addMonth = LocalDate.now().getMonth().minus(i);
            listOfMonthswithYears.add(monthwithyear);
        }
        return listOfMonthswithYears;
    }

    /**
     * Tjekker om den email der er indtastet allerede eksisterer i databasen et
     * User objekt
     *
     * @param person_id
     * @param email
     * @return
     */
    public boolean valExistingEmailEdit(int person_id, String email)
    {
        return validator.valExistingEmailEdit(person_id, email);
    }

    /**
     * Fjerner en User
     *
     * @param disableUser
     */
    public void disableUser(User disableUser)
    {
        bll.disableUser(disableUser);
    }

}
