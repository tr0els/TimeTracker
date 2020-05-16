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
public class Client {

    private int client_id;
    private String client_name;
    private int default_rate;

    public Client(int client_id, String client_name, int default_rate) {
        this.client_id = client_id;
        this.client_name = client_name;
        this.default_rate = default_rate;
    }

    public Client() {
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public int getDefault_rate() {
        return default_rate;
    }

    public void setDefault_rate(int default_rate) {
        this.default_rate = default_rate;
    }

    @Override
    public String toString() {
        return client_name;
    }

}
