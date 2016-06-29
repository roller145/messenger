package arhangel.dim.core.messages;

import java.util.List;

/**
 * Created by riv on 29.06.16.
 */
public class ChatListResultMessage extends Message {
    private List<Long> chats;

    public ChatListResultMessage() {
        super();
        this.setType(Type.MSG_CHAT_LIST_RESULT);
    }

    @Override
    public String toString() {
        return super.toString() + "[" + chats.toString() + "]";
    }

    public List<Long> getChats() {
        return chats;
    }

    public void setChats(List<Long> chats) {
        this.chats = chats;
    }
}
