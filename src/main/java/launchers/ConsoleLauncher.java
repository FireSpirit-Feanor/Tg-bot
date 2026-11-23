package launchers;

import bot.BookBot;
import console.ConsoleReader;
import console.ConsoleWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleLauncher {

    private static final Logger log = LoggerFactory.getLogger(ConsoleLauncher.class);

    public static void main(String[] args) {
        log.info("Запуск консольного бота");
        new BookBot(new ConsoleReader(), new ConsoleWriter()).start();
        log.info("Консольный бот завершил работу");
    }
}