/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.DAL;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import timetracker.BE.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import timetracker.BE.Client;
import timetracker.BE.Project;
import timetracker.BE.Task.Log;
import timetracker.BE.User;

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
    public void deleteProject(int projectID) {
        try ( Connection con = dbCon.getConnection()) {

            String sql = "DELETE FROM Project WHERE project_id = ?";

            PreparedStatement st = con.prepareStatement(sql);

            st.setInt(1, projectID);

            st.execute();

        } catch (Exception e) {
            System.out.println("fail lol");
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
                projects.setClient_id(rs.getInt("client_id"));

                allProjects.add(projects);
            }
            return allProjects;
        } catch (DALException | SQLException ex) {
            Logger.getLogger(DALManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Project getProject(String projectName, int project_rate, int client_id) {
        try ( Connection con = dbCon.getConnection()) {
            
            String sql = "SELECT * FROM Project WHERE project_name = ? AND project_rate = ? AND client_id = ?;";

            PreparedStatement st = con.prepareStatement(sql);

            st.setString(1, projectName);
            st.setInt(2, project_rate);
            st.setInt(3, client_id);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Project project = new Project();
                project.setProject_id(rs.getInt("project_id"));
                project.setProject_name(rs.getString("project_name"));
                project.setProject_rate(rs.getInt("project_rate"));
                project.setClient_id(rs.getInt("client_id"));
                return project;
            }

        } catch (DALException | SQLException ex) {
            Logger.getLogger(DALManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
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
    public void createTask(String task_name, boolean billable, int project_id, int person_id) {
        try ( Connection con = dbCon.getConnection()) {

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
                }
            }
        } catch (Exception e) {
        }
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

    public Client createClient(String name, int timepris)
    {
        try ( Connection con = dbCon.getConnection())
        {

            String sql = "INSERT INTO Client (client_name, default_rate) VALUES (?,?)";

            PreparedStatement st = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);


            st.setString(1, name);
            st.setInt(2, timepris);
            int affectedRows = st.executeUpdate();
            if(affectedRows == 1)
            {
            ResultSet rs = st.getGeneratedKeys();
            if(rs.next())
            {
                int id = rs.getInt(1);
                Client client = new Client(id,name, timepris );
                return client;
            }
          
            
            }
       
        } catch (Exception e)
        {
           

        }
          return null;
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

    /**
     * returnere en specifik task udfra task_id
     *
     * @param task_id
     * @return
     */
    public Task getTaskbyTaskID(int task_id) {
        Task task = new Task();

        try ( Connection con = dbCon.getConnection()) {

            String sql = "SELECT * FROM Task WHERE task_id = " + task_id + ";";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                boolean billable = false; //konvertere billable til boolean fra int. 
                if (rs.getInt("billable") == 1) {
                    billable = true;
                }
                task.setTask_id(rs.getInt("task_id"));
                task.setTask_name(rs.getString("task_name"));
                task.setBillable(billable);
                task.setPerson_id(rs.getInt("person_id"));
                task.setProject_id(rs.getInt("project_id"));
            }

        } catch (Exception e) {
        }
        return task;
    }

    /**
     * henter en liste af Tasks fra DB via et project_id og returnere listen
     *
     * @param project_id
     * @return
     */
    public List<Task> getTaskbyProjectID(int project_id) {
        ArrayList<Task> taskbyID = new ArrayList<>();

        try ( Connection con = dbCon.getConnection()) {

            String sql = "SELECT * FROM Task WHERE project_id = " + project_id + ";";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                Task task = new Task();

                boolean billable = false; //konvertere billable til boolean fra int. 
                if (rs.getInt("billable") == 1) {
                    billable = true;
                }

                task.setTask_id(rs.getInt("task_id"));
                task.setTask_name(rs.getString("task_name"));
                task.setProject_id(rs.getInt("project_id"));
                task.setPerson_id(rs.getInt("person_id"));
                task.setBillable(billable);

                taskbyID.add(task);
            }
            return taskbyID;

        } catch (Exception e) {
        }
        return null;
    }

    /**
     * henter en liste af Logs fra DB via et task_id og returnere listen
     *
     * @param task_id
     * @return
     */
    public List<Log> getTaskLogListbyTaskID(int task_id) {
        ArrayList<Log> tasklogbyID = new ArrayList<>();

        try ( Connection con = dbCon.getConnection()) {

            String sql = "SELECT * FROM Task_log WHERE task_id = ?;";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, task_id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Log log = new Log();

                LocalDateTime end_time;
                if (rs.getTimestamp("task_end") != null)
                {
                    end_time = rs.getTimestamp("task_end").toLocalDateTime();
                }else{
                    end_time = null;
                }
                
                log.setStart_time(rs.getTimestamp("task_start").toLocalDateTime());
                log.setEnd_time(end_time);

                tasklogbyID.add(log);
            }

        } catch (Exception e) {
        }

        return tasklogbyID;
    }

    /**
     * henter en liste af task ud fra person_id og dag (0 = idag, 1 = igår osv.)
     *
     * @return
     */
    public List<Log> getTaskLogListbyDay(int person_id, int dag) {
        ArrayList<Log> tasklogbyDay = new ArrayList<>();

        try ( Connection con = dbCon.getConnection()) {

            String sql = "SELECT Task_log.*, CAST(task_end - task_start AS TIME(0)) AS total_time FROM Task_log\n"
                    + "JOIN Task ON Task.task_id = Task_log.task_id\n"
                    + "WHERE CAST(task_start AS DATE) = DATEADD(day, -"+dag+", CONVERT(date, GETDATE()))\n"
                    + "AND person_id = ? ORDER BY task_start DESC";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, person_id);
            

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Log log = new Log();
                
                LocalDateTime end_time;
                Time total_time;
                if (rs.getTimestamp("task_end") != null)
                {
                    end_time = rs.getTimestamp("task_end").toLocalDateTime();
                    total_time = rs.getTime("total_time");
                }else{
                    end_time = null;
                    total_time = null;
                }

                log.setTotal_tid(total_time);
                log.setStart_time(rs.getTimestamp("task_start").toLocalDateTime());
                log.setEnd_time(end_time);
                log.setTask_id(rs.getInt("task_id"));

                tasklogbyDay.add(log);
            }

        } catch (Exception e) {
        }
        
        return tasklogbyDay;
    }

    /**
     * Ændrer client med de ændringer der bliver sendt ned igennem lagene.
     *
     * @param client
     */
    public void editClient(Client client) {
        try ( Connection con = dbCon.getConnection()) {
            int cl_id = client.getClient_id();
            String sql = "UPDATE Client SET client_name = ?, default_rate = ? WHERE client_id = " + cl_id + ";";

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

    /**
     * Opretter en User med det user objekt der bliver sendt ned igennem lagene.
     *
     * @param client
     */
    public void createUser(User user) {
        try ( Connection con = dbCon.getConnection()) {
            String sql = "INSERT INTO Person (name, surname, email, password, role_id, profession_id) VALUES (?,?,?,?,?,?)";

            PreparedStatement st = con.prepareStatement(sql);

            st.setString(1, user.getName());
            st.setString(2, user.getSurname());
            st.setString(3, user.getEmail());
            st.setString(4, user.getPassword());
            st.setInt(5, user.getRole_id());
            st.setInt(6, user.getProfession_id());

            st.executeQuery();

        } catch (Exception e) {
        }
    }

    /**
     * Ændrer User med de ændringer der bliver sendt ned igennem lagene.
     *
     * @param client
     */
    public void editUser(User user) {
        try ( Connection con = dbCon.getConnection()) {
            int person_id = user.getPerson_id();
            String sql = "UPDATE Person SET name = ?, surname = ?, email = ?, role_id = ? WHERE person_id = " + person_id + ";";

            PreparedStatement st = con.prepareStatement(sql);

            st.setString(1, user.getName());
            st.setString(2, user.getSurname());
            st.setString(3, user.getEmail());
            st.setInt(4, user.getRole_id());
//            st.setInt(5, user.getProfession_id());

            st.executeQuery();

        } catch (Exception e) {
        }
    }

    /**
     * Sletter User ud fra det User objekt der bliver sendt ned igennem lagene.
     *
     * @param client
     */
    public void deleteUser(User user) {
        try ( Connection con = dbCon.getConnection()) {
            String sql = "DELETE FROM Person WHERE person_id = ?";

            PreparedStatement st = con.prepareStatement(sql);

            st.setInt(1, user.getPerson_id());

            st.executeQuery();

        } catch (Exception e) {
        }
    }

    /**
     * Returnerer en liste med alle User i databasen.
     *
     * @return
     * @throws DALException
     * @throws SQLException
     */
    public List<User> getUsers() throws DALException, SQLException {
        ArrayList<User> allUsers = new ArrayList<>();

        try ( Connection con = dbCon.getConnection())
        {
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
        } catch (DALException | SQLException ex) {
        }
        return null;
    }

    public List<Project> getProjectsbyClientID(Client client) {
        ArrayList<Project> allProjectswithClientID = new ArrayList<>();
        int client_ID = client.getClient_id();
        try ( Connection con = dbCon.getConnection()) {
            String sql = "SELECT * FROM Project WHERE client_id =  " + client_ID + ";";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                Project projects = new Project();
                projects.setProject_id(rs.getInt("project_id"));
                projects.setProject_name(rs.getString("project_name"));
                projects.setProject_rate(rs.getInt("project_rate"));
                projects.setClient_id(rs.getInt("client_id"));

                allProjectswithClientID.add(projects);
            }
            return allProjectswithClientID;
        } catch (DALException | SQLException ex) {
            Logger.getLogger(DALManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public List<String> getProfessions() throws DALException, SQLException
    {
        ArrayList<String> allProfessions = new ArrayList<>();

        try ( Connection con = dbCon.getConnection())
        {
            String sql = "SELECT * FROM Profession;";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next())
            {
                allProfessions.add(rs.getString("profession_name"));
            }
            return allProfessions;
        } catch (DALException | SQLException ex)
        {
        }
        return null;
    }
}
