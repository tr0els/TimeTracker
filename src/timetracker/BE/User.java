/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BE;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.util.Objects;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class User extends RecursiveTreeObject<User>
{

    private int personId;
    private String name;
    private String surname;
    private String email;
    private String password;
    private int roleId;
    private String role;
    private int professionId;
    private String profession;

    /**
     * Constructor for User som tager imod parametre
     *
     * @param personId
     * @param name
     * @param surname
     * @param email
     * @param roleId
     * @param professionId
     */
    public User(int personId, String name, String surname, String email, int roleId, int professionId)
    {
        this.personId = personId;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.roleId = roleId;
        this.professionId = professionId;
    }

    /**
     * Constructor for User som tager imod parametre
     *
     * @param personId
     * @param name
     * @param surname
     * @param email
     * @param role
     * @param profession
     */
    public User(int personId, String name, String surname, String email, String role, String profession)
    {
        this.personId = personId;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.role = role;
        this.profession = profession;
    }

    /**
     * Constructor for User
     */
    public User()
    {
    }

    /**
     * Returnerer personId
     *
     * @return
     */
    public int getPersonId()
    {
        return personId;
    }

    /**
     * Sætter personId
     *
     * @param personId
     */
    public void setPersonId(int personId)
    {
        this.personId = personId;
    }

    /**
     * Returnerer name
     *
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sætter name
     *
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returnerer surname
     *
     * @return
     */
    public String getSurname()
    {
        return surname;
    }

    /**
     * Sætter surname
     *
     * @param surname
     */
    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    /**
     * Returnerer email
     *
     * @return
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * Sætter email
     *
     * @param email
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * Returnerer password
     *
     * @return
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Sætter password
     *
     * @param password
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Returnerer roleId
     *
     * @return
     */
    public int getRoleId()
    {
        return roleId;
    }

    /**
     * Sætter roleId
     *
     * @param roleId
     */
    public void setRoleId(int roleId)
    {
        this.roleId = roleId;
    }

    /**
     * Returnerer professionId
     *
     * @return
     */
    public int getProfessionId()
    {
        return professionId;
    }

    /**
     * Sætter professionId
     *
     * @param professionId
     */
    public void setProfessionId(int professionId)
    {
        this.professionId = professionId;
    }

    /**
     * Returnerer role
     *
     * @return
     */
    public String getRole()
    {
        return role;
    }

    /**
     * Sætter role
     *
     * @param role
     */
    public void setRole(String role)
    {
        this.role = role;
    }

    /**
     * Returnerer profession
     *
     * @return
     */
    public String getProfession()
    {
        return profession;
    }

    /**
     * Sætter profession
     *
     * @param profession
     */
    public void setProfession(String profession)
    {
        this.profession = profession;
    }

    /**
     * Returnerer en boolean om User er Admin eller ej
     *
     * @param user
     * @return
     */
    public boolean isAdmin(User user)
    {
        if (roleId == 1)
        {
            return true;
        } else
        {
            return false;
        }
    }

    /**
     * Returnerer en boolean om User er Admin eller ej til TreeTableView
     *
     * @param chosenUser
     * @return
     */
    public boolean isAdminCheck(TreeItem<User> chosenUser)
    {
        if (roleId == 1)
        {
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        return hash;
    }

    /**
     * Bruges til at sammenligne to brugere
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final User other = (User) obj;
        if (this.personId != other.personId)
        {
            return false;
        }
        if (this.roleId != other.roleId)
        {
            return false;
        }
        if (this.professionId != other.professionId)
        {
            return false;
        }
        if (!Objects.equals(this.name, other.name))
        {
            return false;
        }
        if (!Objects.equals(this.surname, other.surname))
        {
            return false;
        }
        if (!Objects.equals(this.email, other.email))
        {
            return false;
        }
        if (!Objects.equals(this.password, other.password))
        {
            return false;
        }
        if (!Objects.equals(this.role, other.role))
        {
            return false;
        }
        if (!Objects.equals(this.profession, other.profession))
        {
            return false;
        }
        return true;
    }

    /**
     * ToString metode så klassen viser det rigtige
     *
     * @return
     */
    @Override
    public String toString()
    {
        return "" + name + " " + surname;
    }

}
