package arhangel.dim.core.messages;

import java.util.List;

/**
 * Created by riv on 17.06.16.
 */
public class ChatCreateMessage extends Message {

    private List<Long> users;

    public ChatCreateMessage() {
        setType(Type.MSG_CHAT_CREATE);
    }

    public ChatCreateMessage(List<Long> users) {
        this.users = users;
        setType(Type.MSG_CHAT_CREATE);
    }

    public List<Long> getUsers() {
        return users;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }
}
