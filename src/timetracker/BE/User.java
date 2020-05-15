/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BE;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class User extends RecursiveTreeObject<User> {

    private int person_id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private int role_id;
    private int profession_id;
    private String role;
    private String profession;

    public User(int person_id, String name, String surname, String email, int role_id, int profession_id) {
        this.person_id = person_id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.role_id = role_id;
        this.profession_id = profession_id;

    }

    public User(int person_id, String name, String surname, String email, String role, String profession) {
        this.person_id = person_id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.role = role;
        this.profession = profession;
    }

    public User() {
    }

    public int getPerson_id() {
        return person_id;
    }

    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public int getProfession_id() {
        return profession_id;
    }

    public void setProfession_id(int profession_id) {
        this.profession_id = profession_id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public boolean isAdmin(User user) {
        if (role_id == 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isAdminCheck(TreeItem<User> chosenUser) {
        if (role_id == 1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "" + name + " " + surname;
    }

}
