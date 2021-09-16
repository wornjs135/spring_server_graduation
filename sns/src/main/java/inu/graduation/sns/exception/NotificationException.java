package inu.graduation.sns.exception;

public class NotificationException extends RuntimeException{
    public NotificationException() {
        super();
    }

    public NotificationException(String message) {
        super(message);
    }

    public NotificationException(Throwable cause) {
        super(cause);
    }
}
