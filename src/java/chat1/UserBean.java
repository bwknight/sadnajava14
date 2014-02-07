/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chat1;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author user
 */
@ManagedBean(name = "user")
@SessionScoped
public class UserBean implements Serializable{

    private String _name;

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public String getTopic() {
        return _topic;
    }

    public void setTopic(String _topic) {
        this._topic = _topic;
    }
    private String _topic;
    /**
     * Creates a new instance of UserBean
     */
    public UserBean() {
    }
    
}
