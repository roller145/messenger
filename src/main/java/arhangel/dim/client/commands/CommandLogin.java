package arhangel.dim.client.commands;

import arhangel.dim.core.messages.LoginMessage;
import arhangel.dim.core.net.ProtocolException;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.inject.Inject;
import com.google.inject.name.Named;


import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Parameters(commandDescription = "login: /login -l <login> -p <password>")
@Named("/login")
class CommandLogin extends Command {
    @Parameter(names = {"-l", "-login"}, description = "user name")
    private String login = "";

    private boolean isLogin() {
        return (login.isEmpty());
    }

    @Parameter(names = {"-password", "-p"}, description = "password")
    private String password = "";

    private boolean isPassword() {
        return (password.isEmpty());
    }

    @Override
    public void execute() throws ExecutionException, IOException, ProtocolException {
        if (isLogin() && isPassword()) {
            logger.info(String.format("Executing login request with parameter: [username=%s]", login));
            LoginMessage loginMessage = new LoginMessage(login, password);
            client.send(loginMessage);
        } else {
            logger.error("Wrong parameters of login request");
        }

    }

}


