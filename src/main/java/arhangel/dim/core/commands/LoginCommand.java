package arhangel.dim.core.commands;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.LoginAnswerMessage;
import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.database.StorageException;

import java.io.IOException;

/**
 * Created by riv on 29.06.16.
 */
public class LoginCommand extends GenericCommand {
    private Type type = Type.MSG_LOGIN;
    @Override
    Message handleMessage(Session session, Message message) throws CommandException, IOException, ProtocolException {
        LoginMessage loginMessage = (LoginMessage) message;
        String username = loginMessage.getLogin();
        String password = loginMessage.getPassword();
        User user = null;
        try {
            user = session.getUserStore().getUser(username, password);
        } catch (StorageException e) {
            throw new CommandException("Storage failed", e);
        }

        LoginAnswerMessage answer = new LoginAnswerMessage();
        if (user == null){
            answer.setText("Failed login");
        }else{
            session.setUser(user);
            answer.setId(user.getId());
            answer.setUsername(user.getName());
            answer.setText(String.format("Logged in as %s, id#%d", username, user.getId()));
        }
        return answer;
    }

}
