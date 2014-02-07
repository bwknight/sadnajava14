/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chat1;


import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
//import org.primefaces.context.RequestContext;
/**
 *
 * @author user
 */
@ManagedBean
@ViewScoped
public class MessageBean implements Serializable {
 
    @EJB
    MessageManager _msgManager;
    
    @ManagedProperty(value="#{user}")    
    UserBean user;
    
    @ManagedProperty(value="#{topic}")    
    TopicBean topic;    

    public void setUser(UserBean user) {
        this.user = user;
    }

    public void setTopic(TopicBean topic) {
        this.topic = topic;
    }

    private Date _lastUpdate;
    private Message _currMessage;
 
    /**
     * Creates a new instance of MessageBean
     */
    public MessageBean() {
        _lastUpdate = new Date(0);
        _currMessage = new Message();
    }
    
    public Date getLastUpdate() {
        return _lastUpdate;
    }
 
    public void setLastUpdate(Date lastUpdate) {
        this._lastUpdate = lastUpdate;
    }
 
    public Message getMessage() {
        return _currMessage;
    }
 
    public void setMessage(Message message) {
        this._currMessage = message;
    }
 
    public void sendMessage() {
        _currMessage.setUser(user.getName()); 
        _currMessage.setTopic(topic.getName()); 
        _msgManager.sendMessage(_currMessage);        
        this._currMessage = new Message();
    }
 
    
    public List<Message> getAllMessages() {
        _lastUpdate = new Date();
        return _msgManager.getAll(this.topic.getName(), null);
    }
    
    public List<Message> getAllMessagesAdmin() {
        _lastUpdate = new Date();
        return _msgManager.getAll(null, null);
    }
    
}

