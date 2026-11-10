package launchers;

import bot.BookBot;
import console.ConsoleReader;
import console.ConsoleWriter;

public class ConsoleLauncher {
    public static void main(String[] args) {
        new BookBot(new ConsoleReader(), new ConsoleWriter()).start();
    }
}