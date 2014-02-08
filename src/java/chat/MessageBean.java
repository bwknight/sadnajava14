/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import chat.db.MessageDAO;
import chat.db.TopicDAO;
import chat.db.UserDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
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
    private boolean _isTransactionOpen;

    /**
     * Creates a new instance of MessageBean
     */
    public MessageBean() {
        _lastUpdate = new Date(0);
        _currMessageText = "";
        _isTransactionOpen = false;
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
