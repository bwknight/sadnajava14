package chat;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import chat.db.TopicDAO;
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
@ManagedBean(name = "topic")
@SessionScoped
public class TopicBean implements Serializable {

    private String _name;
    @PersistenceContext(unitName = "ChatPU")
    private EntityManager em;
    @Resource
    private javax.transaction.UserTransaction utx;

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    /**
     * Creates a new instance of UserBean
     */
    public TopicBean() {
    }

    public TopicDAO LoadOrCreate() {
        TopicDAO dbTopic;
        // Ensure the topic exist, if not - create
        // TODO? put this in a create topic flow?
        List<TopicDAO> topics = em.createNamedQuery("findTopicWithName")
                .setParameter("name", _name)
                .getResultList();
        if (topics.isEmpty()) {
            dbTopic = new TopicDAO();
            dbTopic.setName(_name);
            persist(dbTopic);
        } else {
            dbTopic = topics.get(0);
        }
        return dbTopic;
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
