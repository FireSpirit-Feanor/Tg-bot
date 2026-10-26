package console;

import interfaces.BotReader;
import java.util.Scanner;

public class ConsoleReader implements BotReader {
    
    private Scanner scanner;
    
    public ConsoleReader() {
        this.scanner = new Scanner(System.in);
    }
    
    @Override
    public String readInput() {
        return scanner.nextLine();
    }
}