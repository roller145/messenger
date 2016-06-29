package arhangel.dim.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import arhangel.dim.core.User;
import arhangel.dim.core.commands.Command;
import arhangel.dim.core.commands.CommandException;
import arhangel.dim.core.commands.GenericCommand;
import arhangel.dim.core.messages.Message;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.MessageStoreImpl;
import arhangel.dim.core.store.UserStore;
import arhangel.dim.core.store.UserStoreImpl;
import arhangel.dim.core.store.database.StorageException;
import arhangel.dim.server.Server;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Здесь храним всю информацию, связанную с отдельным клиентом.
 * - объект User - описание пользователя
 * - сокеты на чтение/запись данных в канал пользователя
 */
public class Session implements ConnectionHandler, Runnable{

    private Map<Type, GenericCommand> commands;
    private Logger log = LoggerFactory.getLogger(Session.class);
    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private InputStream in;
    private OutputStream out;
    private Protocol protocol;
    private Socket clientSocket;
    private MessageStore messageStore  = new MessageStoreImpl();
    private UserStore userStore;
    private User user;

    public Session(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.commands = server.getCommands();

        try {
            userStore = new UserStoreImpl();
        } catch (StorageException e) {
            e.printStackTrace();
        }
        try {
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
        } catch (IOException e) {
            Server.getLog().error(e.getMessage());
        }
        protocol = server.getProtocol();
    }

    @Override
    public void send(Message msg) throws ProtocolException, IOException {
        log.info("Sending message: {}", msg);
        out.write(protocol.encode(msg));
        out.flush();
    }

    @Override
    public void onMessage(Message msg) {
        log.info("Handling message: {}", msg);
        try {
            handleMessage(msg);
        } catch (CommandException | IOException | ProtocolException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            clientSocket.close();
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleMessage(Message message) throws CommandException, IOException, ProtocolException {
        Type messageType = message.getType();
        if (messageType == null) {
            throw new CommandException("Message type is null");
        } else if (commands.containsKey(messageType)) {
            commands.get(messageType).execute(this, message);
        } else {
            log.error("Undefined message type");
        }
    }

    @Override
    public void run() {
        log.info("Session running");
        while (!clientSocket.isClosed()) {
            byte[] buf = new byte[1024 * 64];
            int readBytes = 0;
            try {
                readBytes = in.read(buf);
                if (readBytes > 0) {
                    Message msg = null;
                    msg = protocol.decode(buf);
                    onMessage(msg);
                }
            } catch (IOException | ProtocolException e) {
                log.error("Server error", e);
                e.printStackTrace();
            }
        }
    }

    public UserStore getUserStore() {
        return userStore;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MessageStore getMessageStore() {
        return messageStore;
    }

    public User getUser() {
        return user;
    }
}
