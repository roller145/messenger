package arhangel.dim.core.commands;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.UserCreateAnswerMessage;
import arhangel.dim.core.messages.UserCreateMessage;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.database.StorageException;

import java.io.IOException;
import java.sql.SQLException;

public class UserCreateCommand extends GenericCommand{

    @Override
    Message handleMessage(Session session, Message message) throws CommandException, IOException, ProtocolException {
        UserCreateMessage userCreateMessage = (UserCreateMessage) message;
        String username = userCreateMessage.getUsername();
        String password = userCreateMessage.getPassword();
        User user;
        try{
            user = session.getUserStore().addUser(new User(username,password));
        } catch (SQLException | StorageException e) {
            throw new CommandException("Database failed execution of user create command ", e);
        }
        UserCreateAnswerMessage ans = new UserCreateAnswerMessage();

        if (user == null) {
            ans.setText(String.format("User \"%s\" already exists", username));
        } else {
            ans.setUsername(username);
            ans.setId(user.getId());
            session.setUser(user);
            ans.setText(String.format("User \"%s\" created, id = %d", ans.getUsername() , ans.getId()));
        }
        return ans;
    }
}
