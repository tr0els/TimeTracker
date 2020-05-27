/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timetracker.BE;

/**
 *
 * @author Brian Brandt, Kim Christensen, Troels Klein, René Jørgensen &
 * Charlotte Christensen
 */
public class Client
{

    private int clientId;
    private String clientName;
    private int defaultRate;

    /**
     * Constructor for Client som tager imod parametre
     *
     * @param clientId
     * @param clientName
     * @param defaultRate
     */
    public Client(int clientId, String clientName, int defaultRate)
    {
        this.clientId = clientId;
        this.clientName = clientName;
        this.defaultRate = defaultRate;
    }

    /**
     * Constructor for Client
     */
    public Client()
    {
    }

    /**
     * Returnerer clientId
     *
     * @return
     */
    public int getClientId()
    {
        return clientId;
    }

    /**
     * Sætter clientId
     *
     * @param clientId
     */
    public void setClientId(int clientId)
    {
        this.clientId = clientId;
    }

    /**
     * Returnerer clientName
     *
     * @return
     */
    public String getClientName()
    {
        return clientName;
    }

    /**
     * Sætter clientName
     *
     * @param clientName
     */
    public void setClientName(String clientName)
    {
        this.clientName = clientName;
    }

    /**
     * Returnerer defaultRate
     *
     * @return
     */
    public int getDefaultRate()
    {
        return defaultRate;
    }

    /**
     * Sætter defaultRate
     *
     * @param defaultRate
     */
    public void setDefaultRate(int defaultRate)
    {
        this.defaultRate = defaultRate;
    }

    /**
     * ToString metode så klassen viser det rigtige
     *
     * @return
     */
    @Override
    public String toString()
    {
        return clientName;
    }

}
