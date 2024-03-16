package shell;

public class ShellException extends Exception {
    
    public ShellException(Exception cause) { super(cause); }

    public ShellException(String message) {
        super(message);
    }
}
