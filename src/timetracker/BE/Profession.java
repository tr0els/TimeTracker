/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BE;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

/**
 *
 * @author René Jørgensen
 */
public class Profession extends RecursiveTreeObject<User> {

    private int profession_id;
    private String profession_name;

    public Profession(int profession_id, String profession_name) {
        this.profession_id = profession_id;
        this.profession_name = profession_name;
    }

    public Profession() {
    }

    public int getProfession_id() {
        return profession_id;
    }

    public void setProfession_id(int profession_id) {
        this.profession_id = profession_id;
    }

    public String getProfession_name() {
        return profession_name;
    }

    public void setProfession_name(String profession_name) {
        this.profession_name = profession_name;
    }

    @Override
    public String toString() {
        return profession_name;
    }
}
