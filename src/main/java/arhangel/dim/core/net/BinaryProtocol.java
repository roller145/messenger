package arhangel.dim.core.net;

import arhangel.dim.core.messages.Message;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import java.io.*;

/**
 * TODO: реализовать здесь свой протокол
 */
public class BinaryProtocol implements Protocol {
    static Logger loger = (Logger) LoggerFactory.getLogger(BinaryProtocol.class);

    @Override
    public Message decode(byte[] bytes) throws ProtocolException {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(bytes))) {
                return (Message) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                loger.error( "failed to decode");
                throw new ProtocolException("Failed to decode message", e);
            }
    }

    @Override
    public byte[] encode(Message msg) throws ProtocolException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(msg);
            return baos.toByteArray();
        } catch (IOException e) {
            loger.error("failed to encode");
            throw new ProtocolException("Failed to encode message", e);
        }
    }
}
