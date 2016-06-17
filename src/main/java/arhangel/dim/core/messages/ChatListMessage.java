package arhangel.dim.core.messages;

/**
 * Created by riv on 17.06.16.
 */

public class ChatListMessage extends Message {

    Long id;

    public ChatListMessage(Long id) {
        this.id = id;
        setType(Type.MSG_CHAT_LIST);
    }

    @Override
    public String toString() {
        return "ChatListMessage{" +
                '}';
    }
}
