package arhangel.dim.client.commands;

import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.net.ProtocolException;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.inject.Inject;
import com.google.inject.name.Named;


import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Parameters(commandDescription = "login (if the login is not specified, then authorize).")
@Named("/info")
class CommandInfo extends Command {
    @Parameter(names = {"-id"}, description = "user id")
    private Long id = Long.valueOf(-1);

    private boolean isMe() {
        return (id.equals(Long.valueOf(-1)));
    }

    @Override
    public void execute() throws ExecutionException, IOException, ProtocolException {
        logger.info("login with username: " + login);
        if (isMe()) {
            logger.info("Executing info request about me:)");
            InfoMessage loginMessage = new LoginMessage(login, password);
            client.send(loginMessage);
        } else {
            logger.info("Wrong parameters of login request");
        }

    }


