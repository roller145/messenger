package arhangel.dim.core.messages;

/**
 * Created by riv on 29.06.16.
 */
public class AnswerMessage extends Message{
    private String text;

    public AnswerMessage() {
        super();
        this.setType(Type.MSG_ANS);
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

}
