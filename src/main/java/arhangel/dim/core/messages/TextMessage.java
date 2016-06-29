package arhangel.dim.core.messages;

import com.sun.jndi.toolkit.ctx.Continuation;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Простое текстовое сообщение
 */
public class TextMessage extends Message {
    private String text;
    private LocalDateTime dateTime;
    private Long chatId;
    private Continuation date;

    public TextMessage() {
        super();
        this.setType(Type.MSG_TEXT);
    }

    public TextMessage(Long chatId, Long userId, String text, LocalDateTime dateTime) {
        this();
        this.setSenderId(userId);
        this.chatId = chatId;
        this.text = text;
        this.dateTime = dateTime;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        TextMessage message = (TextMessage) other;
        return Objects.equals(text, message.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }

    @Override
    public String toString() {
        return "TextMessage{" +
                "text= " + text +
                '}';
    }

    public Long getChatId() {
        return chatId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

}
