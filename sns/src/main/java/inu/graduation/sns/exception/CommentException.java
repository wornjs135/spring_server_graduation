package inu.graduation.sns.exception;

public class CommentException extends RuntimeException{
    public CommentException() {
        super();
    }

    public CommentException(String message) {
        super(message);
    }

    public CommentException(Throwable cause) {
        super(cause);
    }
}
