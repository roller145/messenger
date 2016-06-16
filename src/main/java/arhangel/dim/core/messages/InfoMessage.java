package arhangel.dim.core.messages;

/**
 * Created by riv on 16.06.16.
 */
public class InfoMessage extends Message {
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    private Long userId;

    public InfoMessage(Long userId) {
        setType(Type.MSG_INFO);
        this.userId = userId;
    }
}