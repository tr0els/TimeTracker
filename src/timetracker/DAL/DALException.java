/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.DAL;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class DALException extends Exception
{

    /**
     * Vores egen DALException er lavet her
     * @param msg 
     */
    public DALException(String msg)
    {
        super(msg);
    }

}
