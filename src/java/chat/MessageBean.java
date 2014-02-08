/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import chat.db.TopicDAO;
import chat.db.UserDAO;
import java.io.Serializable;
import java.util.Date;
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

    @EJB
    MessageManager _msgManager;

    @ManagedProperty(value = "#{user}")
    UserBean user;

    @ManagedProperty(value = "#{topic}")
    TopicBean topic;

    private Date _lastUpdate;
    private Message _currMessage;
    private boolean _isTransactionOpen;
    /**
     * Creates a new instance of MessageBean
     */
    public MessageBean() {
        _lastUpdate = new Date(0);
        _currMessage = new Message();
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
    
    public Message getMessage() {
        return _currMessage;
    }

    public void setMessage(Message message) {
        this._currMessage = message;
    }

    public void sendMessage() {
        boolean abortTransaction = true;
        try {
            // Ensure the user exist, if not - create
            // TODO move this to a login flow
            List users = em.createNamedQuery("findUserWithName")
                    .setParameter("name", user.getName())
                    .getResultList();
            if (users.isEmpty()) {
                UserDAO dbuser = new UserDAO();
                dbuser.setName(user.getName());
                persist(dbuser);
            }

            // Ensure the topic exist, if not - create
            // TODO? put this in a create topic flow?
            List topics = em.createNamedQuery("findTopicWithName")
                    .setParameter("name", topic.getName())
                    .getResultList();
            if (topics.isEmpty()) {
                TopicDAO dbTopic = new TopicDAO();
                dbTopic.setName(topic.getName());
                persist(dbTopic);
            }

            _currMessage.setUser(user.getName());
            _currMessage.setTopic(topic.getName());
            _msgManager.sendMessage(_currMessage);
            this._currMessage = new Message();
            
            // Reaching here means all went well, we can commit
            abortTransaction = false; 
        } finally {
            closeTransaction(abortTransaction);
        }
    }

    public List<Message> getAllMessages() {
        _lastUpdate = new Date();
        return _msgManager.getAll(this.topic.getName(), null);
    }

    public List<Message> getAllMessagesAdmin() {
        _lastUpdate = new Date();
        return _msgManager.getAll(null, null);
    }

    private void persist(Object object) {
        try {
            if (!_isTransactionOpen){
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
        if (!_isTransactionOpen)
            return; // nothing to do
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
