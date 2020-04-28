/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 *
 * @author Draik
 */
public class DALManager {

    private DatabaseConnector dbCon;

    public DALManager() throws DALException {
        dbCon = new DatabaseConnector();
    }

    public void createProjekt(int clientID, String projectName, int hourlyPay) {
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
