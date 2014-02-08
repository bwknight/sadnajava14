/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.ws;

import chat.db.MessageDAO;
import chat.db.TopicDAO;
import chat.db.UserDAO;
import java.util.ArrayList;
import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author user
 */
@WebService(serviceName = "MessagesWS")
@Stateless()
public class MessagesWS {

    @PersistenceContext(unitName = "ChatPU")
    private EntityManager em;

    @WebMethod(operationName = "getUserMessages")
    public MessageDAO[] getUserMessages(@WebParam(name = "userName") String userName) {
        List<MessageDAO> res = em.createNamedQuery("findMessagesForUser")
                .setParameter("user", loadUser(userName))
                .getResultList();
        MessageDAO[] arres = new MessageDAO[res.size()];
        res.toArray(arres);
        return arres;
    }

    @WebMethod(operationName = "getTopicMessages")
    public MessageDAO[] getTopicMessages(@WebParam(name = "topic") String topic) {
        List<MessageDAO> res = em.createNamedQuery("findMessagesForTopic")
                .setParameter("topic", loadTopic(topic))
                .getResultList();
        MessageDAO[] arres = new MessageDAO[res.size()];
        res.toArray(arres);
        return arres;
    }

    @WebMethod(operationName = "getAllMessages")
    public MessageDAO[] getAllMessages() {
        List<MessageDAO> res = em.createNamedQuery("findAllMessages")
                .getResultList();
        MessageDAO[] arres = new MessageDAO[res.size()];
        res.toArray(arres);
        return arres;
    }

    private void persist(Object object) {
        em.persist(object);
    }

    private UserDAO loadUser(String name) {
        List<UserDAO> users = em.createNamedQuery("findUserWithName")
                .setParameter("name", name)
                .getResultList();
        return (users.isEmpty() ? null : users.get(0));
    }

    private TopicDAO loadTopic(String name) {
        List<TopicDAO> topics = em.createNamedQuery("findTopicWithName")
                .setParameter("name", name)
                .getResultList();
        return (topics.isEmpty() ? null : topics.get(0));
    }

}
