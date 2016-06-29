package arhangel.dim.core.commands;

import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.TextMessage;
import arhangel.dim.core.messages.Type;

import java.util.List;

/**
 * Created by riv on 29.06.16.
 */
public class ChatHistoryResultMessage extends Message{
    private List<TextMessage> history;
    private Long chatId;

    public ChatHistoryResultMessage() {
        super();
        this.setType(Type.MSG_CHAT_HIST_RESULT);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Chat history for chat = ");
        builder.append(chatId);
        builder.append("\n[\n");
        for (TextMessage text : history) {
            builder.append(text.getDateTime().toString());
            builder.append("(");
            builder.append(text.getSenderId());
            builder.append(")");
            builder.append(": \"");
            builder.append(text.getText());
            builder.append("\"\n");
        }
        builder.append("]");
        return builder.toString();
    }

    public List<TextMessage> getHistory() {
        return history;
    }

    public void setHistory(List<TextMessage> history) {
        this.history = history;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
