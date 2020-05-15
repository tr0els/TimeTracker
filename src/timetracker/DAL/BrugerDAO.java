/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import timetracker.BE.Profession;
import timetracker.BE.User;

/**
 *
 * @author Charlotte
 */
public class BrugerDAO {

    private DatabaseConnector dbCon;

    public BrugerDAO() throws DALException {
        dbCon = new DatabaseConnector();
    }

    /**
     * Opretter en User med det user objekt der bliver sendt ned igennem lagene.
     *
     * @param user
     * @param HashedPassword
     * @param salt
     * @throws timetracker.DAL.DALException
     */
    public void createUser(User user, byte[] HashedPassword, byte[] salt) throws DALException {
        try ( Connection con = dbCon.getConnection()) {
            String sql = "INSERT INTO Person (name, surname, email, password, active, role_id, profession_id, salt) VALUES (?,?,?,?,?,?,?,?);";

            PreparedStatement st = con.prepareStatement(sql);

            st.setString(1, user.getName());
            st.setString(2, user.getSurname());
            st.setString(3, user.getEmail());
            st.setBytes(4, HashedPassword);
            st.setInt(5, 1);
            st.setInt(6, user.getRole_id());
            st.setInt(7, user.getProfession_id());
            st.setBytes(8, salt);

            st.executeQuery();

        } catch (SQLException e) {
            throw new DALException("Kunne ikke oprette bruger");
        }
    }

    /**
     * Ændrer User med de ændringer der bliver sendt ned igennem lagene.
     *
     * @param user
     * @throws timetracker.DAL.DALException
     */
    public void editUser(User user) throws DALException {
        try ( Connection con = dbCon.getConnection()) {
            String sql = "UPDATE Person SET name = ?, surname = ?, email = ?, role_id = ?, profession_id = ? WHERE person_id = ?";

            PreparedStatement st = con.prepareStatement(sql);

            st.setString(1, user.getName());
            st.setString(2, user.getSurname());
            st.setString(3, user.getEmail());
            st.setInt(4, user.getRole_id());
            st.setInt(5, user.getProfession_id());
            st.setInt(6, user.getPerson_id());

            st.executeQuery();

        } catch (SQLException e) {
//            throw new DALException("Kunne ikke rette brugeren"); <- dette ødelægger et eller andet
        }
    }

    /**
     * Sletter User ud fra det User objekt der bliver sendt ned igennem lagene.
     *
     * @param user
     */
    public void deleteUser(User user) throws DALException {
        try ( Connection con = dbCon.getConnection()) {
            String sql = "DELETE FROM Person WHERE person_id = ?";

            PreparedStatement st = con.prepareStatement(sql);

            st.setInt(1, user.getPerson_id());

            st.executeQuery();

        } catch (SQLException e) {
            throw new DALException("Kunne ikke slette brugeren");
        }
    }

    /**
     * Returnerer en liste med alle User i databasen.
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public List<User> getUsers() throws DALException {
        ArrayList<User> allUsers = new ArrayList<>();

        try ( Connection con = dbCon.getConnection()) {
            String sql = "SELECT Person.person_id, name, surname, email, Person.role_id, role_name, Person.profession_id ,profession_name\n"
                    + "FROM Person, Profession, Role\n"
                    + "WHERE Person.role_id = Role.role_id\n"
                    + "AND Person.profession_id = Profession.profession_id;";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                User user = new User();
                user.setPerson_id(rs.getInt("person_id"));
                user.setName(rs.getString("name"));
                user.setSurname(rs.getString("surname"));
                user.setEmail(rs.getString("email"));
                user.setRole_id(rs.getInt("role_id"));
                user.setRole(rs.getString("role_name"));
                user.setProfession_id(rs.getInt("profession_id"));
                user.setProfession(rs.getString("profession_name"));

                allUsers.add(user);
            }
            return allUsers;
        } catch (SQLException ex) {
            throw new DALException("Kunne ikke hente brugerne fra Datbasen");
        }
        // return null;
    }

    /**
     * Returnerer en liste med Professions fra databasen.
     *
     * @return
     * @throws DALException
     */
    public List<Profession> getProfessions() throws DALException {
        ArrayList<Profession> allProfessions = new ArrayList<>();

        try ( Connection con = dbCon.getConnection()) {
            String sql = "SELECT * FROM Profession;";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                Profession profession = new Profession();
                profession.setProfession_id(rs.getInt("profession_id"));
                profession.setProfession_name(rs.getString("Profession_name"));

                allProfessions.add(profession);
            }
            return allProfessions;
        } catch (SQLException ex) {
            throw new DALException("Kunne ikke hente proffisionliste ud");
        }
        //return null;
    }

    /**
     * tjekker om det login info som kommer fra BLLManageren er det samme som
     * ligger på serveren. hvis ja returnere den et boolean som er true.
     *
     * @param email
     * @param hashedPassword
     * @return
     */
    public User login(String email, byte[] hashedPassword) {

        try ( Connection con = dbCon.getConnection()) {
            String sql = "SELECT * FROM PERSON WHERE email = ? AND password = ?";
            PreparedStatement st = con.prepareStatement(sql);
            st.setNString(1, email);
            st.setBytes(2, hashedPassword);
            ResultSet rs = st.executeQuery();

            if (rs.next() == false) {
                return null;
            } else {
                do {
                    User user = new User(rs.getInt("person_id"), rs.getString("name"), rs.getString("surname"), rs.getString("email"), rs.getInt("role_id"), rs.getInt("profession_id"));
                    String emailDAO = user.getEmail();
                    byte[] passwordDAO = rs.getBytes("password");

                    if (emailDAO.equals(email) && Arrays.equals(passwordDAO, hashedPassword)) {
                        return user;
                    }
                } while (rs.next());
            }
        } catch (DALException | SQLException ex) {
        }
        return null;
    }

    /**
     * henter det random "salt" som er tilsvarende den email som kommer fra
     * BLLManageren
     *
     * @param email
     * @return
     */
    public byte[] getSalt(String email) {
        byte[] salt = null;

        try ( Connection con = dbCon.getConnection()) {
            String sql = "SELECT email, salt FROM PERSON WHERE email = ?";
            PreparedStatement st = con.prepareStatement(sql);
            st.setNString(1, email);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                salt = rs.getBytes("salt");
            }
        } catch (Exception e) {
        }
        return salt;
    }

}
