package console;

import java.util.Scanner;

public class ConsoleReader implements BotReader {

    private final Scanner scanner = new Scanner(System.in);
    
    @Override
    public String readInput() {
        return scanner.nextLine();
    }
}