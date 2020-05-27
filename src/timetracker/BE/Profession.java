/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BE;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class Profession extends RecursiveTreeObject<User>
{

    private int professionId;
    private String professionName;

    /**
     * Constructor for Profession som tager imod parametre
     *
     * @param professionId
     * @param professionName
     */
    public Profession(int professionId, String professionName)
    {
        this.professionId = professionId;
        this.professionName = professionName;
    }

    /**
     * Constructor for Profession
     */
    public Profession()
    {
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
     * Returnerer professionName
     *
     * @return
     */
    public String getProfessionName()
    {
        return professionName;
    }

    /**
     * Sætter professionName
     *
     * @param professionName
     */
    public void setProfessionName(String professionName)
    {
        this.professionName = professionName;
    }

    /**
     * ToString metode så klassen viser det rigtige
     *
     * @return
     */
    @Override
    public String toString()
    {
        return professionName;
    }
}
