package ru.transfer.exception;

/**
 * Created by Bucky on 15.05.2018.
 */
public class SameAccountException extends RuntimeException {

    public SameAccountException(String msg) {
        super(msg);
    }
}
