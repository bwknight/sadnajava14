/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chat1;

import java.util.Date;
import java.util.List;

/**
 *
 * @author user
 */
public interface MessageManagerLocal {
    void sendMessage(Message msg);
 
    Message getFirstAfter(Date after);
    List<Message> getAll(Date after);
}
