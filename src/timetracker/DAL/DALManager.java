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

/**
 *
 * @author Draik
 */
public class DALManager {

    /**
     * Singleton opsætning af vores DALManager. singleton gør at vores dalmanager ikke vil
     * blive instansieret mere end en gang.
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
     * indsætter pause/stop tidspunkt task på task_id hvor task_end ikke er sat endnu.
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
}
