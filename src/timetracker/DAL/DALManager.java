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
import java.sql.Time;
import java.time.LocalDateTime;
import timetracker.BE.Task;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import timetracker.BE.Client;
import timetracker.BE.Profession;
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

//    /**
//     * Tager imod infoen fra BLLManagerens "createProject" og sætter det ind i
//     * en prepared statement så det vil blive gemt på databasen.
//     *
//     * @param clientID
//     * @param projectName
//     * @param hourlyPay
//     */
//    public void createProject(int clientID, String projectName, int hourlyPay) {
//        try ( Connection con = dbCon.getConnection()) {
//
//            String sql = "INSERT INTO Project (project_name, project_rate, client_id) VALUES (?,?,?)";
//
//            PreparedStatement st = con.prepareStatement(sql);
//
//            st.setString(1, projectName);
//            st.setInt(2, hourlyPay);
//            st.setInt(3, clientID);
//
//            st.executeQuery();
//
//        } catch (Exception e) {
//        }
//    }
//
//    /**
//     * tager imod infoen fra BLLManagerens "deleteProject" og sætter det ind i
//     * en prepared statement så det vil blive slettet fra databasen.
//     *
//     * @param clientID
//     * @param projectName
//     * @param hourlyPay
//     */
//    public void deleteProject(int projectID) {
//        try ( Connection con = dbCon.getConnection()) {
//
//            String sql = "DELETE FROM Project WHERE project_id = ?";
//
//            PreparedStatement st = con.prepareStatement(sql);
//
//            st.setInt(1, projectID);
//
//            st.execute();
//
//        } catch (Exception e) {
//            System.out.println("fail lol");
//        }
//    }
//
//    /**
//     * tager imod infoen fra BLLManagerens "editProject" og sætter det ind i en
//     * prepared statement så infoen kan updateres i databasen.
//     *
//     * @param clientID
//     * @param projectName
//     * @param hourlyPay
//     * @param projectID
//     */
//    public void editProject(int clientID, String projectName, int hourlyPay, int projectID) {
//        try ( Connection con = dbCon.getConnection()) {
//
//            String sql = "UPDATE Project SET Project_name = ?, project_rate = ?, client_id = ? WHERE project_id = ?";
//
//            PreparedStatement st = con.prepareStatement(sql);
//
//            st.setString(1, projectName);
//            st.setInt(2, hourlyPay);
//            st.setInt(3, clientID);
//            st.setInt(4, projectID);
//
//            st.executeQuery();
//
//        } catch (Exception e) {
//        }
//    }
//
//    /**
//     * Henter alt projekt data som ligger gemt på serveren og putter det ind i
//     * en ArrayList.
//     *
//     * @return
//     * @throws DALException
//     * @throws SQLException
//     */
//    public List<Project> getProjects() throws DALException, SQLException {
//        ArrayList<Project> allProjects = new ArrayList<>();
//
//        try ( Connection con = dbCon.getConnection()) {
//            String sql = "SELECT * FROM Project;";
//            Statement statement = con.createStatement();
//            ResultSet rs = statement.executeQuery(sql);
//            while (rs.next()) {
//                Project projects = new Project();
//                projects.setProject_id(rs.getInt("project_id"));
//                projects.setProject_name(rs.getString("project_name"));
//                projects.setProject_rate(rs.getInt("project_rate"));
//                projects.setClient_id(rs.getInt("client_id"));
//
//                allProjects.add(projects);
//            }
//            return allProjects;
//        } catch (DALException | SQLException ex) {
//            Logger.getLogger(DALManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
//
//    public Project getProject(String projectName, int project_rate, int client_id) {
//        try ( Connection con = dbCon.getConnection()) {
//
//            String sql = "SELECT * FROM Project WHERE project_name = ? AND project_rate = ? AND client_id = ?;";
//
//            PreparedStatement st = con.prepareStatement(sql);
//
//            st.setString(1, projectName);
//            st.setInt(2, project_rate);
//            st.setInt(3, client_id);
//
//            ResultSet rs = st.executeQuery();
//
//            while (rs.next()) {
//                Project project = new Project();
//                project.setProject_id(rs.getInt("project_id"));
//                project.setProject_name(rs.getString("project_name"));
//                project.setProject_rate(rs.getInt("project_rate"));
//                project.setClient_id(rs.getInt("client_id"));
//                return project;
//            }
//
//        } catch (DALException | SQLException ex) {
//            Logger.getLogger(DALManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return null;
//    }
//
//     /**
//     * returnere en liste af projecter hvor person_id har lavet tasks på.
//     *
//     * @param person_id
//     * @return
//     */
//    public List<Project> getProjectsbyID(int person_id) {
//        ArrayList<Project> projectsbyID = new ArrayList<>();
//
//        try ( Connection con = dbCon.getConnection()) {
//            String sql = "SELECT p.client_id, p.project_id, p.project_name, p.project_rate,\n"
//                    + "CONVERT(VARCHAR(5),SUM(DATEDIFF(SECOND,tl.task_start,tl.task_end))/60/60) + ':' +\n"
//                    + "RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,tl.task_start,tl.task_end))/60%60), 2) + ':' +\n"
//                    + "RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,tl.task_start,tl.task_end))%60),2)\n"
//                    + "AS total_time\n"
//                    + "FROM Project p\n"
//                    + "JOIN Task t ON t.project_id = p.project_id\n"
//                    + "JOIN Task_log tl ON t.task_id = tl.task_id\n"
//                    + "WHERE t.person_id = ?\n"
//                    + "GROUP BY p.project_id, p.project_name, p.client_id, p.project_rate;";
//
//            PreparedStatement ps = con.prepareStatement(sql);
//
//            ps.setInt(1, person_id);
//
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                Project projects = new Project();
//
//                projects.setProject_id(rs.getInt("project_id"));
//                projects.setProject_name(rs.getString("project_name"));
//                projects.setProject_rate(rs.getInt("project_rate"));
//                projects.setClient_id(rs.getInt("client_id"));
//                projects.setTotal_tid(rs.getString("total_time"));
//
//                projectsbyID.add(projects);
//            }
//
//        } catch (DALException | SQLException ex) {
//            Logger.getLogger(DALManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return projectsbyID;
//    }
//    /**
//     * Returnerer en liste af et projekts tilhørende Tasks udfra person_id og
//     * project_id
//     *
//     * @param project_id
//     * @param person_id
//     * @return
//     */
//    public List<Task> getTaskbyIDs(int project_id, int person_id) {
//        ArrayList<Task> taskbyID = new ArrayList<>();
//
//        try ( Connection con = dbCon.getConnection()) {
//
//            String sql = "SELECT t.task_id, t.task_name, t.project_id, t.person_id, MAX(tl.task_end) as last_worked_on, \n"
//                    + "CONVERT(VARCHAR(5),SUM(DATEDIFF(SECOND,tl.task_start,tl.task_end))/60/60) + ':' +\n"
//                    + "RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,tl.task_start,tl.task_end))/60%60), 2) + ':' +\n"
//                    + "RIGHT('0' + CONVERT(VARCHAR(2),SUM(DATEDIFF(SECOND,tl.task_start,tl.task_end))%60),2)\n"
//                    + "AS total_time FROM Task t\n"
//                    + "INNER JOIN Task_log tl ON t.task_id = tl.task_id\n"
//                    + "WHERE t.person_id = ? AND t.project_id = ?\n"
//                    + "Group BY t.task_id, t.task_name, t.project_id, t.person_id;";
//
//            PreparedStatement ps = con.prepareStatement(sql);
//
//            ps.setInt(1, person_id);
//            ps.setInt(2, project_id);
//
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                Task task = new Task();
//                int task_id = rs.getInt("task_id");
//                
//                task.setTask_id(task_id);
//                task.setTask_name(rs.getString("task_name"));
//                task.setProject_id(rs.getInt("project_id"));
//                task.setPerson_id(rs.getInt("person_id"));
//                task.setTotal_tid(rs.getString("total_time"));
//                task.setLast_worked_on(rs.getTimestamp("last_worked_on").toLocalDateTime());
//               
//                taskbyID.add(task);
//            }
//
//        } catch (Exception e) {
//            // todo
//        }
//
//        return taskbyID;
//    }
//    public List<Log> getLogsbyID(int task_id) {
//        ArrayList<Log> logsbyID = new ArrayList<>();
//        try ( Connection con = dbCon.getConnection()) {
//
//            String sql = "SELECT *,\n"
//                    + "CONVERT(VARCHAR(5),DATEDIFF(SECOND,tl.task_start,tl.task_end)/60/60) + ':' +\n"
//                    + "RIGHT('0' + CONVERT(VARCHAR(2),DATEDIFF(SECOND,tl.task_start,tl.task_end)/60%60), 2) + ':' +\n"
//                    + "RIGHT('0' + CONVERT(VARCHAR(2),DATEDIFF(SECOND,tl.task_start,tl.task_end)%60),2)\n"
//                    + "AS total_time\n"
//                    + "FROM Task_log tl\n"
//                    + "WHERE tl.task_id = ?\n"
//                    + "ORDER BY task_start DESC";
//
//            PreparedStatement ps = con.prepareStatement(sql);
//
//            ps.setInt(1, task_id);
//
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                Log log = new Log();
//
//                LocalDateTime end_time;
//                if (rs.getTimestamp("task_end") != null) {
//                    end_time = rs.getTimestamp("task_end").toLocalDateTime();
//                } else {
//                    end_time = null;
//                }
//
//                log.setLog_id(rs.getInt("log_id"));
//                log.setTask_id(rs.getInt("task_id"));
//                log.setBillable(rs.getBoolean("billable"));
//                log.setTotal_tid(rs.getString("total_time"));
//                log.setStart_time(rs.getTimestamp("task_start").toLocalDateTime());
//                log.setEnd_time(end_time);
//
//                logsbyID.add(log);
//            }
//
//        } catch (Exception e) {
//        }
//
//        return logsbyID;
//    }
//    
//    /**
//     * Starter en ny task
//     *
//     * @param task_name
//     * @param billable
//     * @param project_id
//     * @param person_id
//     */
//    public void startTask(String task_name, boolean billable, int project_id, int person_id) {
//        try ( Connection con = dbCon.getConnection()) {
//
//            String sql = "INSERT INTO Tasklog (task_name, billable, project_id, person_id, task_start) VALUES (?,?,?,?,CURRENT_TIMESTAMP)";
//
//            PreparedStatement ps = con.prepareStatement(sql);
//
//            ps.setString(1, task_name);
//            ps.setBoolean(2, billable);
//            ps.setInt(3, project_id);
//            ps.setInt(4, person_id);
//
//            ps.execute();
//        } catch (Exception e) {
//            // todo
//        }
//    }
//
//    /**
//     * Stopper en igangværende task
//     *
//     * @param person_id
//     */
//    public void stopTask(int person_id) {
//        try ( Connection con = dbCon.getConnection()) {
//
//            String sql = "UPDATE Tasklog SET task_end = CURRENT_TIMESTAMP\n"
//                    + "WHERE person_id = ? AND task_end IS NULL";
//
//            PreparedStatement ps = con.prepareStatement(sql);
//
//            ps.setInt(1, person_id);
//
//            ps.execute();
//
//        } catch (Exception e) {
//            // todo
//        }
//    }
//    /**
//     * Opretter en client med det client objekt der bliver sendt ned igennem
//     * lagene.
//     *
//     * @param name
//     * @param timepris
//     * @param client
//     * @return
//     */
//    public Client createClient(String name, int timepris) {
//        try ( Connection con = dbCon.getConnection()) {
//
//            String sql = "INSERT INTO Client (client_name, default_rate) VALUES (?,?)";
//
//            PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//
//            st.setString(1, name);
//            st.setInt(2, timepris);
//            int affectedRows = st.executeUpdate();
//            if (affectedRows == 1) {
//                ResultSet rs = st.getGeneratedKeys();
//                if (rs.next()) {
//                    int id = rs.getInt(1);
//                    Client client = new Client(id, name, timepris);
//                    return client;
//                }
//
//            }
//
//        } catch (Exception e) {
//
//        }
//        return null;
//    }
//
//    /**
//     * Ændrer client med de ændringer der bliver sendt ned igennem lagene.
//     *
//     * @param client
//     */
//    public void editClient(Client client) {
//        try ( Connection con = dbCon.getConnection()) {
//            int cl_id = client.getClient_id();
//            String sql = "UPDATE Client SET client_name = ?, default_rate = ? WHERE client_id = " + cl_id + ";";
//
//            PreparedStatement st = con.prepareStatement(sql);
//
//            st.setString(1, client.getClient_name());
//            st.setInt(2, client.getDefault_rate());
//
//            st.executeQuery();
//
//        } catch (Exception e) {
//        }
//    }
//
//    /**
//     * Sletter client hvor fra det client_id der bliver sendt ned igennem
//     * lagene.
//     *
//     * @param client
//     */
//    public void deleteClient(Client client) {
//        try ( Connection con = dbCon.getConnection()) {
//            String sql = "DELETE FROM Client WHERE client_id = ?";
//
//            PreparedStatement st = con.prepareStatement(sql);
//
//            st.setInt(1, client.getClient_id());
//
//            st.executeQuery();
//
//        } catch (Exception e) {
//        }
//    }
//
//    /**
//     * Returnerer en liste med alle Clients i databasen.
//     *
//     * @return
//     * @throws DALException
//     * @throws SQLException
//     */
//    public List<Client> getClients() throws DALException, SQLException {
//        ArrayList<Client> allClients = new ArrayList<>();
//
//        try ( Connection con = dbCon.getConnection()) {
//            String sql = "SELECT * FROM Client;";
//            Statement statement = con.createStatement();
//            ResultSet rs = statement.executeQuery(sql);
//            while (rs.next()) {
//                Client clients = new Client();
//                clients.setClient_id(rs.getInt("client_id"));
//                clients.setClient_name(rs.getString("client_name"));
//                clients.setDefault_rate(rs.getInt("default_rate"));
//
//                allClients.add(clients);
//            }
//            return allClients;
//        } catch (DALException | SQLException ex) {
//        }
//        return null;
//    }
//    /**
//     * Opretter en User med det user objekt der bliver sendt ned igennem lagene.
//     *
//     * @param client
//     */
//    public void createUser(User user, byte[] HashedPassword, byte[] salt) {
//        try ( Connection con = dbCon.getConnection()) {
//            String sql = "INSERT INTO Person (name, surname, email, password, active, role_id, profession_id, salt) VALUES (?,?,?,?,?,?,?,?);";
//
//            PreparedStatement st = con.prepareStatement(sql);
//
//            st.setString(1, user.getName());
//            st.setString(2, user.getSurname());
//            st.setString(3, user.getEmail());
//            st.setBytes(4, HashedPassword);
//            st.setInt(5, 1);
//            st.setInt(6, user.getRole_id());
//            st.setInt(7, user.getProfession_id());
//            st.setBytes(8, salt);
//
//            st.executeQuery();
//
//        } catch (Exception e) {
//        }
//    }
//
//    /**
//     * Ændrer User med de ændringer der bliver sendt ned igennem lagene.
//     *
//     * @param client
//     */
//    public void editUser(User user) {
//        try ( Connection con = dbCon.getConnection()) {
//            int person_id = user.getPerson_id();
//            String sql = "UPDATE Person SET name = ?, surname = ?, email = ?, role_id = ? WHERE person_id = " + person_id + ";";
//
//            PreparedStatement st = con.prepareStatement(sql);
//
//            st.setString(1, user.getName());
//            st.setString(2, user.getSurname());
//            st.setString(3, user.getEmail());
//            st.setInt(4, user.getRole_id());
////            st.setInt(5, user.getProfession_id());
//
//            st.executeQuery();
//
//        } catch (Exception e) {
//        }
//    }
//
//    /**
//     * Sletter User ud fra det User objekt der bliver sendt ned igennem lagene.
//     *
//     * @param client
//     */
//    public void deleteUser(User user) {
//        try ( Connection con = dbCon.getConnection()) {
//            String sql = "DELETE FROM Person WHERE person_id = ?";
//
//            PreparedStatement st = con.prepareStatement(sql);
//
//            st.setInt(1, user.getPerson_id());
//
//            st.executeQuery();
//
//        } catch (Exception e) {
//        }
//    }
//
//    /**
//     * Returnerer en liste med alle User i databasen.
//     *
//     * @return
//     * @throws DALException
//     * @throws SQLException
//     */
//    public List<User> getUsers() throws DALException, SQLException {
//        ArrayList<User> allUsers = new ArrayList<>();
//
//        try ( Connection con = dbCon.getConnection()) {
//            String sql = "SELECT Person.person_id, name, surname, email, Person.role_id, role_name, Person.profession_id ,profession_name\n"
//                    + "FROM Person, Profession, Role\n"
//                    + "WHERE Person.role_id = Role.role_id\n"
//                    + "AND Person.profession_id = Profession.profession_id;";
//            Statement statement = con.createStatement();
//            ResultSet rs = statement.executeQuery(sql);
//            while (rs.next()) {
//                User user = new User();
//                user.setPerson_id(rs.getInt("person_id"));
//                user.setName(rs.getString("name"));
//                user.setSurname(rs.getString("surname"));
//                user.setEmail(rs.getString("email"));
//                user.setRole_id(rs.getInt("role_id"));
//                user.setRole(rs.getString("role_name"));
//                user.setProfession_id(rs.getInt("profession_id"));
//                user.setProfession(rs.getString("profession_name"));
//
//                allUsers.add(user);
//            }
//            return allUsers;
//        } catch (DALException | SQLException ex) {
//        }
//        return null;
//    }
//    public List<Project> getProjectsbyClientID(Client client) {
//        ArrayList<Project> allProjectswithClientID = new ArrayList<>();
//        int client_ID = client.getClient_id();
//        try ( Connection con = dbCon.getConnection()) {
//            String sql = "SELECT * FROM Project WHERE client_id =  " + client_ID + ";";
//            Statement statement = con.createStatement();
//            ResultSet rs = statement.executeQuery(sql);
//            while (rs.next()) {
//                Project projects = new Project();
//                projects.setProject_id(rs.getInt("project_id"));
//                projects.setProject_name(rs.getString("project_name"));
//                projects.setProject_rate(rs.getInt("project_rate"));
//                projects.setClient_id(rs.getInt("client_id"));
//
//                allProjectswithClientID.add(projects);
//            }
//            return allProjectswithClientID;
//        } catch (DALException | SQLException ex) {
//            Logger.getLogger(DALManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
//
//    /**
//     * Returnerer en liste med Professions fra databasen.
//     *
//     * @return
//     * @throws DALException
//     * @throws SQLException
//     */
//    public List<Profession> getProfessions() throws DALException, SQLException {
//        ArrayList<Profession> allProfessions = new ArrayList<>();
//
//        try ( Connection con = dbCon.getConnection()) {
//            String sql = "SELECT * FROM Profession;";
//            Statement statement = con.createStatement();
//            ResultSet rs = statement.executeQuery(sql);
//            while (rs.next()) {
//                Profession profession = new Profession();
//                profession.setProfession_id(rs.getInt("profession_id"));
//                profession.setProfession_name(rs.getString("Profession_name"));
//
//                allProfessions.add(profession);
//            }
//            return allProfessions;
//        } catch (DALException | SQLException ex) {
//        }
//        return null;
//    }
//    /**
//     * tjekker om det login info som kommer fra BLLManageren er det samme som
//     * ligger på serveren. hvis ja returnere den et boolean som er true.
//     *
//     * @param email
//     * @param hashedPassword
//     * @return
//     */
//    public User login(String email, byte[] hashedPassword) {
//
//        try ( Connection con = dbCon.getConnection()) {
//            String sql = "SELECT * FROM PERSON WHERE email = ? AND password = ?";
//            PreparedStatement st = con.prepareStatement(sql);
//            st.setNString(1, email);
//            st.setBytes(2, hashedPassword);
//            ResultSet rs = st.executeQuery();
//
//            if (rs.next() == false) {
//                System.out.println("ResultSet is empty");
//            } else {
//                do {
//                    User user = new User(rs.getInt("person_id"), rs.getString("name"), rs.getString("surname"), rs.getString("email"), rs.getInt("role_id"), rs.getInt("profession_id"));
//                    String emailDAO = user.getEmail();
//                    byte[] passwordDAO = rs.getBytes("password");
//
//                    if (emailDAO.equals(email) && Arrays.equals(passwordDAO, hashedPassword)) {
//                        return user;
//                    }
//                } while (rs.next());
//            }
//        } catch (DALException | SQLException ex) {
//        }
//        return null;
//    }
//
//    /**
//     * henter det random "salt" som er tilsvarende den email som kommer fra
//     * BLLManageren
//     *
//     * @param email
//     * @return
//     */
//    public byte[] getSalt(String email) {
//        byte[] salt = null;
//
//        try ( Connection con = dbCon.getConnection()) {
//            String sql = "SELECT email, salt FROM PERSON WHERE email = ?";
//            PreparedStatement st = con.prepareStatement(sql);
//            st.setNString(1, email);
//            ResultSet rs = st.executeQuery();
//            while (rs.next()) {
//                salt = rs.getBytes("salt");
//            }
//        } catch (Exception e) {
//        }
//        return salt;
//    }
}
