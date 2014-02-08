/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import chat.db.TopicDAO;
import chat.db.UserDAO;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author user
 */
@ManagedBean(name = "user")
@SessionScoped
public class UserBean implements Serializable {

    private String _name;
    @PersistenceContext(unitName = "ChatPU")
    private EntityManager em;
    @Resource
    private javax.transaction.UserTransaction utx;

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    /**
     * Creates a new instance of UserBean
     */
    public UserBean() {
    }

    public UserDAO LoadOrCreate() {
        UserDAO dbuser;
        // Ensure the user exist, if not - create        
        List<UserDAO> users = em.createNamedQuery("findUserWithName")
                .setParameter("name", _name)
                .getResultList();
        if (users.isEmpty()) {
            dbuser = new UserDAO();
            dbuser.setName(_name);
            persist(dbuser);
        } else {
            dbuser = users.get(0);
        }
        return dbuser;
    }

    public void persist(Object object) {
        try {
            utx.begin();
            em.persist(object);
            utx.commit();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            throw new RuntimeException(e);
        }
    }

}
