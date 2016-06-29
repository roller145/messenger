package arhangel.dim.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import arhangel.dim.core.commands.CommandException;
import arhangel.dim.core.messages.*;
import arhangel.dim.core.net.BinaryProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arhangel.dim.container.Container;
import arhangel.dim.container.InvalidConfigurationException;
import arhangel.dim.core.net.ConnectionHandler;
import arhangel.dim.core.net.Protocol;
import arhangel.dim.core.net.ProtocolException;

/**
 * Клиент для тестирования серверного приложения
 */
public class Client implements ConnectionHandler {

    /**
     * Механизм логирования позволяет более гибко управлять записью данных в лог (консоль, файл и тд)
     * */
    static Logger log = LoggerFactory.getLogger(Client.class);

    /**
     * Протокол, хост и порт инициализируются из конфига
     *
     * */
    private Protocol protocol = new BinaryProtocol();
    private int port = 19000 ;
    private String host = "localhost";

    private ClientUser user = new ClientUser();
    /**
     * Тред "слушает" сокет на наличие входящих сообщений от сервера
     */
    private Thread socketThread;

    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private InputStream in;
    private boolean closed = false;

    private OutputStream out;

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
;
    public void initSocket() throws IOException {
        Socket socket = new Socket(host, port);
        in = socket.getInputStream();
        out = socket.getOutputStream();

        /**
         * Инициализируем поток-слушатель. Синтаксис лямбды скрывает создание анонимного класса Runnable
         */
        socketThread = new Thread(() -> {
            final byte[] buf = new byte[1024 * 64];
            log.info("Starting listener thread...");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Здесь поток блокируется на ожидании данных
                    int read = in.read(buf);
                    if (read > 0) {

                        // По сети передается поток байт, его нужно раскодировать с помощью протокола
                        Message msg = protocol.decode(Arrays.copyOf(buf, read));
                        onMessage(msg);
                    }
                } catch (Exception e) {
                    log.error("Failed to process connection: {}", e);
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        });

        socketThread.start();
    }

    /**
     * Реагируем на входящее сообщение
     */
    @Override
    public void onMessage(Message msg) {
        log.info("Message received: {" + msg.toString() + "}");
        if (msg.getType() == Type.MSG_USER_CREATE_RESULT) {
            UserCreateAnswerMessage status = (UserCreateAnswerMessage) msg;
            if (status.getUsername() != null) {
                user.login(status.getId(), status.getUsername());
                log.info("Successfully logged in with id = " + user.getId());
            } else {
                log.info(status.getText());
            }
        } else if (msg.getType() == Type.MSG_LOGIN_RESULT){
            LoginAnswerMessage status = (LoginAnswerMessage) msg;
            if (status.getUsername() != null) {
                user.login(status.getId(), status.getUsername());
                log.info("Successfully logged in with id = " + user.getId());
            } else {
                log.info(status.getText());
            }
        }
    }

    /**
     * Обрабатывает входящую строку, полученную с консоли
     * Формат строки можно посмотреть в вики проекта
     */
    public void processInput(String line) throws IOException, ProtocolException {
        String[] tokens = line.split(" ");
        log.info("Tokens: {}", Arrays.toString(tokens));
        String cmdType = tokens[0];
        switch (cmdType) {
            case "/login":
                if (tokens.length < 3) {
                    log.error("Wrong parameters of login request");
                } else {
                    log.info(String.format("Executing login request with parameter: [username=%s]", tokens[1]));
                    LoginMessage loginMessage = new LoginMessage(tokens[1], tokens[2]);
                    send(loginMessage);
                }
                break;
            case "/help":
                System.out.println("login: /login <login> <password>");
                System.out.println("help: /help");
                System.out.println("get info about user: /info <id>");
                System.out.println("get list of your chats: /chat_list");
                System.out.println("create new chat with users with specified id list: /chat_create <id1> <id2> ...");
                System.out.println("a list of messages from the specified chat: /chat_history <chat_id>");
                System.out.println("send a message to the specified chat, chat must be in the list of user chats: /text <id> <message>");
                break;
            case "/text":
                if (tokens.length < 3) {
                    log.error("Wrong parameters of sending text message request");
                } else if (!user.isLoginned()) {
                    log.error("You are not loginned");
                } else {
                    StringBuilder str = new StringBuilder();
                    str.append(tokens[2]);
                    for (int i = 3; i < tokens.length; ++i) {
                        str.append(" ");
                        str.append(tokens[i]);
                    }
                    log.info("Sending message with text: " + str.toString());
                    TextMessage textMessage = new TextMessage(Long.parseLong(tokens[1]), user.getId(), str.toString(), LocalDateTime.now());
                    send(textMessage);
                }
                break;
            case "/info":
                if (tokens.length < 2) {
                    if (!user.isLoginned()) {
                        log.error("You are not loginned");
                    } else {
                        log.info("Executing info request about me:)");
                        InfoMessage infoMessage = new InfoMessage(user.getId());
                        infoMessage.setSenderId(user.getId());
                        send(infoMessage);
                    }
                } else {
                    log.info("Executing info request with parameter: [id="+ Long.parseLong(tokens[1])+"]");
                    InfoMessage infoMessage = new InfoMessage(Long.parseLong(tokens[1]));
                    infoMessage.setSenderId(user.getId());
                    send(infoMessage);
                }
                break;
            case "/chat_list":
                if (!user.isLoginned()) {
                    log.error("You are not loginned");
                } else {
                    log.info("Executing info request about my chats:)");
                    ChatListMessage chatListMessage = new ChatListMessage(user.getId());
                    chatListMessage.setSenderId(user.getId());
                    send(chatListMessage);
                }
                break;
            case "/chat_history":
                if (tokens.length < 2) {
                    log.error("Wrong parameters of chat history request");
                } else if (!user.isLoginned()) {
                    log.error("You are not loginned");
                } else {
                    log.info("Executing info request about chat:" + tokens[1]);
                    ChatHistoryMessage  chatHistoryMessage = new ChatHistoryMessage(Long.parseLong(tokens[1]));
                    chatHistoryMessage.setSenderId(user.getId());
                    send(chatHistoryMessage);
                }
                break;
            case "/chat_create":
                if (tokens.length < 2) {
                    log.error("Wrong parameters of chat create request");
                } else if (!user.isLoginned()) {
                    log.error("You are not loginned");
                } else {
                    List<Long> userIds = new LinkedList<>();
                    for (int i = 1; i < tokens.length; ++i) {
                        userIds.add(Long.parseLong(tokens[i]));
                    }
                    log.info("Creating new chat");
                    ChatCreateMessage chatCreateMessage = new ChatCreateMessage(userIds);
                    chatCreateMessage.setSenderId(user.getId());
                    send(chatCreateMessage);
                }
                break;
            case "/user_create":
                if (tokens.length < 3) {
                    log.error("Not enough arguments");
                }
                String username = tokens[1];
                String password = tokens[2];
                UserCreateMessage userCreateMessage = new UserCreateMessage(username, password);
                userCreateMessage.setType(Type.MSG_USER_CREATE);
                send(userCreateMessage);
                break;
            default:
                log.error("Invalid input: " + line);
        }
    }

    /**
     * Отправка сообщения в сокет клиент -> сервер
     */
    @Override
    public void send(Message msg) throws IOException, ProtocolException {
        log.info(msg.toString());
        if (!closed) {
            out.write(protocol.encode(msg));
        }
        out.flush(); // принудительно проталкиваем буфер с данными
    }

    @Override
    public void close() {
        socketThread.interrupt();
        if (!closed) {
            closed = true;
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {

        Client client = new Client();

        try {
            client.initSocket();
            // Цикл чтения с консоли
            Scanner scanner = new Scanner(System.in);
            System.out.println("$");
            while (!Thread.currentThread().isInterrupted()) {
                String input = scanner.nextLine();
                if ("q".equals(input)) {
                    return;
                }
                try {
                    client.processInput(input);
                } catch (ProtocolException | IOException e) {
                    log.error("Failed to process user input", e);
                }
            }
        } catch (Exception e) {
            log.error("Application failed.", e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

}
