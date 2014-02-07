/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chat;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author user
 */

@Singleton
@Startup
public class MessageManager {
 
    private final List messages =
            Collections.synchronizedList(new LinkedList());;
 
    public void sendMessage(Message msg) {
        messages.add(msg);
        msg.setDateSent(new Date());
    }
 
    public Message getFirstAfter(Date after) {
        if(messages.isEmpty())
            return null;
        if(after == null)
            return (Message) messages.get(0);
        for(Object mo : messages) {
            Message m = (Message) mo;
            if(m.getDateSent().after(after))
                return m;
        }
        return null;
    }
    
    public List<Message> getAll(String topic, Date after) {
        LinkedList<Message> res = new LinkedList<>();
        for(Object mo : messages) {
            Message m = (Message) mo;
            if((after != null) && (m.getDateSent().after(after)))
                continue;

            if ((topic != null) && 
                    ((m.getTopic() == null) || (m.getTopic().equals(topic) == false)) )
                continue;
            
            res.add(m);
        }
        return res;
    }
 
}
