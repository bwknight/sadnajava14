/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chat1;


import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
//import org.primefaces.context.RequestContext;
/**
 *
 * @author user
 */
@ManagedBean
@ViewScoped
public class MessageBean implements Serializable {
 
    @EJB
    MessageManagerLocal mm;
 
    private final List _messages;
    private Date _lastUpdate;
    private Message _currMessage;
 
    /**
     * Creates a new instance of MessageBean
     */
    public MessageBean() {
        _messages = Collections.synchronizedList(new LinkedList());
        _lastUpdate = new Date(0);
        _currMessage = new Message();
    }
 
    public String getFoo(){
        return "fooooooo"; //new Date().toString();
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
 
    public void sendMessage(ActionEvent evt) {
        mm.sendMessage(_currMessage);        
        FacesMessage fm = new FacesMessage("sent: " + _currMessage.getMessage());
        FacesContext.getCurrentInstance().addMessage(null, fm);
        this._currMessage = new Message();
    }
 
    public void peakMessage(ActionEvent evt) {
        Message m = mm.getFirstAfter(_lastUpdate);
        String fms =  "peak: " + ((m == null) ? "N\\A" : m.getMessage());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(fms));
    }
    
    public void peakMessages(ActionEvent evt) {
        List<Message> mlist = mm.getAll(null);
        int i = 0;
        for (Message message1 : mlist) {
            FacesMessage fm = new FacesMessage("#" + (++i) + ' ' + message1.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, fm);
        }
        
    }
    
     public List<Message> getAllMessages() {
        return mm.getAll(null);
    }
    
    public void firstUnreadMessage(ActionEvent evt) {
//       RequestContext ctx = RequestContext.getCurrentInstance();
// 
       Message m = mm.getFirstAfter(_lastUpdate);
        System.out.println("m: " + m);
  
//       ctx.addCallbackParam("ok", m!=null);
       if(m==null)
           return;
// 
       _lastUpdate = m.getDateSent();
// 
//       ctx.addCallbackParam("user", m.getUser());
//       ctx.addCallbackParam("dateSent", m.getDateSent().toString());
//       ctx.addCallbackParam("text", m.getMessage());
// 
    }
 
}
