package customer_api.exception;

public class CustomerDuplicateException extends RuntimeException {
    public CustomerDuplicateException(String message) {
        super(message);
    }
}

