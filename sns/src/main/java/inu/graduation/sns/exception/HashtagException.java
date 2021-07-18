package inu.graduation.sns.exception;

public class HashtagException extends RuntimeException{
    public HashtagException() {
        super();
    }

    public HashtagException(String message) {
        super(message);
    }

    public HashtagException(Throwable cause) {
        super(cause);
    }
}
