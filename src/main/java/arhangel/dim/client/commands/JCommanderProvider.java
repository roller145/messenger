package arhangel.dim.client.commands;

import com.beust.jcommander.JCommander;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.Collection;

/**
 * Created by riv on 16.06.16.
 */
public class JCommanderProvider implements Provider<JCommander> {

    @Inject
    private Collection<Command> commands;

    public JCommander get() {
        JCommander commander = new JCommander();
        for (Command command : commands) {
            addCommand(commander, command);
        }
        return commander;
    }

    private void addCommand(JCommander commander, Command command) {
        commander.addCommand(command.getCommandName(), command, command.getAliases());
    }
}
