package arhangel.dim.lections.socket.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.SelectionKey.OP_CONNECT;
import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;

/**
 *
 */
public class NioClient {
    static final int PORT = 9090;
    static final String ADDRESS = "localhost";
    private ByteBuffer buffer = allocate(16);

    private void run() throws Exception {
        SocketChannel channel = SocketChannel.open();//создаём канал для работы с сетью, ничего не открываем
        channel.configureBlocking(false); //неблокирующий канал
        Selector selector = Selector.open(); //создаём пустой канал
        channel.register(selector, OP_CONNECT); //связываем канал с селектором, говорим,что будем ждать подключения
        channel.connect(new InetSocketAddress(ADDRESS, PORT)); //подключаем канал с сетью, слушаем подключения
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(2); //создаём блокирующую очередь-помощник
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                if ("q".equals(line)) {
                    System.exit(0);
                }
                try {
                    queue.put(line);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SelectionKey key = channel.keyFor(selector);//канал, создай событие на селекторе
                key.interestOps(OP_WRITE);
                selector.wakeup();//проснись
            }
        }).start(); //поток для считывания из консоли, по команде q - выходит из клиента топорным exit(),
        // складывает таски для обработки в блокирующую очередь
        while (true) {
            selector.select(); //мы блокируемся
            for (SelectionKey selectionKey : selector.selectedKeys()) { //произошло событие
                if (selectionKey.isConnectable()) { //подключение-> закончи процесс подключения, подключись на запись
                    channel.finishConnect();
                    selectionKey.interestOps(OP_WRITE);
                } else if (selectionKey.isReadable()) {//прочесть-> чистим буфер, канал читает данные из буфера
                    buffer.clear();
                    channel.read(buffer);
                    System.out.println("Recieved = " + new String(buffer.array()));
                } else if (selectionKey.isWritable()) {//записать->забираем из очереди, пишем в канал, если не пустая,
                    // ставим на ожидание чтения
                    String line = queue.poll();
                    if (line != null) {
                        channel.write(ByteBuffer.wrap(line.getBytes()));
                    }
                    selectionKey.interestOps(OP_READ);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new NioClient().run();
    }
}
