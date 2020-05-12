/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.GUI.Model;

import java.sql.SQLException;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import timetracker.BE.Profession;
import timetracker.BE.User;
import timetracker.BLL.BLLManager;
import timetracker.DAL.DALException;

/**
 *
 * @author Charlotte
 */
public class BrugerModel {

    private ObservableList<Profession> allProfessions;
    private ObservableList<User> allUsers;
    private static BrugerModel model = null;
    private static BLLManager bll;

    public static BrugerModel getInstance() throws DALException, SQLException {
        if (model == null) {
            model = new BrugerModel();
        }
        return model;
    }

    public BrugerModel() throws DALException, SQLException {
        bll = BLLManager.getInstance();
        allUsers = FXCollections.observableArrayList();
        allUsers.addAll(bll.getUsers());
        allProfessions = FXCollections.observableArrayList();

    }
    
     /**
     * Sender User objekt ned til DAL laget som skal oprettes.
     *
     * @param client
     */
    public void createUser(User user)
    {
        bll.createUser(user);
    }

    /**
     * Sender User objekt ned til DAL laget som skal ændres.
     *
     * @param client
     */
    public void editUser(User user)
    {
        bll.editUser(user);
    }

    /**
     * Sender Client objekt ned til DAL laget som skal slettes.
     *
     * @param client
     */
    public void deleteUser(User user)
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
    public ObservableList<User> getUsers() throws DALException, SQLException
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
    public ObservableList<Profession> getProfessions() throws DALException, SQLException
    {
        allProfessions.addAll(bll.getProfessions());
        return allProfessions;
    }

}