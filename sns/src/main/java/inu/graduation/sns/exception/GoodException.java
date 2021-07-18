package inu.graduation.sns.exception;

public class GoodException extends RuntimeException{
    public GoodException() {
        super();
    }

    public GoodException(String message) {
        super(message);
    }

    public GoodException(Throwable cause) {
        super(cause);
    }
}
