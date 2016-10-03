package arhangel.dim.server;

import arhangel.dim.core.commands.*;
import arhangel.dim.core.messages.Type;
import arhangel.dim.core.net.BinaryProtocol;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.Session;
import arhangel.dim.core.store.MessageStore;
import arhangel.dim.core.store.UserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Основной класс для сервера сообщений
 */
public class Server {

    public static final int DEFAULT_MAX_CONNECT = 16;
    static Logger log = LoggerFactory.getLogger(Server.class);

    private int port = 19000;
    private Protocol protocol = new BinaryProtocol();
    private int maxConnection = DEFAULT_MAX_CONNECT;
    private boolean isFinished = false;
    private ExecutorService threadPool = Executors.newFixedThreadPool(8);
    private static ServerSocket serverSocket;


    private MessageStore messageStore;
    private UserStore userStore;
    private List<Session> sessions = new ArrayList<>();
    private Map<Type, GenericCommand> commands = new HashMap<>();



    public Server(){

    }

    public static Logger getLog() {
        return log;
    }

    public void stop() {
        isFinished = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        threadPool.shutdown();
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        log.info("Server created");

        server.commands.put(Type.MSG_CHAT_CREATE, new ChatCreateCommand());
        server.commands.put(Type.MSG_USER_CREATE, new UserCreateCommand());
        server.commands.put(Type.MSG_LOGIN, new LoginCommand());
        server.commands.put(Type.MSG_CHAT_HIST, new ChatHistoryCommand());
        server.commands.put(Type.MSG_CHAT_LIST, new ChatListCommand());
        server.commands.put(Type.MSG_INFO, new InfoCommand());
        server.commands.put(Type.MSG_TEXT, new TextCommand(server));

        try {
            serverSocket = new ServerSocket(server.getPort());

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                log.info("New session");
                Session session = new Session(clientSocket, server);
                server.getSessions().add(session);
                server.threadPool.execute(session);
            }
        } catch (IOException e) {
            log.error("Cannot start new session", e);
            e.printStackTrace();
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }


    public int getPort() {
        return port;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Map<Type, GenericCommand> getCommands() {
        return commands;
    }
}
