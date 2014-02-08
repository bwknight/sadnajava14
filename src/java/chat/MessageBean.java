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
        boolean abortTransaction = true;
        try {
            UserDAO dbuser;
            TopicDAO dbTopic;
            // Ensure the user exist, if not - create
            // TODO move this to a login flow
            List<UserDAO> users = em.createNamedQuery("findUserWithName")
                    .setParameter("name", user.getName())
                    .getResultList();
            if (users.isEmpty()) {
                dbuser = new UserDAO();
                dbuser.setName(user.getName());
                persist(dbuser);
            } else {
                dbuser = users.get(0);
            }

            // Ensure the topic exist, if not - create
            // TODO? put this in a create topic flow?
            List<TopicDAO> topics = em.createNamedQuery("findTopicWithName")
                    .setParameter("name", topic.getName())
                    .getResultList();
            if (topics.isEmpty()) {
                dbTopic = new TopicDAO();
                dbTopic.setName(topic.getName());
                persist(dbTopic);
            } else {
                dbTopic = topics.get(0);
            }

            MessageDAO newMessage = new MessageDAO();
            newMessage.setUser(dbuser);
            newMessage.setTopic(dbTopic);
            newMessage.setDateSent(new Date());
            newMessage.setText(_currMessageText);
            persist(newMessage);
            this._currMessageText = "";

            // Reaching here means all went well, we can commit
            abortTransaction = false;
        } finally {
            closeTransaction(abortTransaction);
        }
    }

    public List<MessageDAO> getAllMessages() {
        _lastUpdate = new Date();

        List<TopicDAO> topics = em.createNamedQuery("findTopicWithName")
                .setParameter("name", topic.getName())
                .getResultList();
        if (topics.isEmpty()) {
            return new ArrayList<>();
        } else {
            TopicDAO dbTopic = topics.get(0);
            List<MessageDAO> res = em.createNamedQuery("findMessagesForTopic")
                    .setParameter("topic", dbTopic)
                    .getResultList();
            return res;
        }

    }

    public List<MessageDAO> getAllMessagesAdmin() {
        _lastUpdate = new Date();
        List<MessageDAO> res = em.createNamedQuery("findAllMessages")
                .getResultList();
        return res;
    }

    private void persist(Object object) {
        try {
            if (!_isTransactionOpen) {
                utx.begin();
                _isTransactionOpen = true;
            }
            em.persist(object);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "JPA exception caught", e);
            throw new RuntimeException(e);
        }
    }

    private void closeTransaction(boolean abort) {
        if (!_isTransactionOpen) {
            return; // nothing to do
        }
        try {
            _isTransactionOpen = false;
            if (abort) {
                utx.rollback();
            } else {
                utx.commit();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "JPA exception caught", e);
            throw new RuntimeException(e);
        }
    }

}
