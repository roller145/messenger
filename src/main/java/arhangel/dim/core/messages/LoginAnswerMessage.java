package arhangel.dim.core.messages;

/**
 * Created by riv on 29.06.16.
 */
public class LoginAnswerMessage extends Message {
    private String text;
    private String username;

    public LoginAnswerMessage() {
        super();
        this.setType(Type.MSG_LOGIN_RESULT);
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
                ", username=" + username+
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
