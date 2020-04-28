/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
import DAL.DALException;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;

public class DatabaseConnector
{

    private SQLServerDataSource dataSource;

    /**
     * Læser vores DBSettings fil så vi kan forbinde til databasaen
     *
     * @throws DALException
     */
    public DatabaseConnector() throws DALException
    {
        try
        {
            Properties props = new Properties();
            props.load(new FileReader("DBSettings.txt"));
            dataSource = new SQLServerDataSource();
            dataSource.setDatabaseName(props.getProperty("database"));
            dataSource.setUser(props.getProperty("user"));
            dataSource.setPassword(props.getProperty("password"));
            dataSource.setServerName(props.getProperty("server"));
        } catch (IOException ex)
        {
            throw new DALException("forkert input, check username or password in file");
        }
    }

    /**
     * Prøver at lave en forbindelse til vores database og returnerer
     * forbindelsen
     *
     * @return
     * @throws DALException
     */
    public Connection getConnection() throws DALException
    {
        try
        {
            return dataSource.getConnection();
        } catch (SQLServerException ex)
        {
            throw new DALException("Kunne ikke oprette forbindelse til serveren");
        }
    }

}
