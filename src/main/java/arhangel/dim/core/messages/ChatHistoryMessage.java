package arhangel.dim.core.messages;

/**
 * Created by riv on 17.06.16.
 */
public class ChatHistoryMessage extends Message {
    Long chatId;

    public ChatHistoryMessage(Long chatId) {
        this.chatId = chatId;
        setType(Type.MSG_CHAT_HIST);
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }


}
