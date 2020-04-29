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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import timetracker.BE.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import timetracker.BE.Client;
import timetracker.BE.Project;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class DALManager {

    /**
     * Singleton opsætning af vores DALManager. singleton gør at vores
     * dalmanager ikke vil blive instansieret mere end en gang.
     */
    private DatabaseConnector dbCon;
    private static DALManager dal = null;

    private DALManager() throws DALException {
        dbCon = new DatabaseConnector();
    }

    public static DALManager getInstance() throws DALException {
        if (dal == null) {
            dal = new DALManager();
        }
        return dal;
    }

    /**
     * Tager imod infoen fra BLLManagerens "createProject" og sætter det ind i
     * en prepared statement så det vil blive gemt på databasen.
     *
     * @param clientID
     * @param projectName
     * @param hourlyPay
     */
    public void createProject(int clientID, String projectName, int hourlyPay) {
        try ( Connection con = dbCon.getConnection()) {

            String sql = "INSERT INTO Project (project_name, project_rate, client_id) VALUES (?,?,?)";

            PreparedStatement st = con.prepareStatement(sql);

            st.setString(1, projectName);
            st.setInt(2, hourlyPay);
            st.setInt(3, clientID);

            st.executeQuery();

        } catch (Exception e) {
        }
    }

    /**
     * tager imod infoen fra BLLManagerens "deleteProject" og sætter det ind i
     * en prepared statement så det vil blive slettet fra databasen.
     *
     * @param clientID
     * @param projectName
     * @param hourlyPay
     */
    public void deleteProject(int clientID, String projectName, int hourlyPay) {
        try ( Connection con = dbCon.getConnection()) {

            String sql = "DELETE FROM Project WHERE Project_name = ? AND project_rate = ? AND client_id = ?";

            PreparedStatement st = con.prepareStatement(sql);

            st.setString(1, projectName);
            st.setInt(2, hourlyPay);
            st.setInt(3, clientID);

            st.executeQuery();

        } catch (Exception e) {
        }
    }

    /**
     * opretter ny task og returnere task
     *
     * @param task_name
     * @param billable
     * @param project_id
     * @param person_id
     * @return
     */
    public Task createTask(String task_name, boolean billable, int project_id, int person_id) {
        try ( Connection con = dbCon.getConnection()) {

            Task task = null;

            int int_billable = 0;//konvertere boolean til 0 el. 1
            if (billable == true) {
                int_billable = 1;
            }

            String sql = "INSERT INTO Task (task_name, billable, project_id, person_id) VALUES (?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setString(1, task_name);
            ps.setInt(2, int_billable);
            ps.setInt(3, project_id);
            ps.setInt(4, person_id);

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int task_id = rs.getInt(1);

                    startTask(task_id);

                    task = new Task(task_id, task_name, billable, project_id, person_id);

                }

            }

            return task;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * tager imod infoen fra BLLManagerens "editProject" og sætter det ind i en
     * prepared statement så infoen kan updateres i databasen.
     *
     * @param clientID
     * @param projectName
     * @param hourlyPay
     * @param projectID
     */

    public void editProject(int clientID, String projectName, int hourlyPay, int projectID) {
        try ( Connection con = dbCon.getConnection()) {

            String sql = "UPDATE Project SET Project_name = ?, project_rate = ?, client_id = ? WHERE project_id = ?";

            PreparedStatement st = con.prepareStatement(sql);

            st.setString(1, projectName);
            st.setInt(2, hourlyPay);
            st.setInt(3, clientID);
            st.setInt(4, projectID);

            st.executeQuery();

        } catch (Exception e) {
        }
    }

    /**
     * Henter alt projekt data som ligger gemt på serveren og putter det ind i
     * en ArrayList.
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public List<Project> getProjects() throws DALException, SQLException {
        ArrayList<Project> allProjects = new ArrayList<>();

        try ( Connection con = dbCon.getConnection()) {
            String sql = "SELECT * FROM Project;";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                Project projects = new Project();
                projects.setProject_id(rs.getInt("project_id"));
                projects.setProject_name(rs.getString("project_name"));
                projects.setProject_rate(rs.getInt("project_rate"));

                allProjects.add(projects);
            }
            return allProjects;
        } catch (DALException | SQLException ex) {
            Logger.getLogger(DALManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Opretter en client med det client objekt der bliver sendt ned igennem
     * lagene.
     *
     * @param client
     */
    public void createClient(Client client) {
        try ( Connection con = dbCon.getConnection()) {
            String sql = "INSERT INTO Client (client_name, default_rate) VALUES (?,?)";

            PreparedStatement st = con.prepareStatement(sql);

            st.setString(1, client.getClient_name());
            st.setInt(2, client.getDefault_rate());

            st.executeQuery();

        } catch (Exception e) {
        }
    }

    /**
     * opretter starttidspunkt for ny task
     *
     * @param task_id
     */
    public void startTask(int task_id) {
        try ( Connection con = dbCon.getConnection()) {

            String sql = "INSERT INTO Task_log (task_id, task_start) VALUES (?, CURRENT_TIMESTAMP)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, task_id);

            ps.execute();

        } catch (Exception e) {

        }

    }

    /**
     * indsætter pause/stop tidspunkt task på task_id hvor task_end ikke er sat
     * endnu.
     *
     * @param task_id
     */
    public void pauseTask(int task_id) {
        try ( Connection con = dbCon.getConnection()) {

            String sql = "UPDATE Task_log SET task_end=CURRENT_TIMESTAMP WHERE task_id = ? AND task_end is null";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, task_id);

            ps.execute();

        } catch (Exception e) {
        }
    }

    public void getLogs() {
        try ( Connection con = dbCon.getConnection()) {

        } catch (Exception e) {
        }

    }

    /**
     * Ændrer client med de ændringer der bliver sendt ned igennem lagene.
     *
     * @param client
     */
    public void editClient(Client client) {
        try ( Connection con = dbCon.getConnection()) {
            String sql = "UPDATE Client SET client_name = ?, default_rate = ? WHERE client_id = ?";

            PreparedStatement st = con.prepareStatement(sql);

            st.setString(1, client.getClient_name());
            st.setInt(2, client.getDefault_rate());

            st.executeQuery();

        } catch (Exception e) {
        }
    }

    /**
     * Sletter client hvor fra det client_id der bliver sendt ned igennem
     * lagene.
     *
     * @param client
     */
    public void deleteClient(Client client) {
        try ( Connection con = dbCon.getConnection()) {
            String sql = "DELETE FROM Client WHERE client_id = ?";

            PreparedStatement st = con.prepareStatement(sql);

            st.setInt(1, client.getClient_id());

            st.executeQuery();

        } catch (Exception e) {
        }
    }

    /**
     * Returnerer en liste med alle Clients i databasen.
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public List<Client> getClients() throws DALException, SQLException {
        ArrayList<Client> allClients = new ArrayList<>();

        try ( Connection con = dbCon.getConnection()) {
            String sql = "SELECT * FROM Client;";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                Client clients = new Client();
                clients.setClient_id(rs.getInt("client_id"));
                clients.setClient_name(rs.getString("client_name"));
                clients.setDefault_rate(rs.getInt("default_rate"));

                allClients.add(clients);
            }
            return allClients;
        } catch (DALException | SQLException ex) {
        }
        return null;
    }
}
