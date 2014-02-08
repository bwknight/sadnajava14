/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.Past;

@NamedQueries({
    @NamedQuery(
            name = "findAllMessages",
            query = "SELECT m FROM MessageDAO m"),
    @NamedQuery(
            name = "findMessagesForTopic",
            query = "SELECT m FROM MessageDAO m WHERE m.topic = :topic"),
    @NamedQuery(
            name = "findMessagesForUser",
            query = "SELECT m FROM MessageDAO m WHERE m.user = :user")

})
@Entity
@Table(name = "messages")
public class MessageDAO implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private UserDAO user;
    @ManyToOne
    private TopicDAO topic;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateSent;
    private String text;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MessageDAO)) {
            return false;
        }
        MessageDAO other = (MessageDAO) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "chat.db.MessageDAO[ id=" + id + " ]";
    }

    public UserDAO getUser() {
        return user;
    }

    public void setUser(UserDAO user) {
        this.user = user;
    }

    public TopicDAO getTopic() {
        return topic;
    }

    public void setTopic(TopicDAO topic) {
        this.topic = topic;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

}
