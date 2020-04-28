/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;

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

}
