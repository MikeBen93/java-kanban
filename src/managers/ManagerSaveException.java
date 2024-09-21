package managers;

public class ManagerSaveException extends RuntimeException{
    public ManagerSaveException(String message) {
        super(message);
    }

    public String getDetailMessage() {
        return getMessage();
    }
}
