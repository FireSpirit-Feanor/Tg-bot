package handlers;

public interface CommandHandler {
    String getCommandName();
    void execute();
}