package arhangel.dim.core.messages;

/**
 * Created by riv on 29.06.16.
 */
public class UserCreateAnswerMessage extends Message{
    private String text;
    private String username;

    public UserCreateAnswerMessage() {
        super();
        this.setType(Type.MSG_USER_CREATE_RESULT);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "{" + super.toString() +
                ", text=\"" + text +
                "\" }";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
