package arhangel.dim.core.commands;

import arhangel.dim.core.User;
import arhangel.dim.core.messages.InfoMessage;
import arhangel.dim.core.messages.InfoResultMessage;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.database.StorageException;

import java.io.IOException;

/**
 * Created by riv on 29.06.16.
 */
public class InfoCommand extends GenericCommand {
    @Override
    Message handleMessage(Session session, Message message) throws CommandException, IOException, ProtocolException {
        InfoMessage infoMessage = (InfoMessage) message;
        Long userId = infoMessage.getSenderId();
        Long id = infoMessage.getUserId();
        User user = null;
        InfoResultMessage answer = new InfoResultMessage();
        try {
            user = session.getUserStore().getUserById(id);
            if (user == null){
                answer.setInfo("no such user");
            }else{
                answer.setInfo(" username: " + user.getName() + "; id: " + user.getId());
            }
            answer.setSenderId(userId);
        } catch (StorageException e) {
            answer.setInfo(e.getMessage());
        }
        return answer;
    }
}
