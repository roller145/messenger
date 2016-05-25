package arhangel.dim.lections.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static arhangel.dim.lections.socket.nio.ChangeRequest.CHANGEOPS;
import static arhangel.dim.lections.socket.nio.NioClient.ADDRESS;
import static arhangel.dim.lections.socket.nio.NioClient.PORT;
import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;

public class NioServer {
    private Selector selector;
    private ByteBuffer readBuffer = allocate(8192);
    private EchoWorker worker = new EchoWorker(); //поток, который отправляет полученные сообщения на запись
    private final List<ChangeRequest> changeRequests = new LinkedList<>();
    private final Map<SocketChannel, List<ByteBuffer>> pendingData = new HashMap<>();

    private NioServer() throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open(); // ждёт подключения
        serverChannel.configureBlocking(false);//неблокирующий
        serverChannel.socket().bind(new InetSocketAddress(ADDRESS, PORT)); //слушай этот адрес
        selector = SelectorProvider.provider().openSelector();
        serverChannel.register(selector, OP_ACCEPT);//сопоставляем селектор на принятие подключения каналу
        new Thread(worker).start();//включаем эхо-воркера
    }

    public static void main(String[] args) throws IOException {
        new NioServer().run();
    }

    private void run() throws IOException {
        while (true) {
            synchronized (changeRequests) { //критическая секция запросов на изменение состояния
                for (ChangeRequest change : changeRequests) {
                    switch (change.getType()) {
                        case CHANGEOPS: //change operation selector
                            SelectionKey key = change.getRightSelectionKey();
                            break;
                        default:
                    }
                }
                changeRequests.clear(); //модель: слушаем - отправляем
            }
            selector.select();//блокируемся, ждём подключения
            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
            while (selectedKeys.hasNext()) {
                SelectionKey key = selectedKeys.next(); //грабли
                selectedKeys.remove();
                if (!key.isValid()) {
                    continue;
                }
                if (key.isAcceptable()) {
                    accept(key);
                } else if (key.isReadable()) {
                    read(key);
                } else if (key.isWritable()) {
                    write(key);
                }
            }
        }
    }

    void send(SocketChannel socket, byte[] data) {
        synchronized (changeRequests) {
            changeRequests.add(new ChangeRequest(socket, CHANGEOPS, OP_WRITE, selector));
            synchronized (pendingData) { //создаётся очередь в очереди на отправку по каналам в общеканальной очереди
                List<ByteBuffer> queue = pendingData.get(socket);
                if (queue == null) {
                    queue = new ArrayList<>();
                    pendingData.put(socket, queue);
                }
                queue.add(ByteBuffer.wrap(data));
            }
        }
        selector.wakeup();//проснись!
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();//создался новый каналб достаём его
        SocketChannel socketChannel = serverSocketChannel.accept();//принимаем канал
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, OP_READ);//регистрируем
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        readBuffer.clear(); //чистим буфер
        int numRead = socketChannel.read(readBuffer); //количество считанных байт
        worker.processData(this, socketChannel, readBuffer.array(), numRead);
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        synchronized (pendingData) {
            List<ByteBuffer> queue = pendingData.get(socketChannel);
            while (!queue.isEmpty()) {
                ByteBuffer buf = queue.get(0);
                socketChannel.write(buf);
                if (buf.remaining() > 0) {
                    break;
                }
                System.out.println("Send echo = " + new String(queue.get(0).array()));
                queue.remove(0);
            }
            if (queue.isEmpty()) {
                key.interestOps(OP_READ);
            }
        }
    }
}
