/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab.graph;

/**
 *
 * @author ricardo_rodab
 */
public class Message {
    
    public Integer finalDestUID = null;
    public Integer TTL = 10;
    
    public Message(Integer finalDestUID){
        this.finalDestUID = finalDestUID;
    }
}
