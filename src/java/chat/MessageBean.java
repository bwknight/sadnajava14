/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import chat.db.MessageDAO;
import chat.wsclients.MessagesWS;
import chat.wsclients.MessagesWS_Service;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author user
 */
@ManagedBean
@ViewScoped
public class MessageBean implements Serializable {

    @PersistenceContext(unitName = "ChatPU")
    private EntityManager em;
    @Resource
    private javax.transaction.UserTransaction utx;

    @ManagedProperty(value = "#{user}")
    UserBean user;

    @ManagedProperty(value = "#{topic}")
    TopicBean topic;

    private Date _lastUpdate;
    private String _currMessageText;
    
    /**
     * Creates a new instance of MessageBean
     */
    public MessageBean() {
        _lastUpdate = new Date(0);
        _currMessageText = "";
    }

    public Date getLastUpdate() {
        return _lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this._lastUpdate = lastUpdate;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public void setTopic(TopicBean topic) {
        this.topic = topic;
    }

    public String getMessageText() {
        return _currMessageText;
    }

    public void setMessageText(String message) {
        this._currMessageText = message;
    }

    public void sendMessage() {
        MessageDAO newMessage = new MessageDAO();
        newMessage.setUser(user.LoadOrCreate());
        newMessage.setTopic(topic.LoadOrCreate());
        newMessage.setDateSent(new Date());
        newMessage.setText(_currMessageText);
        persist(newMessage);
        this._currMessageText = "";

    }

    public List<MessageDAO> getAllMessages() {
        _lastUpdate = new Date();

        List<MessageDAO> res = em.createNamedQuery("findMessagesForTopic")
                .setParameter("topic", topic.LoadOrCreate())
                .getResultList();
        return res;
    }

    public List<MessageDAO> getAllMessagesAdmin() {
        _lastUpdate = new Date();
        List<MessageDAO> res = em.createNamedQuery("findAllMessages")
                .getResultList();
        return res;
    }

    public List<chat.wsclients.MessageDAO> getAllMessagesAdminApi() {
        _lastUpdate = new Date();        
        MessagesWS port = new MessagesWS_Service().getMessagesWSPort();
        return port.getAllMessages();
    }
    
    private String _adminQueryUser;

    public String getAdminQueryUser() {
        return _adminQueryUser;
    }

    public void setAdminQueryUser(String adminQueryUser) {
        this._adminQueryUser = adminQueryUser;
    }
    public List<chat.wsclients.MessageDAO> getUserMessagesAdminApi() {
        _lastUpdate = new Date();        
        MessagesWS port = new MessagesWS_Service().getMessagesWSPort();
        return port.getUserMessages(_adminQueryUser);
    }
    
    private String _adminQueryTopic;

    public String getAdminQueryTopic() {
        return _adminQueryTopic;
    }

    public void setAdminQueryTopic(String _adminQueryTopic) {
        this._adminQueryTopic = _adminQueryTopic;
    }
    
    public List<chat.wsclients.MessageDAO> getTopicMessagesAdminApi() {
        _lastUpdate = new Date();        
        MessagesWS port = new MessagesWS_Service().getMessagesWSPort();
        return port.getTopicMessages(_adminQueryTopic);
    }
    private void persist(Object object) {
        try {
            utx.begin();
            em.persist(object);
            utx.commit();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "JPA exception caught", e);
            throw new RuntimeException(e);
        }
    }

 

}
