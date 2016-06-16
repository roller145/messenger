package arhangel.dim.core;

import java.util.List;

/**
 * Чат.
 */
public class Chat {
    private Long id;
    private List<Long> users;

    public Chat(long chat_id, List<Long> users) {
        this.id = chat_id;
        this.users = users;
    }

    public Long getId() {
        return id;
    }

    public List<Long> getUsers() {
        return users;
    }
}
