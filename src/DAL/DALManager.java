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

    public void createProjekt(String client, String projectName, int hourlyPay) {
                try ( Connection con = dbCon.getConnection())
        {

            String sql = "UPDATE PERSON SET salt = ?, password = ? WHERE person_id = ? ";

            PreparedStatement st = con.prepareStatement(sql);

            st.setString(1, client);
            st.setString(2, projectName);
            st.setInt(3, hourlyPay);

            st.executeQuery();

        } catch (Exception e)
        {
        }
    }
    
}
