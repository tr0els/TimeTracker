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
import java.util.List;
import timetracker.BE.Client;

/**
 *
 * @author Charlotte
 */
public class ClientDAO {

    private DatabaseConnector dbCon;

    public ClientDAO() throws DALException {
        dbCon = new DatabaseConnector();
    }

    /**
     * Opretter en client med det client objekt der bliver sendt ned igennem
     * lagene.
     *
     * @param name
     * @param timepris
     * @param client
     * @return
     */
    public Client createClient(String name, int timepris) throws DALException {
        try ( Connection con = dbCon.getConnection()) {

            String sql = "INSERT INTO Client (client_name, default_rate) VALUES (?,?)";

            PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            st.setString(1, name);
            st.setInt(2, timepris);
            int affectedRows = st.executeUpdate();
            if (affectedRows == 1) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    Client client = new Client(id, name, timepris);
                    return client;
                }

            }

        } catch (SQLException e) {
            //throw new DALException("Kunne ikke oprette Klient");
        }
        return null;
    }

    /**
     * Ændrer client med de ændringer der bliver sendt ned igennem lagene.
     *
     * @param client
     */
    public void editClient(Client client) throws DALException {
        try ( Connection con = dbCon.getConnection()) {
            int cl_id = client.getClientId();
            String sql = "UPDATE Client SET client_name = ?, default_rate = ? WHERE client_id = " + cl_id + ";";

            PreparedStatement st = con.prepareStatement(sql);

            st.setString(1, client.getClientName());
            st.setInt(2, client.getDefaultRate());

            st.executeQuery();

        } catch (SQLException e) {
            //throw new DALException("Kunne ikke rette Klienten" + e);
        }
    }

    /**
     * Sletter client hvor fra det client_id der bliver sendt ned igennem
     * lagene.
     *
     * @param client
     */
    public void deleteClient(Client client) throws DALException {
        try ( Connection con = dbCon.getConnection()) {
            String sql = "DELETE FROM Client WHERE client_id = ?";

            PreparedStatement st = con.prepareStatement(sql);

            st.setInt(1, client.getClientId());

            st.executeQuery();

        } catch (SQLException e) {

            //throw new DALException("Kunne ikke Slette Klienten");
        }
    }

    /**
     * Returnerer en liste med alle Clients i databasen.
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public List<Client> getClients() throws DALException {
        ArrayList<Client> allClients = new ArrayList<>();

        try ( Connection con = dbCon.getConnection()) {
            String sql = "SELECT * FROM Client;";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                Client clients = new Client();
                clients.setClientId(rs.getInt("client_id"));
                clients.setClientName(rs.getString("client_name"));
                clients.setDefaultRate(rs.getInt("default_rate"));

                allClients.add(clients);
            }
            return allClients;
        } catch (SQLException ex) {
            //throw new DALException("Kunne ikke hente Klienter");
        }
        
        return null;
    }

}
