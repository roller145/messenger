package arhangel.dim.core;

import java.io.Serializable;
import java.util.LongSummaryStatistics;

/**
 * Представление пользователя
 */
public class User implements Serializable {
    private Long id;
    private String name;
    private String password;

    public User(Long id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public User() {
        this.id = Long.valueOf(0);
        this.name = "";
        this.password = "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }
}
