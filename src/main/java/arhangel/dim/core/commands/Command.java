package arhangel.dim.core.commands;

import arhangel.dim.core.messages.Message;
import arhangel.dim.core.net.ProtocolException;
import arhangel.dim.core.net.Session;

import java.io.IOException;

// TODO: на каждое сообщение завести обработчик-команду
public interface Command {

    /**
     * Реализация паттерна Команда. Метод execute() вызывает соответствующую реализацию,
     * для запуска команды нужна сессия, чтобы можно было сгенерить ответ клиенту и провести валидацию
     * сессии.
     * @param session - текущая сессия
     * @param message - сообщение для обработки
     * @throws CommandException - все исключения перебрасываются как CommandException
     */
    void execute(Session session, Message message) throws CommandException, ProtocolException, IOException;
}