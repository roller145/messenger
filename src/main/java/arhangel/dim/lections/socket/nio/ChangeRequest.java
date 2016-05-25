package arhangel.dim.lections.socket.nio;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 *
 */
class ChangeRequest {
    static final int CHANGEOPS = 2;

    private SocketChannel channel;
    private int type;
    private int ops;
    private Selector selector;

    ChangeRequest(SocketChannel channel, int type, int ops, Selector selector) {
        this.channel = channel;
        this.type = type;
        this.ops = ops;
        this.selector = selector;
    }

    SelectionKey getRightSelectionKey() {
        SelectionKey key = this.channel.keyFor(selector); //достали канал из запроса и порождаем новый event
        key.interestOps(this.ops); //меняем его состояние
        return key;
    }

    public int getType() {
        return type;
    }
}
